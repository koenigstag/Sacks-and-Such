package mod.traister101.sacks.util;

import net.dries007.tfc.common.capabilities.size.Size;

import mod.traister101.sacks.common.items.SackItem;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public interface SackType extends StringRepresentable {

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