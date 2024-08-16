package mod.traister101.sacks.compat.curios;

import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import mod.traister101.sacks.client.renderer.curios.FramePackCurioRenderer;
import mod.traister101.sacks.common.items.SNSItems;

public final class CuriosCompat {

	public static void clientSetup() {
		CuriosRendererRegistry.register(SNSItems.FRAME_PACK.get(), FramePackCurioRenderer::new);
	}
}