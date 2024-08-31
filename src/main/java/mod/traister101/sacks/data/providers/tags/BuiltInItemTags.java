package mod.traister101.sacks.data.providers.tags;

import net.dries007.tfc.common.items.*;
import top.theillusivec4.curios.api.CuriosApi;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.common.SNSTags;
import mod.traister101.sacks.common.items.SNSItems;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.data.ExistingFileHelper;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public class BuiltInItemTags extends ItemTagsProvider {

	public BuiltInItemTags(final PackOutput packOutput, final CompletableFuture<Provider> lookupProvider,
			final CompletableFuture<TagLookup<Block>> blockTags, @Nullable final ExistingFileHelper existingFileHelper) {
		super(packOutput, lookupProvider, blockTags, SacksNSuch.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(final Provider provider) {
		tag(SNSTags.Items.PREVENTED_IN_ITEM_CONTAINERS).add(SNSItems.STRAW_BASKET.get(), SNSItems.LEATHER_SACK.get(), SNSItems.BURLAP_SACK.get(),
				SNSItems.ORE_SACK.get(), SNSItems.SEED_POUCH.get(), SNSItems.FRAME_PACK.get(), SNSItems.LUNCHBOX.get());
		tag(SNSTags.Items.ALLOWED_IN_SEED_POUCH).addTag(SNSTags.Items.TFC_SEEDS);
		tag(SNSTags.Items.ALLOWED_IN_ORE_SACK).addTag(SNSTags.Items.TFC_SMALL_ORE_PIECES).addTag(SNSTags.Items.TFC_ORE_PIECES);
		tag(SNSTags.Items.LUNCHBOX_FOOD).add(TFCItems.FOOD.get(Food.BARLEY_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.BARLEY_BREAD_JAM_SANDWICH).get(), TFCItems.FOOD.get(Food.MAIZE_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.MAIZE_BREAD_JAM_SANDWICH).get(), TFCItems.FOOD.get(Food.OAT_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.OAT_BREAD_JAM_SANDWICH).get(), TFCItems.FOOD.get(Food.RYE_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.RYE_BREAD_JAM_SANDWICH).get(), TFCItems.FOOD.get(Food.RICE_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.RICE_BREAD_JAM_SANDWICH).get(), TFCItems.FOOD.get(Food.WHEAT_BREAD_SANDWICH).get(),
						TFCItems.FOOD.get(Food.WHEAT_BREAD_JAM_SANDWICH).get())
				.addTag(SNSTags.Items.TFC_BREADS)
				.addTag(SNSTags.Items.TFC_COOKED_MEATS)
				.addTag(SNSTags.Items.TFC_DAIRY)
				.addTag(SNSTags.Items.TFC_FIRUITS)
				.addTag(SNSTags.Items.TFC_VEGETABLES);

		// Curios
		tag(TagKey.create(Registries.ITEM, new ResourceLocation(CuriosApi.MODID, "belt"))).add(SNSItems.LEATHER_SACK.get(),
				SNSItems.BURLAP_SACK.get(), SNSItems.ORE_SACK.get(), SNSItems.SEED_POUCH.get());
		tag(TagKey.create(Registries.ITEM, new ResourceLocation(CuriosApi.MODID, "back"))).add(SNSItems.FRAME_PACK.get());
	}
}