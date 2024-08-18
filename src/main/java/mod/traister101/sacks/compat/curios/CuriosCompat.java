package mod.traister101.sacks.compat.curios;

import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import mod.traister101.sacks.client.renderer.curios.*;
import mod.traister101.sacks.common.items.SNSItems;

public final class CuriosCompat {

	public static void clientSetup() {
		CuriosRendererRegistry.register(SNSItems.FRAME_PACK.get(), FramePackCurioRenderer::new);
		CuriosRendererRegistry.register(SNSItems.LEATHER_SACK.get(), SmallSackCurioRenderer.create(SmallSackCurioRenderer.LEATHER_SACK_TEXTURE));
		CuriosRendererRegistry.register(SNSItems.BURLAP_SACK.get(), SmallSackCurioRenderer.create(SmallSackCurioRenderer.BURLAP_SACK_TEXTURE));
		CuriosRendererRegistry.register(SNSItems.SEED_POUCH.get(), SmallSackCurioRenderer.create(SmallSackCurioRenderer.SEED_POUCH_TEXTURE));
		CuriosRendererRegistry.register(SNSItems.ORE_SACK.get(), LargeSackCurioRenderer.create(LargeSackCurioRenderer.ORE_SACK_TEXTURE));
	}
}