package mod.traister101.sacks.network;

import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.util.handlers.PickBlockHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public final class ServerBoundPickBlockPacket {

	private final ItemStack stackToSelect;

	public ServerBoundPickBlockPacket(final ItemStack stackToSelect) {
		this.stackToSelect = stackToSelect;
	}

	ServerBoundPickBlockPacket(final FriendlyByteBuf friendlyByteBuf) {
		this.stackToSelect = friendlyByteBuf.readItem();
	}

	void encoder(final FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeItem(stackToSelect);
	}

	void handle(final Supplier<Context> contextSupplier) {
		if (!SNSConfig.COMMON.doPickBlock.get()) return;

		final Context context = contextSupplier.get();
		final ServerPlayer player = context.getSender();
		if (player == null) return;

		PickBlockHandler.handlePickBlock(player, stackToSelect);
	}
}