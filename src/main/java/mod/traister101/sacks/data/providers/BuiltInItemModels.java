package mod.traister101.sacks.data.providers;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.common.items.SNSItems;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class BuiltInItemModels extends ItemModelProvider {

	public BuiltInItemModels(final PackOutput output, final ExistingFileHelper existingFileHelper) {
		super(output, SacksNSuch.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		basicItem(SNSItems.UNFINISHED_LEATHER_SACK.get());
		basicItem(SNSItems.REINFORCED_FIBER.get());
		basicItem(SNSItems.REINFORCED_FABRIC.get());
		basicItem(SNSItems.PACK_FRAME.get());

		basicItem(SNSItems.LEATHER_SACK.get());
		basicItem(SNSItems.BURLAP_SACK.get());
		basicItem(SNSItems.ORE_SACK.get());
		basicItem(SNSItems.SEED_POUCH.get());
		basicItem(SNSItems.FRAME_PACK.get());

		iconWithHeldModel(SNSItems.STRAW_BASKET.get());
	}

	@SuppressWarnings("UnusedReturnValue")
	private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final Item item) {
		final var key = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));

		final var heldModel = getExistingFile(key.withPrefix(ITEM_FOLDER + "/held/"));
		final var icon = getBuilder(key.withPrefix("icon/").toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", key.withPrefix("item/icon/"));

		return getTransformedItemModelBuilder(key).base(nested().parent(heldModel))
				.perspective(ItemDisplayContext.GUI, nested().parent(icon))
				.perspective(ItemDisplayContext.GROUND, nested().parent(icon))
				.perspective(ItemDisplayContext.FIXED, nested().parent(icon));
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