package mod.traister101.sacks.network;

import org.jetbrains.annotations.Nullable;

import mod.traister101.sacks.common.items.SackItem;
import mod.traister101.sacks.util.NBTHelper;
import mod.traister101.sacks.util.SNSUtils.ToggleType;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ServerboundTogglePacket {

	private final boolean toggle;
	private final ToggleType type;

	public ServerboundTogglePacket(final boolean toggle, final ToggleType type) {
		this.toggle = toggle;
		this.type = type;
	}

	ServerboundTogglePacket(final FriendlyByteBuf friendlyByteBuf) {
		toggle = friendlyByteBuf.readBoolean();
		type = ToggleType.byId(friendlyByteBuf.readInt());
	}

	void encode(final FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeBoolean(toggle);
		friendlyByteBuf.writeInt(type.ordinal());
	}

	void handle(final @Nullable ServerPlayer player) {
		if (player == null) return;

		final ItemStack mainHandItem = player.getMainHandItem();
		if (!(mainHandItem.getItem() instanceof SackItem)) return;

		NBTHelper.toggle(mainHandItem, type, toggle);
	}
}
