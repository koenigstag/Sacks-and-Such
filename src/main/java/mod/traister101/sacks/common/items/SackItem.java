package mod.traister101.sacks.common.items;

import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.dries007.tfc.util.Helpers;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.common.capability.LazyCapabilityProvider.LazySerializedCapabilityProvider;
import mod.traister101.sacks.common.capability.SackHandler;
import mod.traister101.sacks.common.menu.SackMenu;
import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.util.NBTHelper;
import mod.traister101.sacks.util.SackType;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;

import lombok.Getter;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Getter
public class SackItem extends Item implements IItemSize {

	private final SackType type;

	public SackItem(final Properties properties, final SackType type) {
		super(properties);
		this.type = type;
	}

	private static SimpleMenuProvider createMenuProvider(final Player player, final InteractionHand hand, final ItemStack heldStack) {
		return new SimpleMenuProvider((windowId, inventory, unused) -> new SackMenu(windowId, inventory,
				heldStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get(), hand,
				hand == InteractionHand.OFF_HAND ? -1 : player.getInventory().selected), heldStack.getHoverName());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
		final ItemStack heldStack = player.getItemInHand(hand);
		if (level.isClientSide) return InteractionResultHolder.success(heldStack);

		if (!player.isShiftKeyDown()) {
			NetworkHooks.openScreen(((ServerPlayer) player), createMenuProvider(player, hand, heldStack), byteBuf -> {
				byteBuf.writeBoolean(hand == InteractionHand.MAIN_HAND);
				byteBuf.writeInt(type.getSlotCount());
				byteBuf.writeInt(type.getSlotCapacity());
			});
			return InteractionResultHolder.consume(heldStack);
		}

		if (SNSConfig.CLIENT.shiftClickTogglesVoid.get()) {
			if (type.doesVoiding()) {
//				SNSUtils.sendPacketAndStatus(!NBTHelper.isAutoVoid(heldStack), ToggleType.VOID);
			} else {
				final Component status = Component.translatable(SacksNSuch.MODID + ".sack.no_void");
				player.displayClientMessage(status, true);
			}
			return InteractionResultHolder.consume(heldStack);
		}

		if (type.doesAutoPickup()) {
//			SNSUtils.sendPacketAndStatus(!NBTHelper.isAutoPickup(heldStack), ToggleType.PICKUP);
		} else {
			final Component status = Component.translatable(SacksNSuch.MODID + ".sack.no_pickup");
			player.displayClientMessage(status, true);
		}
		return InteractionResultHolder.consume(heldStack);
	}

	@Override
	public boolean overrideStackedOnOther(final ItemStack itemStack, final Slot slot, final ClickAction clickAction, final Player player) {
		if (player.isCreative()) return false;
		if (clickAction != ClickAction.SECONDARY) return false;
		if (!SNSConfig.SERVER.enableSackInventoryInteraction.get()) return false;

		return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
			for (int slotIndex = handler.getSlots() - 1; slotIndex >= 0; slotIndex--) {
				final ItemStack simulate = handler.extractItem(slotIndex, Container.LARGE_MAX_STACK_SIZE, true);
				if (simulate.isEmpty()) continue;

				final ItemStack extracted = handler.extractItem(slotIndex, Container.LARGE_MAX_STACK_SIZE, false);
				final ItemStack leftover = slot.safeInsert(extracted);

				if (leftover.isEmpty()) continue;

				handler.insertItem(slotIndex, leftover, false);

				player.containerMenu.slotsChanged(slot.container);
				return true;
			}

			return false;
		}).orElse(false);
	}

	@Override
	public boolean overrideOtherStackedOnMe(final ItemStack itemStack, final ItemStack carriedStack, final Slot slot, final ClickAction clickAction,
			final Player player, final SlotAccess carriedSlot) {
		if (player.isCreative()) return false;
		if (clickAction != ClickAction.SECONDARY) return false;
		if (!SNSConfig.SERVER.enableSackInventoryInteraction.get()) return false;

		return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {

			if (carriedStack.isEmpty()) {
				for (int slotIndex = handler.getSlots() - 1; slotIndex >= 0; slotIndex--) {
					final ItemStack current = handler.getStackInSlot(slotIndex);
					if (current.isEmpty()) continue;

					carriedSlot.set(handler.extractItem(slotIndex, Container.LARGE_MAX_STACK_SIZE, false));

					player.containerMenu.slotsChanged(slot.container);
					return true;
				}
				return false;
			}

			boolean slotsChanged = false;
			final int initalCount = carriedStack.getCount();

			{
				ItemStack remainder = handler.insertItem(0, carriedStack, false);

				if (remainder.getCount() != initalCount) {
					slotsChanged = true;
					carriedSlot.set(remainder);
				}

				for (int slotIndex = 1; slotIndex < handler.getSlots(); slotIndex++) {

					remainder = handler.insertItem(slotIndex, remainder, false);

					if (remainder.getCount() != initalCount || slotsChanged) {
						slotsChanged = true;
						carriedSlot.set(remainder);
					}

					if (remainder.isEmpty()) break;
				}
			}

			if (!slotsChanged) return false;

			player.containerMenu.slotsChanged(slot.container);
			return true;

		}).orElse(false);
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flagIn) {
		String text = SacksNSuch.MODID + ".sack.tooltip";
		if (Screen.hasShiftDown()) {
			if (NBTHelper.isAutoVoid(itemStack) && type.doesVoiding()) {
				text += ".void";
			}
			if (NBTHelper.isAutoPickup(itemStack) && type.doesAutoPickup()) {
				text += ".pickup";
			}
			text += ".shift";
		}
		tooltip.add(Component.translatable((text)));
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(final ItemStack itemStack) {
		if (!SNSConfig.CLIENT.displayItemContentsAsImages.get()) return super.getTooltipImage(itemStack);

		return itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
			final int width, hight;
			final int slotCount = handler.getSlots();
			switch (slotCount) {
				case 1 -> width = hight = 1;
				case 4 -> width = hight = 2;
				case 8 -> {
					width = 4;
					hight = 2;
				}
				case 18 -> {
					width = 9;
					hight = 2;
				}
				default -> {
					// We want to round up, integer math rounds down
					width = (int) Math.ceil((double) slotCount / 9);
					hight = slotCount / width;
				}
			}
			return Helpers.getTooltipImage(handler, width, hight, 0, slotCount - 1);
		}).orElse(super.getTooltipImage(itemStack));
	}

	@Override
	public boolean isFoil(final ItemStack itemStack) {
		return SNSConfig.CLIENT.voidGlint.get() ? NBTHelper.isAutoVoid(itemStack) : NBTHelper.isAutoPickup(itemStack);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(final ItemStack itemStack, @Nullable CompoundTag nbt) {
		// Must be lazy as stacks can be created before server config is initalized
		return new LazySerializedCapabilityProvider<>(ForgeCapabilities.ITEM_HANDLER, () -> new SackHandler(nbt, type));
	}

	@Override
	public Size getSize(final ItemStack itemStack) {
		return type.getSize(itemStack);
	}

	@Override
	public Weight getWeight(final ItemStack itemStack) {
		return Weight.VERY_HEAVY;
	}
}