package mod.traister101.sacks.util;

import net.dries007.tfc.common.capabilities.size.Size;

import mod.traister101.sacks.common.items.SackItem;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public interface SackType extends StringRepresentable {

	/**
	 * @return If the passed {@link ItemStack} supports auto pickup
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	static boolean canDoItemPickup(final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof SackItem sackItem)) return false;

		if (!sackItem.getType().doesAutoPickup()) return false;

		return NBTHelper.isAutoPickup(itemStack);
	}

	/**
	 * @return If the passed {@link ItemStack} supports voiding
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	static boolean canDoItemVoiding(final ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof SackItem sackItem)) return false;

		if (!sackItem.getType().doesVoiding()) return false;

		return NBTHelper.isAutoVoid(itemStack);
	}

	/**
	 * @return The amount of slots this sack has TODO mention and link to menu and screen
	 */
	int getSlotCount();

	/**
	 * @return The slot capacity of this sack type
	 */
	int getSlotCapacity();

	/**
	 * @return If this sack type supports item pickup
	 */
	boolean doesAutoPickup();

	/**
	 * @return If this sack type supports item voiding
	 */
	boolean doesVoiding();

	/**
	 * @return The largest allowed {@link Size} inside the sack
	 */
	Size getAllowedSize();

	/**
	 * Abstracted out to allow custom overrides without the need of a new item class. Called by {@link SackItem#getSize(ItemStack)}
	 *
	 * @param itemStack The {@link SackItem} instance
	 *
	 * @return Size for the stack
	 */
	Size getSize(final ItemStack itemStack);
}