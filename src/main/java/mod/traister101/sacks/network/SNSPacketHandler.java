package mod.traister101.sacks.network;

import org.apache.commons.lang3.mutable.MutableInt;

import mod.traister101.sacks.SacksNSuch;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class SNSPacketHandler {

	private static final String VERSION = ModList.get().getModFileById(SacksNSuch.MODID).versionString();
	private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(SacksNSuch.MODID, "main"), () -> VERSION,
			VERSION::equals, VERSION::equals);
	private static final MutableInt ID = new MutableInt(0);

	public static void send(final PacketDistributor.PacketTarget target, final Object message) {
		CHANNEL.send(target, message);
	}

	/**
	 * Shorthand for {@code SNSPacketHandler.send(PacketDistributor.SERVER.noArg(), message);}
	 */
	public static void sendToServer(final Object message) {
		send(PacketDistributor.SERVER.noArg(), message);
	}

	public static void init() {
		// Server -> Client
		register(ClientboundExtendedSlotInitialDataPacket.class, ClientboundExtendedSlotInitialDataPacket::encode,
				ClientboundExtendedSlotInitialDataPacket::new, ClientboundExtendedSlotInitialDataPacket::handle);
		register(ClientboundExtendedSlotSyncPacket.class, ClientboundExtendedSlotSyncPacket::encode, ClientboundExtendedSlotSyncPacket::new,
				ClientboundExtendedSlotSyncPacket::handle);

		// Client -> Server
		register(ServerboundPickBlockPacket.class, ServerboundPickBlockPacket::encode, ServerboundPickBlockPacket::new,
				ServerboundPickBlockPacket::handle);
		register(ServerboundTogglePacket.class, ServerboundTogglePacket::encode, ServerboundTogglePacket::new, ServerboundTogglePacket::handle);
	}

	private static <T> void register(final Class<T> clazz, final BiConsumer<T, FriendlyByteBuf> encoder, final Function<FriendlyByteBuf, T> decoder,
			final Consumer<T> handler) {
		register(clazz, encoder, decoder, (packet, player) -> handler.accept(packet));
	}

	private static <T> void register(final Class<T> clazz, final BiConsumer<T, FriendlyByteBuf> encoder, final Function<FriendlyByteBuf, T> decoder,
			final BiConsumer<T, ServerPlayer> handler) {
		CHANNEL.registerMessage(ID.getAndIncrement(), clazz, encoder, decoder, (packet, context) -> {
			context.get().setPacketHandled(true);
			context.get().enqueueWork(() -> handler.accept(packet, context.get().getSender()));
		});
	}
}