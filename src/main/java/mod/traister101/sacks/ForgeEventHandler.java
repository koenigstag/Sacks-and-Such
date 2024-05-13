package mod.traister101.sacks;

import mod.traister101.sacks.common.menu.ExtendedSlotCapacityMenu;
import mod.traister101.sacks.common.menu.ExtendedSlotCapacitySynchronizer;
import mod.traister101.sacks.util.handlers.PickupHandler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ForgeEventHandler {

	public static void init() {
		final IEventBus eventBus = MinecraftForge.EVENT_BUS;

		eventBus.addListener(PickupHandler::onPickupItem);
		eventBus.addListener(PickupHandler::onBlockActivated);
		eventBus.addListener(ForgeEventHandler::onMenuOpen);
	}

	/**
	 * Set the container synchronizer to our ExtendedSlotCapacitySynchronizer as vanilla sets it during {@link ServerPlayer#initMenu}
	 */
	private static void onMenuOpen(final PlayerContainerEvent.Open event) {
		final AbstractContainerMenu containerMenu = event.getContainer();
		if (!(containerMenu instanceof ExtendedSlotCapacityMenu)) return;

		containerMenu.setSynchronizer(new ExtendedSlotCapacitySynchronizer((ServerPlayer) event.getEntity()));
	}
}