package mod.traister101.sacks.common.menu;

import mod.traister101.sacks.SacksNSuch;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.*;

public final class SNSMenus {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SacksNSuch.MODID);

	public static final RegistryObject<MenuType<SackMenu>> SACK_MENU = MENUS.register("sack_menu",
			() -> IForgeMenuType.create(SackMenu::fromNetwork));
}