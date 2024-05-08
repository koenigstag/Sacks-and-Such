package mod.traister101.sacks.util;

import mod.traister101.sacks.common.items.SackItem;
import mod.traister101.sacks.util.SNSUtils.ToggleType;

import net.minecraft.world.item.ItemStack;

import lombok.experimental.UtilityClass;
import javax.annotation.Nonnull;

@UtilityClass
public final class NBTHelper {

	public static boolean isAutoVoid(@Nonnull ItemStack stack) {
		return stack.getItem() instanceof SackItem && stack.getOrCreateTag().getBoolean(ToggleType.VOID.key);
	}

	public static boolean isAutoPickup(@Nonnull ItemStack stack) {
		if (stack.getItem() instanceof SackItem) {
			if (!stack.getOrCreateTag().contains(ToggleType.PICKUP.key)) {
//				SacksNSuch.getNetwork().sendToServer(new TogglePacket(true, ToggleType.PICKUP));
				return true;
			}
		}
		return stack.getOrCreateTag().getBoolean(ToggleType.PICKUP.key);
	}
}