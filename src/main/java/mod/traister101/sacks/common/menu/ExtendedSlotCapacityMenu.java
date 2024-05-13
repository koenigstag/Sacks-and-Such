package mod.traister101.sacks.common.menu;

import mod.traister101.sacks.common.capability.ExtendedSlotCapacityHandler;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

/**
 * This is a bare-bones {@link Slot} agnostic Menu for slots which can exeede {@value Container#LARGE_MAX_STACK_SIZE} items.
 * If you wish to use this Menu you need someathing like {@link ExtendedSlotCapacityHandler} in order to correctly store
 * and serialize the stack sizes as vanilla serialiazation caps out at {@value Byte#MAX_VALUE}
 * <p>
 * The container slots must support extended counts via {@link Slot#getMaxStackSize()} and {@link Slot#getMaxStackSize(ItemStack)}
 * We provide {@link ExtendedSlotItemHandler} as the forge provided {@link SlotItemHandler} clamps the max stack size to the
 * passed in stacks {@link ItemStack#getMaxStackSize()} which in practice means the item must be able to stack up to more than
 * the typical max stack size of 64.
 */
public abstract class ExtendedSlotCapacityMenu extends AbstractContainerMenu {

	/**
	 * The amount of slots this container has
	 */
	protected final int containerSlots;

	protected ExtendedSlotCapacityMenu(final MenuType<? extends ExtendedSlotCapacityMenu> menuType, final int windowId, final int containerSlots) {
		super(menuType, windowId);
		this.containerSlots = containerSlots;
	}

	@Override
	public abstract ItemStack quickMoveStack(final Player player, final int slotIndex);

	@Override
	public void clicked(final int slotIndex, final int mouseButtom, final ClickType clickType, final Player player) {
		// Not a slot
		if (0 > slotIndex) {
			if (slotIndex != SLOT_CLICKED_OUTSIDE) return;
			if (clickType != ClickType.PICKUP && clickType != ClickType.QUICK_MOVE) return;
			if (mouseButtom != 0 && mouseButtom != 1) return;
			if (this.getCarried().isEmpty()) return;

			final ClickAction clickAction = mouseButtom == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
			if (clickAction != ClickAction.PRIMARY) {
				player.drop(this.getCarried().split(1), true);
				return;
			}

			player.drop(this.getCarried(), true);
			this.setCarried(ItemStack.EMPTY);
			return;
		}

		final Inventory inventory = player.getInventory();
		if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && (mouseButtom == 0 || mouseButtom == 1)) {
			clickPickup(slotIndex, mouseButtom, clickType, player);
			return;
		}

		if (clickType == ClickType.SWAP) {
			clickSwap(slotIndex, mouseButtom, player, inventory);
			return;
		}

		if (clickType == ClickType.CLONE && player.getAbilities().instabuild && this.getCarried().isEmpty()) {
			final Slot slot = this.slots.get(slotIndex);
			if (slot.hasItem()) {
				final ItemStack slotStack = slot.getItem();
				this.setCarried(slotStack.copyWithCount(slotStack.getMaxStackSize()));
			}
			return;
		}

		if (clickType == ClickType.THROW && this.getCarried().isEmpty()) {
			final Slot slot = this.slots.get(slotIndex);
			final int stackCount = mouseButtom == 0 ? 1 : slot.getItem().getMaxStackSize();
			final ItemStack dropStack = slot.safeTake(stackCount, Integer.MAX_VALUE, player);
			player.drop(dropStack, true);
			return;
		}

		if (clickType == ClickType.PICKUP_ALL) {
			final Slot slot = this.slots.get(slotIndex);
			final ItemStack carriedStack = this.getCarried();
			if (!carriedStack.isEmpty() && (!slot.hasItem() || !slot.mayPickup(player))) {
				final int l1 = mouseButtom == 0 ? 0 : this.slots.size() - 1;
				final int k2 = mouseButtom == 0 ? 1 : -1;

				for (int l2 = 0; l2 < 2; ++l2) {
					for (int l3 = l1; l3 >= 0 && l3 < this.slots.size() && carriedStack.getCount() < carriedStack.getMaxStackSize(); l3 += k2) {
						final Slot loopSlot = this.slots.get(l3);

						if (!loopSlot.hasItem()) continue;
						if (!canItemQuickReplace(loopSlot, carriedStack, true)) continue;
						if (!loopSlot.mayPickup(player)) continue;
						if (!canTakeItemForPickAll(carriedStack, loopSlot)) continue;

						final ItemStack loopStack = loopSlot.getItem();
						if (l2 == 0 && loopStack.getCount() == loopStack.getMaxStackSize()) continue;
						final ItemStack resultStack = loopSlot.safeTake(loopStack.getCount(),
								carriedStack.getMaxStackSize() - carriedStack.getCount(), player);
						carriedStack.grow(resultStack.getCount());
					}
				}
			}
		}
	}

	@Override
	public abstract boolean stillValid(final Player player);

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

	protected void clickPickup(final int slotIndex, final int mouseButtom, final ClickType clickType, final Player player) {
		final ClickAction clickAction = mouseButtom == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
		if (clickType == ClickType.QUICK_MOVE) {

			final Slot slot = this.slots.get(slotIndex);
			if (!slot.mayPickup(player)) {
				return;
			}

			ItemStack moveStack = this.quickMoveStack(player, slotIndex);
			while (!moveStack.isEmpty() && ItemStack.isSameItem(slot.getItem(), moveStack)) {
				moveStack = this.quickMoveStack(player, slotIndex);
			}
			return;
		}

		final Slot slot = this.slots.get(slotIndex);
		final ItemStack slotStack = slot.getItem();
		final ItemStack carriedStack = this.getCarried();
		player.updateTutorialInventoryAction(carriedStack, slot.getItem(), clickAction);
		if (tryItemClickBehaviourOverride(player, clickAction, slot, slotStack, carriedStack)) return;

		if (ForgeHooks.onItemStackedOn(slotStack, carriedStack, slot, clickAction, player, createCarriedSlotAccess())) return;

		if (slotStack.isEmpty()) {
			if (carriedStack.isEmpty()) return;

			final int insertCount = clickAction == ClickAction.PRIMARY ? carriedStack.getCount() : 1;
			this.setCarried(slot.safeInsert(carriedStack, insertCount));
			slot.setChanged();
			return;
		}

		if (!slot.mayPickup(player)) return;

		// Not holding anything
		if (carriedStack.isEmpty()) {
			// How much we should extract
			final int extractAmount;
			if (clickAction == ClickAction.PRIMARY) {
				extractAmount = slotStack.getCount();
			} else {
				extractAmount = (Math.min(slotStack.getCount(), slotStack.getMaxStackSize()) + 1) / 2;
			}
			slot.tryRemove(extractAmount, Integer.MAX_VALUE, player).ifPresent((stack) -> {
				this.setCarried(stack);
				slot.onTake(player, stack);
			});
			slot.setChanged();
			return;
		}

		if (slot.mayPlace(carriedStack)) {
			if (ItemStack.isSameItemSameTags(slotStack, carriedStack)) {
				final int insertAmount = clickAction == ClickAction.PRIMARY ? carriedStack.getCount() : 1;
				this.setCarried(slot.safeInsert(carriedStack, insertAmount));
				slot.setChanged();
				return;
			}

			if (carriedStack.getCount() <= slot.getMaxStackSize(carriedStack)) {
				this.setCarried(slotStack);
				slot.setByPlayer(carriedStack);
				slot.setChanged();
				return;
			}
		}

		if (ItemStack.isSameItemSameTags(slotStack, carriedStack)) {
			slot.tryRemove(slotStack.getCount(), carriedStack.getMaxStackSize() - carriedStack.getCount(), player).ifPresent((p_150428_) -> {
				carriedStack.grow(p_150428_.getCount());
				slot.onTake(player, p_150428_);
			});
		}
		slot.setChanged();
	}

	protected void clickSwap(final int slotIndex, final int mouseButtom, final Player player, final Inventory inventory) {
		final Slot slot = this.slots.get(slotIndex);
		final ItemStack itemStack = inventory.getItem(mouseButtom);
		final ItemStack slotStack = slot.getItem();
		if (itemStack.isEmpty() && slotStack.isEmpty()) return;

		if (itemStack.isEmpty()) {
			if (!slot.mayPickup(player)) return;

			inventory.setItem(mouseButtom, slotStack);
			// I think we don't have to worry about crafting...
			//slot.onSwapCraft(slotStack.getCount());
			slot.setByPlayer(ItemStack.EMPTY);
			slot.onTake(player, slotStack);
			return;
		}

		if (slotStack.isEmpty()) {
			if (!slot.mayPlace(itemStack)) return;

			final int maxStackSize = slot.getMaxStackSize(itemStack);

			if (maxStackSize >= itemStack.getCount()) {
				inventory.setItem(mouseButtom, ItemStack.EMPTY);
				slot.setByPlayer(itemStack);
				return;
			}

			slot.setByPlayer(itemStack.split(maxStackSize));
			return;
		}
		if (slot.mayPickup(player) && slot.mayPlace(itemStack)) {
			final int maxStackSize = slot.getMaxStackSize(itemStack);
			if (itemStack.getCount() <= maxStackSize) {
				inventory.setItem(mouseButtom, slotStack);
				slot.setByPlayer(itemStack);
				slot.onTake(player, slotStack);
				return;
			}

			slot.setByPlayer(itemStack.split(maxStackSize));
			slot.onTake(player, slotStack);
			if (!inventory.add(slotStack)) {
				player.drop(slotStack, true);
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

	/**
	 * Same as Forges {@link SlotItemHandler} but with an overriden {@link Slot#getMaxStackSize(ItemStack)} which respects the extended slot count
	 */
	public static class ExtendedSlotItemHandler extends SlotItemHandler {

		public ExtendedSlotItemHandler(final IItemHandler itemHandler, final int index, final int xPosition, final int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
		}

		@Override
		public int getMaxStackSize(final ItemStack itemStack) {
			final int maxInput = getMaxStackSize();
			final ItemStack maxAdd = itemStack.copyWithCount(maxInput);

			final IItemHandler handler = this.getItemHandler();
			final int slotIndex = getSlotIndex();
			final ItemStack currentStack = handler.getStackInSlot(slotIndex);

			if (handler instanceof final IItemHandlerModifiable handlerModifiable) {

				handlerModifiable.setStackInSlot(slotIndex, ItemStack.EMPTY);

				final ItemStack remainder = handlerModifiable.insertItem(slotIndex, maxAdd, true);

				handlerModifiable.setStackInSlot(slotIndex, currentStack);

				return maxInput - remainder.getCount();
			}

			final ItemStack remainder = handler.insertItem(slotIndex, maxAdd, true);

			final int current = currentStack.getCount();
			final int added = maxInput - remainder.getCount();
			return current + added;
		}
	}
}