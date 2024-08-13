package mod.traister101.sacks.compat.curios;

import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import mod.traister101.sacks.client.renderer.CurioSackItemRenderer;
import mod.traister101.sacks.common.items.*;

public final class CuriosCompat {

	public static void clientSetup() {
		for (final DefaultContainers sackType : DefaultContainers.values()) {
			CuriosRendererRegistry.register(SNSItems.ITEM_CONTAINERS.get(sackType).get(), CurioSackItemRenderer::new);
		}
	}
}