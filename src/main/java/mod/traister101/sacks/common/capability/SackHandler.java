package mod.traister101.sacks.common.capability;

import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mod.traister101.sacks.common.items.DefaultSacks;
import mod.traister101.sacks.common.items.SackItem;
import mod.traister101.sacks.util.SackType;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

// TODO needs more touch up
public class SackHandler extends ExtendedSlotCapacityHandler implements ICapabilityProvider {

	private final SackType type;
	private final LazyOptional<SackHandler> capability;

	public SackHandler(@Nullable final CompoundTag nbt, final SackType type) {
		super(type.getSlotCount(), type.getSlotCapacity());
		this.type = type;
		this.capability = LazyOptional.of(() -> this);
		if (nbt != null) {
			deserializeNBT(nbt);
		}
	}

	@Override
	public int getStackLimit(final int slotIndex, final ItemStack itemStack) {
		if (type == DefaultSacks.KNAPSACK) return itemStack.getMaxStackSize();
		return super.getStackLimit(slotIndex, itemStack);
	}

	// TODO more ore config
	@Override
	public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
		final Item item = itemStack.getItem();

		// Stack is a sack, no sack-ception
		if (item instanceof SackItem) return false;

//		if (type == SackTypes.FARMER_SACK) {
		// TODO Allow other than seeds
//			if (!ConfigSNS.SACK.FARMER_SACK.allowNonSeed) {
//				if (!(item instanceof ItemSeedsTFC)) return false;
//			}
//		}

		// TODO Food in every sack
//		if (!ConfigSNS.GLOBAL.allAllowFood) if (item instanceof ItemFoodTFC) return false;

//		if (type == SackTypes.MINER_SACK) {
		// TODO Allow other than ore
//			if (!ConfigSNS.SACK.MINER_SACK.allowNonOre) {
//				// TODO If item is a TFC ore
//				if (!(item instanceof ItemOreTFC || item instanceof ItemSmallOre)) return false;
//			}
//		}

		// TODO Ore for all sacks
//		if (!ConfigSNS.GLOBAL.allAllowOre)
//			if (type != SackTypes.MINER_SACK) if (item instanceof ItemOreTFC || item instanceof ItemSmallOre) return false;

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
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull final Capability<T> capability, @Nullable final Direction direction) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return this.capability.cast();
		}
		return LazyOptional.empty();
	}
}