package mod.traister101.sacks.common.items;

import mod.traister101.sacks.util.ContainerType;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;

import org.jetbrains.annotations.Nullable;

public class LunchBoxItem extends ContainerItem {

	public LunchBoxItem(final Properties properties, final ContainerType type) {
		super(properties, type);
	}

	public static ItemStack getTargetStack(final ItemStack itemStack) {
		final var optItemHandler = itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();

		if (optItemHandler.isEmpty()) return ItemStack.EMPTY;

		final var itemHandler = optItemHandler.get();

		for (int slotIndex = itemHandler.getSlots() - 1; slotIndex >= 0; slotIndex--) {
			final ItemStack stackInSlot = itemHandler.getStackInSlot(slotIndex);
			if (stackInSlot.isEmpty() || !stackInSlot.isEdible()) continue;
			return stackInSlot;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
		final ItemStack heldStack = player.getItemInHand(hand);
		if (!level.isClientSide) {
			if (player.isShiftKeyDown()) {
				NetworkHooks.openScreen((ServerPlayer) player, createMenuProvider(player, hand, heldStack), byteBuf -> {
					byteBuf.writeBoolean(hand == InteractionHand.MAIN_HAND);
					byteBuf.writeInt(type.getSlotCount());
					byteBuf.writeInt(type.getSlotCapacity());
				});
				return InteractionResultHolder.consume(heldStack);
			}
		}

		if (!player.isShiftKeyDown()) {
			final ItemStack targetFood = getTargetStack(heldStack);
			if (targetFood.isEmpty()) return InteractionResultHolder.pass(heldStack);
			final FoodProperties targetFoodProperties = targetFood.getFoodProperties(player);
			if (!targetFood.isEmpty() && !player.getCooldowns().isOnCooldown(targetFood.getItem()) && player.canEat(
					(targetFood.isEdible() && targetFoodProperties != null && targetFoodProperties.canAlwaysEat()))) {
				player.startUsingItem(hand);
				return InteractionResultHolder.consume(heldStack);
			}

			return InteractionResultHolder.fail(heldStack);
		}
		return InteractionResultHolder.pass(heldStack);
	}

	@Override
	public ItemStack finishUsingItem(final ItemStack itemStack, final Level level, final LivingEntity livingEntity) {
		itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
			for (int slotIndex = itemHandler.getSlots() - 1; slotIndex >= 0; slotIndex--) {
				final ItemStack extractItem = itemHandler.extractItem(slotIndex, 1, false);
				if (extractItem.isEmpty()) continue;

				livingEntity.eat(level, extractItem);
				break;
			}
		});

		return itemStack;
	}

	@Nullable
	@Override
	public FoodProperties getFoodProperties(final ItemStack itemStack, final @Nullable LivingEntity entity) {
		return getTargetStack(itemStack).getFoodProperties(entity);
	}
}