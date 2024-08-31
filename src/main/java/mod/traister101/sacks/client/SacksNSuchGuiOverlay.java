package mod.traister101.sacks.client;

import mod.traister101.sacks.common.capability.LunchboxCapability;
import mod.traister101.sacks.common.items.*;
import mod.traister101.sacks.util.SNSUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.*;

public enum SacksNSuchGuiOverlay {

	LUNCHBOX_INFO("lunchbox_info", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
		final Minecraft minecraft = gui.getMinecraft();
		if (!minecraft.options.hideGui) {
			gui.setupOverlayRenderState(true, false);
			gui.renderHotbar(partialTick, guiGraphics);
		}
		final LocalPlayer player = minecraft.player;
		if (player == null) return;

		final ItemStack currentItem;
		{
			final ItemStack mainHandItem = player.getMainHandItem();
			if (!mainHandItem.is(SNSItems.LUNCHBOX.get())) {
				final ItemStack offhandItem = player.getOffhandItem();
				if (!offhandItem.is(SNSItems.LUNCHBOX.get())) return;
				currentItem = offhandItem;
			} else currentItem = mainHandItem;
		}

		currentItem.getCapability(LunchboxCapability.LUNCHBOX).ifPresent(lunchboxHandler -> {
			final ItemStack selectedStack = lunchboxHandler.getSelectedStack();

			if (!selectedStack.isEmpty()) {
				final Component stackName = selectedStack.getHoverName();

				guiGraphics.drawString(minecraft.font, stackName, (screenWidth - minecraft.font.width(stackName)), screenHeight - 40, 16777215);

				final int itemStackX = screenWidth - 16;
				final int itemStackY = screenHeight - 30;
				guiGraphics.renderFakeItem(selectedStack, itemStackX, itemStackY);
				guiGraphics.renderItemDecorations(minecraft.font, selectedStack, itemStackX, itemStackY);
			}

			final MutableComponent selectedText = Component.translatable(LunchBoxItem.SELECTED_SLOT_TOOLTIP,
					SNSUtils.intComponent(lunchboxHandler.getSelectedSlot() + 1));
			guiGraphics.drawString(minecraft.font, selectedText, (screenWidth - minecraft.font.width(selectedText)), screenHeight - 8, 16777215);

		});
	});

	private final String id;
	private final IGuiOverlay overlay;

	SacksNSuchGuiOverlay(final String id, final IGuiOverlay overlay) {
		this.id = id;
		this.overlay = overlay;
	}

	public static void registerOverlays(final RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), LUNCHBOX_INFO.id(), LUNCHBOX_INFO.overlay);
	}

	public String id() {
		return id;
	}
}