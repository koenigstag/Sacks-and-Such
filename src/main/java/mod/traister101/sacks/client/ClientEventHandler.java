package mod.traister101.sacks.client;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ClientEventHandler {

	public static void init() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(ClientEventHandler::registerKeyBindings);
	}

	private static void registerKeyBindings(final RegisterKeyMappingsEvent event) {
		event.register(SNSKeybinds.TOGGLE_VOID);
		event.register(SNSKeybinds.TOGGLE_PICKUP);
	}
}