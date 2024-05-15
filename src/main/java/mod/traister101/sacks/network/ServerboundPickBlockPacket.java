package mod.traister101.sacks.network;

import org.jetbrains.annotations.Nullable;

import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.util.handlers.PickBlockHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ServerboundPickBlockPacket {

	private final ItemStack stackToSelect;

	public ServerboundPickBlockPacket(final ItemStack stackToSelect) {
		this.stackToSelect = stackToSelect;
	}

	ServerboundPickBlockPacket(final FriendlyByteBuf friendlyByteBuf) {
		this.stackToSelect = friendlyByteBuf.readItem();
	}

	void encode(final FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeItem(stackToSelect);
	}

	void handle(final @Nullable ServerPlayer player) {
		if (!SNSConfig.COMMON.doPickBlock.get()) return;

		if (player == null) return;

		PickBlockHandler.handlePickBlock(player, stackToSelect);
	}
}