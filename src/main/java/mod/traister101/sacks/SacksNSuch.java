package mod.traister101.sacks;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import mod.traister101.sacks.client.ClientEventHandler;
import mod.traister101.sacks.client.ClientForgeEventHandler;
import mod.traister101.sacks.common.SNSCreativeTab;
import mod.traister101.sacks.common.items.SNSItems;
import mod.traister101.sacks.common.menu.SNSMenus;
import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.network.SNSPacketHandler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@SuppressWarnings("FieldMayBeFinal")
@Mod(SacksNSuch.MODID)
public final class SacksNSuch {

	public static final String MODID = "sns";
	public static final String NAME = "Sacks 'N Such";
	public static final Logger LOGGER = LogUtils.getLogger();

	public SacksNSuch() {
		final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		SNSItems.ITEMS.register(eventBus);
		SNSMenus.MENUS.register(eventBus);
		SNSCreativeTab.CREATIVE_TABS.register(eventBus);

		SNSConfig.init();
		SNSPacketHandler.init();
		ForgeEventHandler.init();

		if (FMLEnvironment.dist == Dist.CLIENT) {
			ClientEventHandler.init();
			ClientForgeEventHandler.init();
		}
	}
}