package mod.traister101.sacks.common.menu;

import mod.trasiter101.esc.common.capability.ExtendedSlotCapacityHandler;
import mod.trasiter101.esc.common.menu.ExtendedSlotCapacityMenu;
import mod.trasiter101.esc.common.slot.ExtendedSlotItemHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.*;

public class SackMenu extends ExtendedSlotCapacityMenu {

	private static final Set<ClickType> ILLEGAL_ITEM_CLICKS = EnumSet.of(ClickType.QUICK_MOVE, ClickType.PICKUP, ClickType.THROW, ClickType.SWAP);
	protected final Player player;
	protected final InteractionHand hand;
	/**
	 * Index in the hotbar. Between [0, 9), or -1 if this is the offhand
	 */
	protected final int heldItemIndex;
	protected final IItemHandler handler;
	/**
	 * Index into the slot for the hotbar slot. Hotbar is at the end of the inventory.
	 */
	protected int itemIndex;

	public SackMenu(final int windowId, final Inventory inventory, final IItemHandler handler, final InteractionHand hand, final int hotbarSlot) {
		super(SNSMenus.SACK_MENU.get(), windowId, handler.getSlots());
		this.player = inventory.player;
		this.hand = hand;
		this.heldItemIndex = hotbarSlot;
		this.handler = handler;

		if (this.hand == InteractionHand.MAIN_HAND) {
			this.itemIndex = containerSlots + heldItemIndex + 27;
		} else {
			this.itemIndex = -100;
		}

		this.addContainerSlots();
		this.addPlayerInventorySlots(inventory);
	}

	static SackMenu fromNetwork(final int windowId, final Inventory inventory, final FriendlyByteBuf byteBuf) {
		final InteractionHand hand = byteBuf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

		final ItemStack heldStack = inventory.player.getItemInHand(hand);
		final IItemHandler itemHandler = heldStack.getCapability(ForgeCapabilities.ITEM_HANDLER)
				.orElse(new ExtendedSlotCapacityHandler(byteBuf.readInt(), byteBuf.readInt()));
		return new SackMenu(windowId, inventory, itemHandler, hand, hand == InteractionHand.OFF_HAND ? -1 : inventory.selected);
	}

	@Override
	public ItemStack quickMoveStack(final Player player, final int slotIndex) {
		final Slot slot = slots.get(slotIndex);

		if (slot.hasItem()) {
			final ItemStack slotStack = slot.getItem();

			if (slotIndex < containerSlots) {
				if (!moveItemStackTo(slotStack, containerSlots, slots.size(), true)) return ItemStack.EMPTY;
			} else if (!moveItemStackTo(slotStack, 0, containerSlots, false)) return ItemStack.EMPTY;

			if (slotStack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else slot.setChanged();

			return slotStack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void clicked(final int slotIndex, final int mouseButtom, final ClickType clickType, final Player player) {
		// We can't move if:
		// the slot is the item index, and it's an illegal action (like, swapping the items)
		// the hotbar item is being swapped out
		// the action is "pickup all" (this ignores every slot, so we cannot allow it)
		if (slotIndex == itemIndex && ILLEGAL_ITEM_CLICKS.contains(clickType)) return;
		if (mouseButtom == heldItemIndex && clickType == ClickType.SWAP) return;
		if (mouseButtom == 40 && clickType == ClickType.SWAP && hand == InteractionHand.OFF_HAND) return;

		super.clicked(slotIndex, mouseButtom, clickType, player);
	}

	@Override
	public boolean stillValid(final Player player) {
		return !(hand == InteractionHand.MAIN_HAND ? slots.get(itemIndex).getItem() : this.player.getOffhandItem()).isEmpty();
	}

	/**
	 * Adds the slots for this container
	 */
	protected void addContainerSlots() {
		switch (containerSlots) {
			case 1 -> addSlots(1, 1, 80, 32);
			case 4 -> addSlots(2, 2, 71, 23);
			case 8 -> addSlots(2, 4, 53, 23);
			case 18 -> addSlots(2, 9, 8, 34);
			default -> {
				// We want to round up, integer math rounds down
				final int rows = Math.round((float) containerSlots / 9);
				final int columns = containerSlots / rows;
				addSlots(rows, columns);
			}
		}
	}

	/**
	 * Dynamically adds slots to the container depending on the amount of rows and columns.
	 *
	 * @param rows How many rows of slots
	 * @param columns How many columns of slots
	 * @param startX The X starting position
	 * @param startY The Y starting position
	 */
	private void addSlots(final int rows, final int columns, final int startX, final int startY) {
		assert rows != 0 : "Cannot have zero rows of slots";
		assert columns != 0 : "Cannot have zero columns of slots";

		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				final int yPosition = startY + row * 18;
				final int xPosition = startX + column * 18;
				final int index = column + row * columns;
				addSlot(new ExtendedSlotItemHandler(handler, index, xPosition, yPosition));
			}
		}
	}

	/**
	 * Dynamically adds slots to the container depending on the amount of rows and columns. Will start from the top left
	 *
	 * @param rows How many rows of slots
	 * @param columns How many columns of slots
	 */
	private void addSlots(final int rows, final int columns) {
		if (rows > 1) {
			addSlots(rows - 1, 9, 8, 18);
		}

		for (int column = 0; column < columns; column++) {
			final int yPosition = 18 * (rows - 1) + 18;
			final int xPosition = 8 + column * 18;
			final int index = column + (rows - 1) * columns;
			addSlot(new ExtendedSlotItemHandler(handler, index, xPosition, yPosition));
		}
	}

	/**
	 * Adds the player inventory slots to the container.
	 */
	protected final void addPlayerInventorySlots(final Inventory inventory) {
		// Main Inventory. Indexes [0, 27)
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		// Hotbar. Indexes [27, 36)
		for (int k = 0; k < 9; k++) {
			addSlot(new Slot(inventory, k, 8 + k * 18, 142));
		}
	}
}