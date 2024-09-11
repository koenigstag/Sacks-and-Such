package mod.traister101.sns.client.screen;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.menu.ContainerItemMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class ContainerItemScreen extends AbstractContainerScreen<ContainerItemMenu> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(SacksNSuch.MODID, "textures/gui/container.png");

	public ContainerItemScreen(final ContainerItemMenu menu, final Inventory inventory, final Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
		this.renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTick);
		renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(final GuiGraphics graphics, final float partialtick, final int mouseX, final int mouseY) {
		graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		drawSlots(graphics);
	}

	/**
	 * Dynamically draws the slot texture to the screen where slots are located.
	 */
	private void drawSlots(final GuiGraphics graphics) {
		// TODO come up with good idea to restrict drawn slots? Don't want to require our own slot extention
		// Yes we draw every slot, even the player inventory ones we have baked into the texture
		// Despite this performace is not a concern. Drawing of the ItemStacks the slots contain is much more expensive
		for (final Slot slot : menu.slots) {
			final int x = leftPos + slot.x - 1;
			final int y = topPos + slot.y - 1;
			graphics.blit(TEXTURE, x, y, 176, 0, 18, 18);
		}
	}
}