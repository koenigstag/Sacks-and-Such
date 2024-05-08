package mod.traister101.sacks.util;

import mod.traister101.sacks.SacksNSuch;

import net.minecraft.network.chat.Component;

import lombok.experimental.UtilityClass;
import javax.annotation.Nonnull;

@UtilityClass
public final class SNSUtils {

	/**
	 * @param type Type to toggle
	 */
	public static void sendPacketAndStatus(boolean flag, @Nonnull ToggleType type) {
//		SacksNSuch.getNetwork().sendToServer(new TogglePacket(flag, type));
		final String translationKey = SacksNSuch.MODID + type.lang + "." + (flag ? "enabled" : "disabled");
		final Component statusMessage = Component.translatable(translationKey);
//		Minecraft.getMinecraft().player.sendStatusMessage(statusMessage, true);
	}

	/**
	 * An enum for easy and consistent toggle logic
	 */
	public enum ToggleType {
		SEAL(".explosive_vessel.seal", "seal"),
		VOID(".sack.auto_void", "void"),
		PICKUP(".sack.auto_pickup", "pickup"),
		ITEMS("", "hasItems"),
		NULL("", "");

		public final String lang;
		public final String key;

		ToggleType(String lang, String key) {
			this.lang = lang;
			this.key = key;
		}

		@Nonnull
		public static ToggleType getEmum(int id) {
			return id < 0 || id >= values().length ? NULL : values()[id];
		}
	}
}