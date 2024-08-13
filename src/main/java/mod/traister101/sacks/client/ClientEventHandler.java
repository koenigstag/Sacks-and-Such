package mod.traister101.sacks.client;

import top.theillusivec4.curios.api.CuriosApi;

import mod.traister101.sacks.client.screen.ContainerItemScreen;
import mod.traister101.sacks.common.menu.SNSMenus;
import mod.traister101.sacks.compat.curios.CuriosCompat;

import net.minecraft.client.gui.screens.MenuScreens;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ClientEventHandler {

	public static void init() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(ClientEventHandler::onClientSetup);
		modEventBus.addListener(ClientEventHandler::registerKeyBindings);
	}

	private static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(SNSMenus.SACK_MENU.get(), ContainerItemScreen::new);
			if (ModList.get().isLoaded(CuriosApi.MODID)) {
				CuriosCompat.clientSetup();
			}
		});
	}

	private static void registerKeyBindings(final RegisterKeyMappingsEvent event) {
		event.register(SNSKeybinds.TOGGLE_VOID);
		event.register(SNSKeybinds.TOGGLE_PICKUP);
	}
}