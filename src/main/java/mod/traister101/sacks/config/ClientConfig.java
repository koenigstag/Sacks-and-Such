package mod.traister101.sacks.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public final class ClientConfig {

	public final BooleanValue voidGlint;
	public final BooleanValue shiftClickTogglesVoid;
	public final BooleanValue displayItemContentsAsImages;

	ClientConfig(final ForgeConfigSpec.Builder builder) {
		voidGlint = builder.comment("Swaps the enchant glint from when auto pickup is enabled to when it's dissabled").define("voidGlint", true);
		shiftClickTogglesVoid = builder.comment("This determines whether shift right click toggles item voiding or item pickup for sacks")
				.define("shiftClickTogglesVoid", false);
		displayItemContentsAsImages = builder.comment("When enabled sacks will display their contents like how TFC vessels do")
				.define("displayItemContentsAsImages", true);
	}
}