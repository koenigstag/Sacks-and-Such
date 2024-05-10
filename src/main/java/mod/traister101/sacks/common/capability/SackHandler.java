package mod.traister101.sacks.common.capability;

import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import org.jetbrains.annotations.Nullable;

import mod.traister101.sacks.common.SNSTags;
import mod.traister101.sacks.common.SNSTags.Items;
import mod.traister101.sacks.common.items.DefaultSacks;
import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.util.SackType;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

// TODO needs more touch up
public class SackHandler extends ExtendedSlotCapacityHandler implements ICapabilityProvider {

	private final SackType type;
	private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> this);

	public SackHandler(@Nullable final CompoundTag nbt, final SackType type) {
		super(type.getSlotCount(), type.getSlotCapacity());
		this.type = type;
		if (nbt != null) {
			deserializeNBT(nbt);
		}
	}

	@Override
	public int getStackLimit(final int slotIndex, final ItemStack itemStack) {
		if (type == DefaultSacks.KNAPSACK) return itemStack.getMaxStackSize();
		return super.getStackLimit(slotIndex, itemStack);
	}

	@Override
	public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
		if (itemStack.is(SNSTags.Items.PREVENTED_IN_SACKS)) return false;

		if (type == DefaultSacks.FARMER_SACK && !itemStack.is(Items.ALLOWED_IN_FARMER_SACK)) return false;

		if (!SNSConfig.SERVER.allAllowFood.get() && itemStack.is(Items.TFC_FOODS)) return false;

		if (type == DefaultSacks.MINER_SACK && itemStack.is(Items.TFC_ORE)) return true;

		if (!SNSConfig.SERVER.allAllowOre.get() && type != DefaultSacks.MINER_SACK && itemStack.is(Items.TFC_ORE)) return false;

		final IItemSize stackSize = ItemSizeManager.get(itemStack);
		final Size size = stackSize.getSize(itemStack);
		// Larger than the sacks slot size
		return size.isEqualOrSmallerThan(type.getAllowedSize());
	}

	// TODO rethink if this is needed
	public boolean hasItems() {
		return stacks.stream().anyMatch(itemStack -> !itemStack.isEmpty());
	}

	@Override
	public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction direction) {
		return ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, this.holder);
	}
}