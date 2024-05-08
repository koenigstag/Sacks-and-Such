package mod.traister101.sacks.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public final class CommonConfig {

	public final BooleanValue doPickBlock;

	public CommonConfig(final ForgeConfigSpec.Builder builder) {
		doPickBlock = builder.comment("Do pick block for sacks. Server will trump client config!").define("doPickBlock", true);
	}
}