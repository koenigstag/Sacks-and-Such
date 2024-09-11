package mod.traister101.sns.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

import java.util.function.Function;

public final class SNSConfig {

	public static final CommonConfig COMMON = register(Type.COMMON, CommonConfig::new).getKey();
	public static final ClientConfig CLIENT = register(Type.CLIENT, ClientConfig::new).getKey();
	public static final ServerConfig SERVER;

	private static final ForgeConfigSpec SERVER_SPEC;

	static {
		final var pair = register(Type.SERVER, ServerConfig::new);

		SERVER = pair.getKey();
		SERVER_SPEC = pair.getRight();
	}

	public static void init() {
	}

	private static <Config> Pair<Config, ForgeConfigSpec> register(final Type type, final Function<Builder, Config> configFactory) {
		final var pair = new ForgeConfigSpec.Builder().configure(configFactory);
		ModLoadingContext.get().registerConfig(type, pair.getRight());
		return pair;
	}
}