package mod.traister101.sacks.network;

import net.dries007.tfc.client.ClientHelpers;

import mod.traister101.sacks.common.menu.SackMenu;
import mod.traister101.sacks.util.ByteBufUtils;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ClientboundExtendedSlotInitialDataPacket {

	private final int stateID;
	private final int windowId;
	private final NonNullList<ItemStack> stacks;
	private final ItemStack carried;

	public ClientboundExtendedSlotInitialDataPacket(final int stateID, final int windowId, final NonNullList<ItemStack> stacks,
			final ItemStack carried) {
		this.stateID = stateID;
		this.windowId = windowId;
		this.stacks = stacks;
		this.carried = carried;
	}

	ClientboundExtendedSlotInitialDataPacket(final FriendlyByteBuf byteBuf) {
		stateID = byteBuf.readInt();
		windowId = byteBuf.readInt();
		carried = byteBuf.readItem();
		stacks = byteBuf.readCollection(NonNullList::createWithCapacity, ByteBufUtils::readExtendedItemStack);
	}

	void encode(final FriendlyByteBuf byteBuf) {
		byteBuf.writeInt(stateID);
		byteBuf.writeInt(windowId);
		byteBuf.writeItem(carried);
		byteBuf.writeCollection(stacks, ByteBufUtils::writeExtendedItemStack);
	}


	void handle() {
		final Player player = ClientHelpers.getPlayer();
		if (player != null && player.containerMenu instanceof SackMenu && windowId == player.containerMenu.containerId) {
			player.containerMenu.initializeContents(stateID, stacks, carried);
		}
	}
}