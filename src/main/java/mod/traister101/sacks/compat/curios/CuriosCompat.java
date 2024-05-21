package mod.traister101.sacks.compat.curios;

import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import mod.traister101.sacks.client.renderer.CurioSackItemRenderer;
import mod.traister101.sacks.common.items.DefaultSacks;
import mod.traister101.sacks.common.items.SNSItems;

public final class CuriosCompat {

	public static void clientSetup() {
		for (final DefaultSacks sackType : DefaultSacks.values()) {
			CuriosRendererRegistry.register(SNSItems.SACKS.get(sackType).get(), CurioSackItemRenderer::new);
		}
	}
}