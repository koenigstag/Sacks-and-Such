package mod.traister101.sacks.client;

import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.util.*;
import mod.traister101.sacks.util.SNSUtils.ToggleType;
import mod.traister101.sacks.util.handlers.PickBlockHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.HitResult.Type;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public final class ClientForgeEventHandler {

	public static final Minecraft MC = Minecraft.getInstance();

	public static void init() {
		final IEventBus eventBus = MinecraftForge.EVENT_BUS;

		eventBus.addListener(ClientForgeEventHandler::onKeyPress);
		eventBus.addListener(ClientForgeEventHandler::onClickInput);
	}

	public static void onKeyPress(final InputEvent.Key event) {
		// Sanity check
		if (MC.player == null) return;

		if (SNSKeybinds.TOGGLE_VOID.isDown()) {
			final ItemStack heldStack = MC.player.getMainHandItem();
			final boolean flag = !NBTHelper.isAutoVoid(heldStack);
			SNSUtils.sendTogglePacket(ToggleType.VOID, flag);
			MC.player.displayClientMessage(ToggleType.VOID.getComponent(flag), true);
		}

		if (SNSKeybinds.TOGGLE_PICKUP.isDown()) {
			final ItemStack heldStack = MC.player.getMainHandItem();
			final boolean flag = !NBTHelper.isAutoPickup(heldStack);
			SNSUtils.sendTogglePacket(ToggleType.PICKUP, flag);
			MC.player.displayClientMessage(ToggleType.PICKUP.getComponent(flag), true);
		}
	}

	private static void onClickInput(final InputEvent.InteractionKeyMappingTriggered event) {
		if (!event.isPickBlock()) return;

		// If we should handle pickblock (Client)
		if (SNSConfig.COMMON.doPickBlock.get()) {
			// Sanity checks
			if (MC.player == null) return;
			if (MC.hitResult == null) return;
			// In creative so don't handle
			if (MC.player.isCreative()) return;

			event.setCanceled(
					vanillaPickBlock(MC.hitResult, MC.player, MC.level, MC.gameMode) || PickBlockHandler.onPickBlock(MC.player, MC.hitResult));
		}
	}

	/**
	 * Copy of vanillas client pick block logic for us to call
	 *
	 * @return If the vanilla handling succeeded
	 */
	public static boolean vanillaPickBlock(final HitResult hitResult, final Player player, final Level level, final MultiPlayerGameMode gameMode) {
		final boolean creative = player.getAbilities().instabuild;

		final ItemStack pickedStack = getPickedStack(player, level, hitResult, creative);

		if (pickedStack.isEmpty()) return false;

		final Inventory inventory = player.getInventory();

		if (creative) {
			inventory.setPickedItem(pickedStack);
			gameMode.handleCreativeModeItemAdd(player.getItemInHand(InteractionHand.MAIN_HAND), Inventory.INVENTORY_SIZE + inventory.selected);
			return true;
		}

		final int slotIndex = inventory.findSlotMatchingItem(pickedStack);
		if (slotIndex != Inventory.NOT_FOUND_INDEX) {
			if (Inventory.isHotbarSlot(slotIndex)) {
				inventory.selected = slotIndex;
			} else {
				gameMode.handlePickItem(slotIndex);
			}
			return true;
		}

		return false;
	}

	/**
	 * @return The picked {@link ItemStack}. {@link ItemStack#EMPTY} for when nothing should be added
	 */
	private static ItemStack getPickedStack(final Player player, final Level level, final HitResult hitResult, final boolean creative) {
		final Type type = hitResult.getType();

		if (type == Type.BLOCK) {
			final BlockPos blockpos = ((BlockHitResult) hitResult).getBlockPos();
			final BlockState blockState = level.getBlockState(blockpos);
			if (blockState.isAir()) return ItemStack.EMPTY;

			final ItemStack itemStack = blockState.getCloneItemStack(hitResult, level, blockpos, player);

			if (creative && Screen.hasControlDown() && blockState.hasBlockEntity()) {
				final BlockEntity blockEntity = level.getBlockEntity(blockpos);
				if (blockEntity != null) {
					MC.addCustomNbtData(itemStack, blockEntity);
				}
			}

			return itemStack;
		}

		if (type != Type.ENTITY || !creative) return ItemStack.EMPTY;

		final Entity entity = ((EntityHitResult) hitResult).getEntity();

		return entity.getPickedResult(hitResult);
	}
}