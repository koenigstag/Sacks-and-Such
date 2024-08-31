package mod.traister101.sacks.common.items;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.common.SNSTags;
import mod.traister101.sacks.common.capability.*;
import mod.traister101.sacks.util.*;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.network.NetworkHooks;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class LunchBoxItem extends ContainerItem {

	public static final String SELECTED_SLOT_TOOLTIP = SacksNSuch.MODID + ".tooltip.lunchbox.selected_slot";

	public LunchBoxItem(final Properties properties, final ContainerType type) {
		super(properties, type);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
		final ItemStack heldStack = player.getItemInHand(hand);

		if (!player.isShiftKeyDown()) {
			return heldStack.getCapability(LunchboxCapability.LUNCHBOX).map(lunchboxHandler -> {
				final ItemStack targetFood = lunchboxHandler.getSelectedStack();
				if (targetFood.isEmpty()) return InteractionResultHolder.pass(heldStack);
				final FoodProperties targetFoodProperties = targetFood.getFoodProperties(player);
				if (!targetFood.isEmpty() && !player.getCooldowns().isOnCooldown(targetFood.getItem()) && player.canEat(
						(targetFood.isEdible() && targetFoodProperties != null && targetFoodProperties.canAlwaysEat()))) {
					player.startUsingItem(hand);
					return InteractionResultHolder.consume(heldStack);
				}
				return InteractionResultHolder.pass(heldStack);
			}).orElseGet(() -> InteractionResultHolder.pass(heldStack));
		}

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

		return InteractionResultHolder.pass(heldStack);
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			tooltip.add(Component.translatable(SELECTED_SLOT_TOOLTIP,
					SNSUtils.intComponent(itemStack.getCapability(LunchboxCapability.LUNCHBOX).map(ILunchboxHandler::getSelectedSlot).orElse(0) + 1)
							.withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
			super.appendHoverText(itemStack, level, tooltip, flagIn);
			return;
		}

		super.appendHoverText(itemStack, level, tooltip, flagIn);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(final ItemStack itemStack) {
		// TODO handle the selected slot somehow
		return super.getTooltipImage(itemStack);
	}

	@Override
	public ItemStack finishUsingItem(final ItemStack itemStack, final Level level, final LivingEntity livingEntity) {
		return itemStack.getCapability(LunchboxCapability.LUNCHBOX)
				.map(itemHandler -> itemHandler.consumeSelected(itemStack, level, livingEntity))
				.orElse(itemStack);
	}

	@Override
	public UseAnim getUseAnimation(final ItemStack itemStack) {
		return UseAnim.EAT;
	}

	@Override
	public int getUseDuration(final ItemStack itemStack) {
		return itemStack.getCapability(LunchboxCapability.LUNCHBOX)
				.map(lunchboxHandler -> lunchboxHandler.getSelectedStack().getUseDuration())
				.orElse(32);
	}

	@Nullable
	@Override
	public FoodProperties getFoodProperties(final ItemStack itemStack, final @Nullable LivingEntity entity) {
		final var capability = itemStack.getCapability(LunchboxCapability.LUNCHBOX).resolve();
		return capability.map(lunchboxHandler -> lunchboxHandler.getSelectedFoodProperties(entity)).orElse(null);
	}

	@Getter
	public static class LunchboxHandler extends ContainerItemHandler implements ILunchboxHandler {

		public static final String SELECTED_SLOT_KEY = "selectedSlot";
		private int selectedSlot = 0;

		public LunchboxHandler(final ContainerType type) {
			super(type);
		}

		@Override
		public CompoundTag serializeNBT() {
			final CompoundTag compoundTag = super.serializeNBT();
			compoundTag.putInt(SELECTED_SLOT_KEY, selectedSlot);
			return compoundTag;
		}

		@Override
		public void deserializeNBT(final CompoundTag compoundTag) {
			super.deserializeNBT(compoundTag);
			selectedSlot = compoundTag.getInt(SELECTED_SLOT_KEY);
		}

		@Override
		public boolean isItemValid(final int slotIndex, final ItemStack itemStack) {
			return itemStack.is(SNSTags.Items.LUNCHBOX_FOOD) && super.isItemValid(slotIndex, itemStack);
		}

		@Override
		public ItemStack getSelectedStack() {
			return getStackInSlot(getSelectedSlot());
		}

		@Override
		public void cycleSelected(final CycleDirection cycleDirection) {
			int nextSelection = selectedSlot + switch (cycleDirection) {
				case FORWARD -> 1;
				case BACKWARD -> -1;
			};

			{ // Wrap to within slot bounds
				if (nextSelection >= getSlots()) {
					nextSelection -= getSlots();
				}

				if (nextSelection < 0) {
					nextSelection += getSlots();
				}
			}

			selectedSlot = nextSelection;
			assert selectedSlot >= 0 && selectedSlot < getSlots() : String.format("Selected Slot: %s must be a valid slot index in range [0,%s)",
					selectedSlot, getSlots());
		}

		@Override
		public ItemStack consumeSelected(final ItemStack itemStack, final Level level, final LivingEntity livingEntity) {
			final ItemStack food = extractItem(getSelectedSlot(), 1, false);
			livingEntity.eat(level, food);

			while (getSelectedStack().isEmpty() && getSelectedSlot() != 0) {
				cycleSelected(CycleDirection.BACKWARD);
			}
			return itemStack;
		}
	}
}