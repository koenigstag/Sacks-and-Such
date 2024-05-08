package mod.traister101.sacks.util.handlers;

import net.dries007.tfc.common.blocks.GroundcoverBlock;

import mod.traister101.sacks.common.capability.SackHandler;
import mod.traister101.sacks.common.items.SackItem;
import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.util.NBTHelper;
import mod.traister101.sacks.util.SackType;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Optional;

public final class PickupHandler {

	/**
	 * Intercept item pickups to try and place them into sacks
	 */
	public static void onPickupItem(final EntityItemPickupEvent event) {
		if (!SNSConfig.SERVER.doPickup.get()) return;

		final Player player = event.getEntity();
		final ItemEntity itemEntity = event.getItem();
		final ItemStack itemResult;
		final int pickupCount;
		{
			final ItemStack stack = itemEntity.getItem();
			final int startCount = stack.getCount();
			itemResult = pickupItemStack(player, stack);
			pickupCount = startCount - itemResult.getCount();
		}

		// Update the item entity
		itemEntity.setItem(itemResult);

		// Picked up more than 0
		if (0 < pickupCount) {
			final var packet = new ClientboundTakeItemEntityPacket(itemEntity.getId(), player.getId(), pickupCount);
			((LocalPlayer) player).connection.send(packet);
		}

		event.setCanceled(itemResult.isEmpty());
	}

	/**
	 * Intercept block right clicks, so we can yoink TFC ground items
	 */
	public static void onBlockActivated(final RightClickBlock event) {
		if (!SNSConfig.SERVER.doPickup.get()) return;

		final BlockPos blockPos = event.getPos();
		final Level level = event.getLevel();
		final BlockState blockState = level.getBlockState(blockPos);

		// TFC flat item block
		if (blockState.getBlock() instanceof GroundcoverBlock) {
			final BlockEntity blockEntity = level.getBlockEntity(blockPos);

			if (!(level instanceof ServerLevel serverLevel)) {
				return;
			}

			final Player player = event.getEntity();
			for (final ItemStack itemStack : Block.getDrops(blockState, serverLevel, blockPos, blockEntity, player, ItemStack.EMPTY)) {
				final ItemStack itemResult = pickupItemStack(player, itemStack);

				if (!itemResult.isEmpty()) {
					ItemHandlerHelper.giveItemToPlayer(player, itemResult);
				} else {
					playPickupSound(serverLevel, player.position());
				}
			}
			level.removeBlock(blockPos, false);

			player.swing(InteractionHand.MAIN_HAND);
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	/**
	 * Tries to first fill any valid stacks in the player inventory then tries to fill any {@link SackItem}s. If both fail to consume the entire stack
	 * the remainer is returned
	 *
	 * @param player Player to handle
	 * @param itemPickup The item being picked up
	 *
	 * @return Empty {@link ItemStack} or the remainer.
	 */
	private static ItemStack pickupItemStack(final Player player, final ItemStack itemPickup) {

		final Inventory playerInventory = player.getInventory();
		if (topOffPlayerInventory(playerInventory, itemPickup)) return ItemStack.EMPTY;

		for (int i = 0; i < playerInventory.getContainerSize(); i++) {
			final ItemStack itemContainer = playerInventory.getItem(i);

			final LazyOptional<IItemHandler> containerInv = itemContainer.getCapability(ForgeCapabilities.ITEM_HANDLER);
			if (!containerInv.isPresent()) continue;

			if (itemContainer.getItem() instanceof SackItem) {
				final SackType type = ((SackItem) itemContainer.getItem()).getType();
				// Config pickup disabled for sack type
				if (!type.doesAutoPickup()) continue;
				// This sack in particular has auto pickup disabled
				if (!NBTHelper.isAutoPickup(itemContainer)) continue;
			}

			final Optional<IItemHandler> filtered = containerInv.filter(handler -> isValidForContainer(handler, itemPickup));

			if (filtered.isEmpty()) continue;

			final IItemHandler handler = filtered.get();

			// Goes through the sack slots to see if the picked up item can be added
			for (int j = 0; j < handler.getSlots(); j++) {
				if (handler.getStackInSlot(j).getCount() < handler.getSlotLimit(j)) {
					final ItemStack pickupResult = handler.insertItem(j, itemPickup, false);
					final int numPickedUp = itemPickup.getCount() - pickupResult.getCount();

					if (0 < numPickedUp) {
						player.containerMenu.broadcastChanges();
						if (handler instanceof SackHandler sackHandler) {
							final boolean toggleFlag = sackHandler.hasItems();
//							SNSUtils.toggle(itemContainer, SNSUtils.ToggleType.ITEMS, toggleFlag);
						}
					}

					if (pickupResult.isEmpty()) {
						return ItemStack.EMPTY;
					} else {
						itemPickup.setCount(pickupResult.getCount());
					}
				}
			}
			// Can't void
			if (!canItemVoid(itemContainer)) continue;
			// Make sure there's a slot with the same type of item before voiding the pickup
			for (int j = 0; j < handler.getSlots(); j++) {
				final ItemStack slotStack = handler.getStackInSlot(j);
				if (ItemStack.isSameItem(slotStack, itemPickup)) {
					itemPickup.setCount(0);
					return ItemStack.EMPTY;
				}
			}
		}
		return itemPickup;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean canItemVoid(final ItemStack itemContainer) {
		if (!SNSConfig.SERVER.doVoiding.get()) return false;
		// Not a sack
		if (!(itemContainer.getItem() instanceof SackItem)) return false;
		// Type can't void items
		if (!((SackItem) itemContainer.getItem()).getType().doesVoiding()) return false;
		// Returns if this particular sack item has voiding enabled
		return NBTHelper.isAutoVoid(itemContainer);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean isValidForContainer(final IItemHandler containerInv, final ItemStack itemPickup) {
		for (int i = 0; i < containerInv.getSlots(); i++) {
			if (containerInv.isItemValid(i, itemPickup)) return true;
		}
		return false;
	}

	/**
	 * Tops off the player inventory consuming the itemstack until all stacks in the inventory are filled
	 *
	 * @param inventoryPlayer Player inventory we should top up
	 * @param itemStack The itemstack we consume to fill the inventory
	 *
	 * @return If the item stack was fully consumed
	 */
	private static boolean topOffPlayerInventory(final Inventory inventoryPlayer, final ItemStack itemStack) {
		// Add to player inventory first, if there is an incomplete stack in there.
		for (int i = 0; i < inventoryPlayer.getContainerSize(); i++) {
			final ItemStack inventoryStack = inventoryPlayer.getItem(i);

			// We only add to existing stacks.
			if (inventoryStack.isEmpty()) continue;

			// Already full
			if (inventoryStack.getCount() >= inventoryStack.getMaxStackSize()) continue;

			// Can merge stacks
			if (ItemStack.isSameItemSameTags(inventoryStack, itemStack)) {
				final int remainingSpace = inventoryStack.getMaxStackSize() - inventoryStack.getCount();

				if (remainingSpace >= itemStack.getCount()) {
					// Enough space to add all
					inventoryStack.grow(itemStack.getCount());
					itemStack.setCount(0);
					return true;
				} else {
					// Only part can be added
					inventoryStack.setCount(inventoryStack.getMaxStackSize());
					itemStack.shrink(remainingSpace);
				}
			}
		}
		return false;
	}

	/**
	 * Take a guess
	 *
	 * @param level The level to play the sound in
	 * @param pos The position to play the sound at
	 */
	private static void playPickupSound(final Level level, final Vec3 pos) {
		final var rand = level.random;
		level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
				((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
	}
}