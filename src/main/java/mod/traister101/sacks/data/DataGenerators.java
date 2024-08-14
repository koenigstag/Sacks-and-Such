package mod.traister101.sacks.data;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.data.providers.*;
import mod.traister101.sacks.data.providers.tags.*;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import lombok.experimental.UtilityClass;

@UtilityClass
@Mod.EventBusSubscriber(modid = SacksNSuch.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

	@SubscribeEvent
	public static void gatherData(final GatherDataEvent event) {
		final var generator = event.getGenerator();
		final var lookupProvider = event.getLookupProvider();
		final var existingFileHelper = event.getExistingFileHelper();
		final var packOutput = generator.getPackOutput();

		final var blockTagsProvider = generator.<BuiltInBlockTags>addProvider(event.includeServer(),
				poutput -> new BuiltInBlockTags(poutput, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(),
				new BuiltInItemTags(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
		generator.addProvider(event.includeServer(), new BuiltInRecipes(packOutput));
		generator.addProvider(event.includeServer(), new BuiltInCurios(packOutput, existingFileHelper, lookupProvider));

		generator.addProvider(event.includeClient(), new BuiltIntLanguage(packOutput));
		generator.addProvider(event.includeClient(), new BuiltInItemModels(packOutput, existingFileHelper));
	}
}