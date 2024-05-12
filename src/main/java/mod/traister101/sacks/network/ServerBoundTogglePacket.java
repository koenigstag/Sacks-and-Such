package mod.traister101.sacks.network;

import mod.traister101.sacks.common.items.SackItem;
import mod.traister101.sacks.util.NBTHelper;
import mod.traister101.sacks.util.SNSUtils.ToggleType;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public final class ServerBoundTogglePacket {

	private final boolean toggle;
	private final ToggleType type;

	public ServerBoundTogglePacket(final boolean toggle, final ToggleType type) {
		this.toggle = toggle;
		this.type = type;
	}

	ServerBoundTogglePacket(final FriendlyByteBuf friendlyByteBuf) {
		toggle = friendlyByteBuf.readBoolean();
		type = ToggleType.byId(friendlyByteBuf.readInt());
	}

	public void encode(final FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeBoolean(toggle);
		friendlyByteBuf.writeInt(type.ordinal());
	}

	public void handle(final Supplier<Context> contextSupplier) {
		final ServerPlayer player = contextSupplier.get().getSender();

		if (player == null) return;

		final ItemStack mainHandItem = player.getMainHandItem();
		if (!(mainHandItem.getItem() instanceof SackItem)) return;

		NBTHelper.toggle(mainHandItem, type, toggle);
	}
}
