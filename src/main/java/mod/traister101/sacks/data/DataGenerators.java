package mod.traister101.sacks.data;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.data.providers.BuiltIntLanguage;

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
		final var packOutput = generator.getPackOutput();

		generator.addProvider(event.includeClient(), new BuiltIntLanguage(packOutput));
	}
}