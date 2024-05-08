package mod.traister101.sacks.common.items;

import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.common.capability.SackHandler;
import mod.traister101.sacks.config.SNSConfig;
import mod.traister101.sacks.util.NBTHelper;
import mod.traister101.sacks.util.SackType;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

import lombok.Getter;
import javax.annotation.Nullable;
import java.util.List;

@Getter
public class SackItem extends Item implements IItemSize {

	private final SackType type;

	public SackItem(final Properties properties, final SackType type) {
		super(properties);
		this.type = type;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
		final ItemStack heldStack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			if (player.isShiftKeyDown()) {
				if (SNSConfig.CLIENT.shiftClickTogglesVoid.get()) {
					if (type.doesVoiding()) {
//						SNSUtils.sendPacketAndStatus(!NBTHelper.isAutoVoid(heldStack), ToggleType.VOID);
					} else {
						final Component status = Component.translatable(SacksNSuch.MODID + ".sack.no_void");
						player.displayClientMessage(status, true);
					}
				} else {
					if (type.doesAutoPickup()) {
//						SNSUtils.sendPacketAndStatus(!NBTHelper.isAutoPickup(heldStack), ToggleType.PICKUP);
					} else {
						final Component status = Component.translatable(SacksNSuch.MODID + ".sack.no_pickup");
						player.displayClientMessage(status, true);
					}
				}
			} else {
				// TODO gui stuff
//				GuiHandler.openGui(level, player, GuiType.SACK);
			}
			return InteractionResultHolder.success(heldStack);
		}
		return InteractionResultHolder.fail(heldStack);
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
	public boolean isFoil(final ItemStack itemStack) {
		return SNSConfig.CLIENT.voidGlint.get() ? NBTHelper.isAutoVoid(itemStack) : NBTHelper.isAutoPickup(itemStack);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(final ItemStack itemStack, @Nullable CompoundTag nbt) {
		return new SackHandler(nbt, type);
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