package mod.traister101.sns.common.capability;

import mod.traister101.sns.common.SNSItemTags;
import mod.traister101.sns.util.ContainerType;
import mod.trasiter101.esc.common.capability.ExtendedSlotCapacityHandler;
import net.dries007.tfc.common.capabilities.size.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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
		if (weight != -1)
			cachedWeight = Weight.valueOf(weight);
	}

	@Override
	public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
		if (itemStack.is(SNSItemTags.PREVENTED_IN_ITEM_CONTAINERS))
			return false;

		return fitsInSlot(itemStack);
	}

	@Override
	protected void onContentsChanged(final int slotIndex) {
		if (isEmpty()) {
			this.cachedWeight = Weight.LIGHT;
		} else {
			this.cachedWeight = Weight.VERY_HEAVY; // causes overburden
		}

		super.onContentsChanged(slotIndex);
	}

	private boolean isEmpty() {
		for (int slotIndex = 0; slotIndex < getSlots(); slotIndex++) {
			if (!stacks.get(slotIndex).isEmpty()) {
				return false;
			}
		}
		return true;
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
		if (cachedWeight == null) return Weight.LIGHT;

		return cachedWeight;
	}
}
