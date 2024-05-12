package mod.traister101.sacks.util;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.network.SNSPacketHandler;
import mod.traister101.sacks.network.ServerBoundTogglePacket;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ByIdMap;

import net.minecraftforge.network.PacketDistributor;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import java.util.function.IntFunction;

@UtilityClass
public final class SNSUtils {

	public static void sendTogglePacket(final ToggleType toggleType, final boolean flag) {
		SNSPacketHandler.send(PacketDistributor.SERVER.noArg(), new ServerBoundTogglePacket(flag, toggleType));
	}

	/**
	 * An enum for easy and consistent toggle logic
	 */
	public enum ToggleType {
		NONE(0, "", ""),
		VOID(1, SacksNSuch.MODID + ".sack.auto_void", "void"),
		PICKUP(2, SacksNSuch.MODID + ".sack.auto_pickup", "pickup");

		private static final IntFunction<ToggleType> BY_ID = ByIdMap.continuous(ToggleType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		@Getter
		public final int id;
		public final String langKey;
		public final String tag;

		ToggleType(final int id, final String langKey, final String tag) {
			this.id = id;
			this.langKey = langKey;
			this.tag = tag;
		}

		public static ToggleType byId(final int toggleTypeId) {
			return BY_ID.apply(toggleTypeId);
		}

		public MutableComponent getComponent(final boolean flag) {
			return Component.translatable(langKey + "." + (flag ? "enabled" : "disabled"));
		}
	}
}