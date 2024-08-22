package mod.traister101.sacks.common.items;

import net.dries007.tfc.common.capabilities.size.Size;

import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.config.ServerConfig.ContainerConfig;
import mod.traister101.sacks.util.ContainerType;

import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public enum DefaultContainers implements ContainerType {

	STRAW_BASKET(SNSConfig.SERVER.strawBasket),
	LEATHER_SACK(SNSConfig.SERVER.leatherSack),
	BURLAP_SACK(SNSConfig.SERVER.burlapSack),
	ORE_SACK(SNSConfig.SERVER.oreSack),
	SEED_POUCH(SNSConfig.SERVER.seedPouch),
	FRAME_PACK(SNSConfig.SERVER.framePack);

	private final ContainerConfig containerConfig;

	DefaultContainers(final ContainerConfig containerConfig) {
		this.containerConfig = containerConfig;
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
		if (this == FRAME_PACK) return Size.HUGE;
		return Size.NORMAL;
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
}