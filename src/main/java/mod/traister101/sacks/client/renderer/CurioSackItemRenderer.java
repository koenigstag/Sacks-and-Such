package mod.traister101.sacks.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CurioSackItemRenderer implements ICurioRenderer {

	private final HumanoidModel<LivingEntity> model;

	public CurioSackItemRenderer() {
		final EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
		this.model = new HumanoidModel<>(entityModels.bakeLayer(ModelLayers.PLAYER));
	}

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(final ItemStack itemStack, final SlotContext slotContext,
			final PoseStack poseStack, final RenderLayerParent<T, M> renderLayerParent, final MultiBufferSource bufferSource, final int packedLight,
			final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw,
			final float headPitch) {

		if (!itemStack.isEmpty()) {
			final LivingEntity entity = slotContext.entity();
			poseStack.pushPose();

			ICurioRenderer.translateIfSneaking(poseStack, entity);
			ICurioRenderer.rotateIfSneaking(poseStack, entity);
			ICurioRenderer.followBodyRotations(entity, model);

			poseStack.translate(0, 0.6, 0.15);
			poseStack.scale(0.5F, 0.5F, 0.5F);

			Minecraft.getInstance()
					.getItemRenderer()
					.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource,
							entity.level(), entity.getId());

			poseStack.popPose();
		}
	}
}