package mod.traister101.sacks.common.items;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.util.ContainerType;

import net.minecraft.world.item.Item;
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
	public static final RegistryObject<Item> PACK_FRAME = registerSimple("pack_frame");

	// Container Items
	public static final RegistryObject<ContainerItem> THATCH_BASKET = registerContainerItem(DefaultContainers.THATCH_BASKET);
	public static final RegistryObject<ContainerItem> LEATHER_SACK = registerContainerItem(DefaultContainers.LEATHER_SACK);
	public static final RegistryObject<ContainerItem> BURLAP_SACK = registerContainerItem(DefaultContainers.BURLAP_SACK);
	public static final RegistryObject<ContainerItem> ORE_SACK = registerContainerItem(DefaultContainers.ORE_SACK);
	public static final RegistryObject<ContainerItem> SEED_POUCH = registerContainerItem(DefaultContainers.SEED_POUCH);
	public static final RegistryObject<ContainerItem> FRAME_PACK = registerContainerItem(DefaultContainers.FRAME_PACK);

	private static RegistryObject<ContainerItem> registerContainerItem(final ContainerType containerType) {
		return register(containerType.getSerializedName(), () -> new ContainerItem(new Properties().stacksTo(1), containerType));
	}

	private static RegistryObject<Item> registerSimple(final String name) {
		return register(name, () -> new Item(new Properties()));
	}

	private static <I extends Item> RegistryObject<I> register(final String name, final Supplier<I> itemSupplier) {
		return ITEMS.register(name, itemSupplier);
	}
}