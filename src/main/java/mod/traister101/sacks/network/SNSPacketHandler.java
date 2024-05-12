package mod.traister101.sacks.network;

import mod.traister101.sacks.SacksNSuch;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class SNSPacketHandler {

	private static final String VERSION = ModList.get().getModFileById(SacksNSuch.MODID).versionString();
	private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(SacksNSuch.MODID, "main"), () -> VERSION,
			VERSION::equals, VERSION::equals);

	public static void send(final PacketDistributor.PacketTarget target, final Object message) {
		CHANNEL.send(target, message);
	}

	public static void init() {
		int id = 0;
		CHANNEL.messageBuilder(ServerBoundPickBlockPacket.class, id++)
				.encoder(ServerBoundPickBlockPacket::encoder)
				.decoder(ServerBoundPickBlockPacket::new)
				.consumerMainThread(ServerBoundPickBlockPacket::handle)
				.add();

		CHANNEL.messageBuilder(ClientBoundExtendedSlotSyncPacket.class, id++)
				.encoder(ClientBoundExtendedSlotSyncPacket::encode)
				.decoder(ClientBoundExtendedSlotSyncPacket::new)
				.consumerMainThread((clientBoundExtendedSlotSyncPacket, contextSupplier) -> clientBoundExtendedSlotSyncPacket.handle())
				.add();

		CHANNEL.messageBuilder(ClientBoundExtendedSlotInitialDataPacket.class, id++)
				.encoder(ClientBoundExtendedSlotInitialDataPacket::encode)
				.decoder(ClientBoundExtendedSlotInitialDataPacket::new)
				.consumerMainThread((clientBoundExtendedSlotInitialDataPacket, contextSupplier) -> clientBoundExtendedSlotInitialDataPacket.handle())
				.add();
	}
}