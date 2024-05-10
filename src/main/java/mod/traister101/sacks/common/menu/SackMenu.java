package mod.traister101.sacks.common.menu;

import mod.traister101.sacks.common.capability.ExtendedSlotCapacityHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.EnumSet;
import java.util.Set;

public class SackMenu extends AbstractContainerMenu {

	private static final Set<ClickType> ILLEGAL_ITEM_CLICKS = EnumSet.of(ClickType.QUICK_MOVE, ClickType.PICKUP, ClickType.THROW, ClickType.SWAP);
	protected final Player player;
	protected final InteractionHand hand;
	/**
	 * Index in the hotbar. Between [0, 9), or -1 if this is the offhand
	 */
	protected final int heldItemIndex;
	protected final IItemHandler handler;
	/**
	 * The amount of slots this container has
	 */
	protected final int containerSlots;
	/**
	 * Index into the slot for the hotbar slot. Hotbar is at the end of the inventory.
	 */
	protected int itemIndex;

	public SackMenu(final int windowId, final Inventory inventory, final IItemHandler handler, final InteractionHand hand, final int hotbarSlot) {
		super(SNSMenus.SACK_MENU.get(), windowId);
		this.player = inventory.player;
		this.hand = hand;
		this.heldItemIndex = hotbarSlot;
		this.handler = handler;
		this.containerSlots = this.handler.getSlots();

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

		return new SackMenu(windowId, inventory, new ExtendedSlotCapacityHandler(byteBuf.readInt(), byteBuf.readInt()), hand,
				hand == InteractionHand.OFF_HAND ? -1 : inventory.selected);
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
	public void clicked(final int slotIndex, final int dragType, final ClickType clickType, final Player player) {
		// We can't move if:
		// the slot is the item index, and it's an illegal action (like, swapping the items)
		// the hotbar item is being swapped out
		// the action is "pickup all" (this ignores every slot, so we cannot allow it)
		if (slotIndex == itemIndex && ILLEGAL_ITEM_CLICKS.contains(clickType)) return;
		if (dragType == heldItemIndex && clickType == ClickType.SWAP) return;
		if (dragType == 40 && clickType == ClickType.SWAP && hand == InteractionHand.OFF_HAND) return;
		if (clickType == ClickType.PICKUP_ALL) return;

		// TODO re-impliment this from 1.12
		//  Yeah... it's still needed :|
		super.clicked(slotIndex, dragType, clickType, player);
	}

	@Override
	public boolean stillValid(final Player player) {
		return !(hand == InteractionHand.MAIN_HAND ? slots.get(itemIndex).getItem() : this.player.getOffhandItem()).isEmpty();
	}

	@Override
	protected boolean moveItemStackTo(final ItemStack movedStack, final int startIndex, final int endIndex, final boolean reverseDirection) {
		boolean haveMovedStack = false;
		// Start iterating from the end if we merge in reverse order
		int slotIndex = reverseDirection ? endIndex - 1 : startIndex;

		if (movedStack.isStackable()) {
			while (!movedStack.isEmpty()) {
				if (reverseDirection) {
					if (slotIndex < startIndex) break;
				} else if (slotIndex >= endIndex) break;

				final Slot slot = this.slots.get(slotIndex);
				final ItemStack slotStack = slot.getItem();

				// Can't merge these stacks
				if (!ItemStack.isSameItemSameTags(movedStack, slotStack)) {
					slotIndex += (reverseDirection) ? -1 : 1;
					continue;
				}

				final int total = slotStack.getCount() + movedStack.getCount();

				final int maxSize;
				// If it's our container slots use the slot limit to determine the max stack size
				if (slotIndex < containerSlots) {
					maxSize = slot.getMaxStackSize();
				} else maxSize = Math.min(slot.getMaxStackSize(), movedStack.getMaxStackSize());

				// Can fully consume the merge stack
				if (maxSize >= total) {
					movedStack.setCount(0);
					slotStack.setCount(total);
					slot.setChanged();
					haveMovedStack = true;
					slotIndex += (reverseDirection) ? -1 : 1;
					continue;
				}

				// Can only partially consume the stack
				if (maxSize > slotStack.getCount()) {
					movedStack.shrink(maxSize - slotStack.getCount());
					slotStack.grow(maxSize - slotStack.getCount());
					slot.setChanged();
					haveMovedStack = true;
					slotIndex += (reverseDirection) ? -1 : 1;
					continue;
				}
				slotIndex += (reverseDirection) ? -1 : 1;
			}
		}

		// Try and fill empty slots now
		if (!movedStack.isEmpty()) {
			if (reverseDirection) {
				slotIndex = endIndex - 1;
			} else slotIndex = startIndex;

			while (true) {
				if (reverseDirection) {
					if (slotIndex < startIndex) break;
				} else if (slotIndex >= endIndex) break;

				final Slot slot = this.slots.get(slotIndex);

				// Continue early if we can't put anything in this slot
				if (slot.hasItem() || !slot.mayPlace(movedStack)) {
					slotIndex += (reverseDirection) ? -1 : 1;
					continue;
				}

				// If it's our container slots use the slots stack cap
				if (slotIndex < containerSlots) {
					slot.setByPlayer(movedStack.split(slot.getMaxStackSize()));
					haveMovedStack = true;
					slotIndex += (reverseDirection) ? -1 : 1;
					continue;
				}

				{
					final int splitSize = Math.min(slot.getMaxStackSize(movedStack), movedStack.getMaxStackSize());
					// Can merge
					if (movedStack.getCount() > splitSize) {
						slot.setByPlayer(movedStack.split(splitSize));
						haveMovedStack = true;
						slotIndex += (reverseDirection) ? -1 : 1;
						continue;
					}
				}

				// Put the whole stack in the slot
				slot.setByPlayer(movedStack.split(movedStack.getCount()));
				haveMovedStack = true;
				slotIndex += (reverseDirection) ? -1 : 1;
			}
		}
		return haveMovedStack;
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
				final int rows = (int) Math.ceil((double) containerSlots / 9);
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
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				addSlot(new SlotItemHandler(handler, column + row * columns, startX + column * 18, startY + row * 18));
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
		for (int row = 0; row < rows; row++) {
			if (row == rows - 1) {
				for (int column = 0; column < columns; column++) {
					addSlot(new SlotItemHandler(handler, column + row * columns, 8 + column * 18, 27 + row * 18));
				}
			} else {
				for (int j = 0; j < 9; j++) {
					addSlot(new SlotItemHandler(handler, j + row * columns, 8 + j * 18, 27 + row * 18));
				}
			}
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