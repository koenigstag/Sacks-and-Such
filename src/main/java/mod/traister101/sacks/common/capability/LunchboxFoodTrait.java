package mod.traister101.sacks.common.capability;

import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.Helpers;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.config.SNSConfig;

public final class LunchboxFoodTrait {

	public static final String LUNCHBOX_LANG = SacksNSuch.MODID + ".tooltip.food_trait.lunchbox";
	public static final FoodTrait LUNCHBOX = FoodTrait.register(Helpers.identifier("lunchbox"),
			new FoodTrait(() -> SNSConfig.SERVER.traitLunchboxModifier.get().floatValue(), LUNCHBOX_LANG));

	public static void init() {
	}
}