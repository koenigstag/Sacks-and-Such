package mod.traister101.sns.datagen.providers;

import mod.traister101.sns.SacksNSuch;
import mod.traister101.sns.common.items.SNSItems;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class BuiltInItemModels extends ItemModelProvider {

	public static final ResourceLocation SMALL_SACK = new ResourceLocation(SacksNSuch.MODID, "item/held/small_sack");
	public static final ResourceLocation LARGE_SACK = new ResourceLocation(SacksNSuch.MODID, "item/held/large_sack");

	public BuiltInItemModels(final PackOutput output, final ExistingFileHelper existingFileHelper) {
		super(output, SacksNSuch.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		basicItem(SNSItems.UNFINISHED_LEATHER_SACK.get());
		basicItem(SNSItems.REINFORCED_FIBER.get());
		basicItem(SNSItems.REINFORCED_FABRIC.get());
		basicItem(SNSItems.PACK_FRAME.get());

		iconWithHeldModel(SNSItems.STRAW_BASKET.get());
		iconWithHeldModel(SNSItems.LEATHER_SACK.get(),
				withExistingParent("item/held/leather_sack", SMALL_SACK).texture("sack", modLoc("item/held/leather_sack")));
		iconWithHeldModel(SNSItems.BURLAP_SACK.get(),
				withExistingParent("item/held/burlap_sack", SMALL_SACK).texture("sack", modLoc("item/held/burlap_sack")));
		iconWithHeldModel(SNSItems.ORE_SACK.get(),
				withExistingParent("item/held/ore_sack", LARGE_SACK).texture("sack", modLoc("item/held/ore_sack")));
		iconWithHeldModel(SNSItems.SEED_POUCH.get(),
				withExistingParent("item/held/seed_pouch", SMALL_SACK).texture("sack", modLoc("item/held/seed_pouch")));
		iconWithHeldModel(SNSItems.FRAME_PACK.get());
	}

	@SuppressWarnings("UnusedReturnValue")
	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final Item item) {
		return iconWithHeldModel(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
	}

	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final ResourceLocation item) {
		return iconWithHeldModel(item, getExistingFile(item.withPrefix(ITEM_FOLDER + "/held/")));
	}

	@SuppressWarnings("UnusedReturnValue")
	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final Item item, final ModelFile heldModel) {
		return iconWithHeldModel(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), heldModel);
	}

	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final ResourceLocation item, final ModelFile heldModel) {
		return iconWithHeldModel(item, heldModel, icon(item));
	}

	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final ResourceLocation item, final ModelFile heldModel,
			final ModelFile iconModel) {
		return getTransformedItemModelBuilder(item).base(nested().parent(heldModel))
				.perspective(ItemDisplayContext.GUI, nested().parent(iconModel))
				.perspective(ItemDisplayContext.GROUND, nested().parent(iconModel))
				.perspective(ItemDisplayContext.FIXED, nested().parent(iconModel));
	}

	@SuppressWarnings("unused")
	private ItemModelBuilder icon(final Item item) {
		return icon(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
	}

	private ItemModelBuilder icon(final ResourceLocation item) {
		return getBuilder(item.withPrefix("icon/").toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", item.withPrefix("item/icon/"));
	}

	@SuppressWarnings("unused")
	private SeparateTransformsModelBuilder<ItemModelBuilder> getTransformedItemModelBuilder(final Item item) {
		return getTransformedItemModelBuilder(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
	}

	private SeparateTransformsModelBuilder<ItemModelBuilder> getTransformedItemModelBuilder(final ResourceLocation key) {
		return getBuilder(key.toString()).parent(getExistingFile(new ResourceLocation("forge", "item/default")))
				.customLoader(SeparateTransformsModelBuilder::begin);
	}
}