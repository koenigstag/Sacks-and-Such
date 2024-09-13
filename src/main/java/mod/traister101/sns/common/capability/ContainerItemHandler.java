package mod.traister101.sns.common.capability;

import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.util.ContainerType;
import mod.trasiter101.esc.common.capability.ExtendedSlotCapacityHandler;
import net.dries007.tfc.common.capabilities.size.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public class ContainerItemHandler extends ExtendedSlotCapacityHandler {

	public final ContainerType type;
	@Nullable
	private Weight cachedWeight;

	public ContainerItemHandler(final ContainerType type) {
		super(type.getSlotCount(), type.getSlotCapacity());
		this.type = type;
	}

	@Override
	public CompoundTag serializeNBT() {
		final CompoundTag compoundTag = super.serializeNBT();
		compoundTag.putByte("weight", (byte) (cachedWeight != null ? cachedWeight.ordinal() : -1));
		return compoundTag;
	}

	@Override
	public void deserializeNBT(final CompoundTag compoundTag) {
		super.deserializeNBT(compoundTag);
		final byte weight = compoundTag.getByte("weight");
		if (weight != -1) cachedWeight = Weight.valueOf(weight);
	}

	@Override
	public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
		if (itemStack.is(SNSItemTags.PREVENTED_IN_ITEM_CONTAINERS)) return false;

		return fitsInSlot(itemStack);
	}

	@Override
	protected void onContentsChanged(final int slotIndex) {
		// Invalidate our cached weight when any contents change
		this.cachedWeight = null;
		super.onContentsChanged(slotIndex);
	}

	/**
	 * @param itemStack The {@link ItemStack} to check
	 *
	 * @return If the provided {@link ItemStack} will fit inside our slots
	 */
	protected final boolean fitsInSlot(final ItemStack itemStack) {
		final IItemSize stackSize = ItemSizeManager.get(itemStack);
		final Size size = stackSize.getSize(itemStack);
		// Larger than the sacks slot size
		return size.isEqualOrSmallerThan(type.getAllowedSize());
	}

	/**
	 * @return The weight of the sack
	 */
	public Weight getWeight() {
		if (cachedWeight != null) return cachedWeight;

		cachedWeight = fixedWeight();
		// cachedWeight = percentageBasedWeight();
	}

	private Weight fixedWeight() {
		int itemsCount = 0;

		for (int slotIndex = 0; slotIndex < getSlots(); slotIndex++) {
			final ItemStack itemStack = stacks.get(slotIndex);
			itemsCount += itemStack.getCount();
		}

		return itemsCount >= 1 ? Weight.VERY_HEAVY : Weight.VERY_LIGHT;
	}

	private Weight percentageBasedWeight() {
		int totalItems = 0, maxCapacity = 0;

		for (int slotIndex = 0; slotIndex < getSlots(); slotIndex++) {
			final ItemStack itemStack = stacks.get(slotIndex);
			totalItems += itemStack.getCount();
			maxCapacity += getStackLimit(slotIndex, itemStack);
		}

		final float amountFilled = (float) totalItems / (float) maxCapacity;

		// TODO Simple percentage based approuch, maybe not the best?
		if (0.80 <= amountFilled) {
			return Weight.VERY_HEAVY;
		}

		if (0.60 <= amountFilled) {
			return Weight.HEAVY;
		}

		if (0.40 <= amountFilled) {
			return Weight.MEDIUM;
		}

		if (0.20 <= amountFilled) {
			return Weight.LIGHT;
		}

		return Weight.VERY_LIGHT;
	}
}
