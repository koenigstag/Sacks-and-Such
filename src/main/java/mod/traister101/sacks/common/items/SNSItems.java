package mod.traister101.sacks.common.items;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.util.ContainerType;

import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;

import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class SNSItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SacksNSuch.MODID);

	// Crafting items
	public static final RegistryObject<Item> UNFINISHED_LEATHER_SACK = registerSimple("unfinished_leather_sack");
	public static final RegistryObject<Item> REINFORCED_FIBER = registerSimple("reinforced_fiber");
	public static final RegistryObject<Item> REINFORCED_FABRIC = registerSimple("reinforced_fabric");
	public static final RegistryObject<Item> PACK_FRAME = registerSimple("pack_frame", new Properties().rarity(Rarity.UNCOMMON));

	// Container Items
	public static final RegistryObject<ContainerItem> STRAW_BASKET = registerContainerItem(DefaultContainers.STRAW_BASKET);
	public static final RegistryObject<ContainerItem> LEATHER_SACK = registerContainerItem(DefaultContainers.LEATHER_SACK);
	public static final RegistryObject<ContainerItem> BURLAP_SACK = registerContainerItem(DefaultContainers.BURLAP_SACK);
	public static final RegistryObject<ContainerItem> ORE_SACK = registerContainerItem(DefaultContainers.ORE_SACK);
	public static final RegistryObject<ContainerItem> SEED_POUCH = registerContainerItem(DefaultContainers.SEED_POUCH);
	public static final RegistryObject<ContainerItem> FRAME_PACK = registerContainerItem(DefaultContainers.FRAME_PACK,
			new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	public static final RegistryObject<LunchBoxItem> LUNCHBOX = register("lunchbox",
			() -> new LunchBoxItem(new Properties(), DefaultContainers.LUNCHBOX));

	private static RegistryObject<ContainerItem> registerContainerItem(final ContainerType containerType) {
		return registerContainerItem(containerType, new Properties().stacksTo(1));
	}

	private static RegistryObject<ContainerItem> registerContainerItem(final ContainerType containerType, final Properties properties) {
		return register(containerType.getSerializedName(), () -> new ContainerItem(properties, containerType));
	}

	private static RegistryObject<Item> registerSimple(final String name) {
		return registerSimple(name, new Properties());
	}

	private static RegistryObject<Item> registerSimple(final String name, final Properties properties) {
		return register(name, () -> new Item(properties));
	}

	private static <I extends Item> RegistryObject<I> register(final String name, final Supplier<I> itemSupplier) {
		return ITEMS.register(name, itemSupplier);
	}
}