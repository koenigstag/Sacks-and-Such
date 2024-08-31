package mod.traister101.sacks.common.items;

import net.dries007.tfc.common.capabilities.size.Size;

import mod.traister101.sacks.common.SNSItemTags;
import mod.traister101.sacks.common.capability.*;
import mod.traister101.sacks.common.capability.LazyCapabilityProvider.LazySerializedCapabilityProvider;
import mod.traister101.sacks.common.items.LunchBoxItem.LunchboxHandler;
import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.config.ServerConfig.ContainerConfig;
import mod.traister101.sacks.util.ContainerType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;

import org.jetbrains.annotations.Nullable;
import java.util.function.Function;

public final class DefaultContainers {

	public static final ContainerType STRAW_BASKET = new ContainerItemType<>("straw_basket", Size.NORMAL, SNSConfig.SERVER.strawBasket,
			GenericHandler::new, ForgeCapabilities.ITEM_HANDLER);

	public static final ContainerType LEATHER_SACK = new ContainerItemType<>("leather_sack", Size.NORMAL, SNSConfig.SERVER.leatherSack,
			GenericHandler::new, ForgeCapabilities.ITEM_HANDLER);

	public static final ContainerType BURLAP_SACK = new ContainerItemType<>("burlap_sack", Size.NORMAL, SNSConfig.SERVER.burlapSack,
			GenericHandler::new, ForgeCapabilities.ITEM_HANDLER);

	public static final ContainerType ORE_SACK = new ContainerItemType<>("ore_sack", Size.NORMAL, SNSConfig.SERVER.oreSack, OreSackHandler::new,
			ForgeCapabilities.ITEM_HANDLER);

	public static final ContainerType SEED_POUCH = new ContainerItemType<>("seed_pouch", Size.NORMAL, SNSConfig.SERVER.seedPouch,
			SeedPouchHandler::new, ForgeCapabilities.ITEM_HANDLER);

	public static final ContainerType FRAME_PACK = new ContainerItemType<>("frame_pack", Size.HUGE, SNSConfig.SERVER.framePack, FramePackHandler::new,
			ForgeCapabilities.ITEM_HANDLER);

	public static final ContainerType LUNCHBOX = new ContainerItemType<>("lunchbox", Size.NORMAL, SNSConfig.SERVER.lunchBox, LunchboxHandler::new,
			ForgeCapabilities.ITEM_HANDLER, LunchboxCapability.LUNCHBOX);

	private record ContainerItemType<Handler extends INBTSerializable<CompoundTag>>(String name,
			Size size,
			ContainerConfig containerConfig,
			Function<ContainerType, Handler> handlerFactory,
			Capability<? super Handler>... capabilities) implements ContainerType {

		@SafeVarargs
		@SuppressWarnings("varargs")
		private ContainerItemType {
		}

		@Override
		public int getSlotCount() {
			return containerConfig.slotCount.get();
		}

		@Override
		public int getSlotCapacity() {
			return containerConfig.slotCap.get();
		}

		@Override
		public boolean doesAutoPickup() {
			return containerConfig.doPickup.get();
		}

		@Override
		public boolean doesVoiding() {
			return containerConfig.doVoiding.get();
		}

		@Override
		public boolean doesInventoryInteraction() {
			return containerConfig.doInventoryTransfer.get();
		}

		@Override
		public Size getAllowedSize() {
			return containerConfig.allowedSize.get();
		}

		@Override
		public Size getSize(final ItemStack itemStack) {
			return size;
		}

		@Override
		public ICapabilityProvider getCapabilityProvider(final ItemStack itemStack, @Nullable final CompoundTag nbt) {
			// Must be lazy as stacks can be created before server config is initalized
			return new LazySerializedCapabilityProvider<>(() -> handlerFactory.apply(this), capabilities);
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	private static class GenericHandler extends ContainerItemHandler {

		public GenericHandler(final ContainerType type) {
			super(type);
		}

		@Override
		public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
			if (!SNSConfig.SERVER.allAllowFood.get() && itemStack.is(SNSItemTags.TFC_FOODS)) return false;

			if (!SNSConfig.SERVER.allAllowOre.get() && (itemStack.is(SNSItemTags.TFC_SMALL_ORE_PIECES) || itemStack.is(SNSItemTags.TFC_ORE_PIECES)))
				return false;

			return super.isItemValid(slotIndex, itemStack);
		}
	}

	private static class SeedPouchHandler extends ContainerItemHandler {

		public SeedPouchHandler(final ContainerType type) {
			super(type);
		}

		@Override
		public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
			if (!SNSConfig.SERVER.allAllowFood.get() && itemStack.is(SNSItemTags.TFC_FOODS)) return false;

			return itemStack.is(SNSItemTags.ALLOWED_IN_SEED_POUCH) && super.isItemValid(slotIndex, itemStack);
		}
	}

	private static class OreSackHandler extends ContainerItemHandler {

		public OreSackHandler(final ContainerType type) {
			super(type);
		}

		@Override
		public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
			if (!SNSConfig.SERVER.allAllowFood.get() && itemStack.is(SNSItemTags.TFC_FOODS)) return false;

			return itemStack.is(SNSItemTags.ALLOWED_IN_ORE_SACK) && super.isItemValid(slotIndex, itemStack);
		}
	}

	private static class FramePackHandler extends GenericHandler {

		public FramePackHandler(final ContainerType type) {
			super(type);
		}

		@Override
		public int getStackLimit(final int slotIndex, final ItemStack itemStack) {
			return itemStack.getMaxStackSize();
		}
	}
}