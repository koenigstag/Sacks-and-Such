package mod.traister101.sacks.client;

import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.util.handlers.PickBlockHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public final class ClientForgeEventHandler {

	public static final Minecraft MC = Minecraft.getInstance();

	public static void init() {
		final IEventBus eventBus = MinecraftForge.EVENT_BUS;

		eventBus.addListener(ClientForgeEventHandler::onKeyPress);
		eventBus.addListener(ClientForgeEventHandler::onClickInput);
	}

	public static void onKeyPress(final InputEvent.Key event) {
		// Sanity check
		if (MC.player == null) return;

		if (SNSKeybinds.TOGGLE_VOID.isDown()) {
			final ItemStack heldStack = MC.player.getMainHandItem();
//			SNSUtils.sendPacketAndStatus(!NBTHelper.isAutoVoid(heldStack), SNSUtils.ToggleType.VOID);
		}

		if (SNSKeybinds.TOGGLE_PICKUP.isDown()) {
			final ItemStack heldStack = MC.player.getMainHandItem();
//			SNSUtils.sendPacketAndStatus(!NBTHelper.isAutoPickup(heldStack), SNSUtils.ToggleType.PICKUP);
		}
	}

	private static void onClickInput(final InputEvent.InteractionKeyMappingTriggered event) {
		if (!event.isPickBlock()) return;

		// If we should handle pickblock (Client)
		if (SNSConfig.COMMON.doPickBlock.get()) {
			// Sanity checks
			if (MC.player == null) return;
			if (MC.hitResult == null) return;
			// In creative so don't handle
			if (MC.player.isCreative()) return;

			event.setCanceled(PickBlockHandler.onPickBlock(MC.player, MC.hitResult));
		}
	}
}