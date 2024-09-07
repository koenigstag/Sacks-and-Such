package mod.traister101.sacks.data.providers;

import com.google.gson.*;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;

import mod.traister101.sacks.SacksNSuch;
import mod.traister101.sacks.common.items.SNSItems;
import mod.traister101.sacks.data.recipes.ShapedRecipeBuilder;
import mod.traister101.sacks.data.recipes.*;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Items;
import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;

public class BuiltInRecipes extends RecipeProvider {

	public BuiltInRecipes(final PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	protected void buildRecipes(final Consumer<FinishedRecipe> writer) {
		ShapedRecipeBuilder.shaped(SNSItems.REINFORCED_FIBER.get())
				.pattern("JJJ", "SSS", "JJJ")
				.define('J', TFCItems.JUTE_FIBER.get())
				.define('S', Tags.Items.STRING)
				.unlockedBy("has_jute", has(TFCItems.JUTE_FIBER.get()))
				.unlockedBy("has_string", has(Tags.Items.STRING))
				.save(writer);

		final var steelRodsTag = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "rods/steel"));
		ShapedRecipeBuilder.shaped(SNSItems.PACK_FRAME.get())
				.pattern("RRR", "R R", "RRR")
				.define('R', steelRodsTag)
				.unlockedBy("has_steel_rod", has(steelRodsTag))
				.save(writer);

		DamageInputShapedRecipeBuilder.shaped(SNSItems.STRAW_BASKET.get())
				.pattern("SSS", "T T", " TK")
				.define('S', TFCItems.STRAW.get())
				.define('T', TFCBlocks.THATCH.get())
				.define('K', TFCTags.Items.KNIVES)
				.unlockedBy("has_straw", has(TFCItems.STRAW.get()))
				.unlockedBy("has_thatch", has(TFCBlocks.THATCH.get()))
				.unlockedBy("has_knife", has(TFCTags.Items.KNIVES))
				.save(writer);

		DamageInputShapedRecipeBuilder.shaped(SNSItems.LEATHER_SACK.get())
				.pattern("JJJ", "LUL", " LN")
				.define('J', TFCItems.JUTE_FIBER.get())
				.define('L', Tags.Items.LEATHER)
				.define('U', SNSItems.UNFINISHED_LEATHER_SACK.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_jute", has(TFCItems.JUTE_FIBER.get()))
				.unlockedBy("has_leather", has(Tags.Items.LEATHER))
				.unlockedBy("has_unfinished_sack", has(SNSItems.UNFINISHED_LEATHER_SACK.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		DamageInputShapedRecipeBuilder.shaped(SNSItems.BURLAP_SACK.get())
				.pattern("JJJ", "B B", " BN")
				.define('J', TFCItems.JUTE_FIBER.get())
				.define('B', TFCItems.BURLAP_CLOTH.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_jute", has(TFCItems.JUTE_FIBER.get()))
				.unlockedBy("has_burlap_cloth", has(TFCItems.BURLAP_CLOTH.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		DamageInputShapedRecipeBuilder.shaped(SNSItems.SEED_POUCH.get())
				.pattern("SSS", "WBW", " WN")
				.define('S', Tags.Items.STRING)
				.define('W', TFCItems.WOOL_CLOTH.get())
				.define('B', TFCItems.BURLAP_CLOTH.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_string", has(Tags.Items.STRING))
				.unlockedBy("has_wool_cloth", has(TFCItems.WOOL_CLOTH.get()))
				.unlockedBy("has_burlap_cloth", has(TFCItems.BURLAP_CLOTH.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		DamageInputShapedRecipeBuilder.shaped(SNSItems.ORE_SACK.get())
				.pattern("RRR", "LBL", " LN")
				.define('R', SNSItems.REINFORCED_FIBER.get())
				.define('L', Tags.Items.LEATHER)
				.define('B', TFCItems.BURLAP_CLOTH.get())
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_reinforced_fiber", has(SNSItems.REINFORCED_FIBER.get()))
				.unlockedBy("has_leather", has(Tags.Items.LEATHER))
				.unlockedBy("has_burlap_cloth", has(TFCItems.BURLAP_CLOTH.get()))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		DamageInputShapedRecipeBuilder.shaped(SNSItems.FRAME_PACK.get())
				.pattern(" F ", "LPL", " FN")
				.define('P', SNSItems.PACK_FRAME.get())
				.define('F', SNSItems.REINFORCED_FABRIC.get())
				.define('L', Items.LEATHER)
				.define('N', TFCTags.Items.SEWING_NEEDLES)
				.unlockedBy("has_pack_frame", has(SNSItems.PACK_FRAME.get()))
				.unlockedBy("has_reinforced_fabric", has(SNSItems.REINFORCED_FABRIC.get()))
				.unlockedBy("has_leather", has(Items.LEATHER))
				.unlockedBy("has_sewing_needle", has(TFCTags.Items.SEWING_NEEDLES))
				.save(writer);

		writer.accept(new LeatherKnapping(SNSItems.UNFINISHED_LEATHER_SACK.get(), " XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX "));
		writer.accept(new Loom(new ItemStackIngredient(Ingredient.of(SNSItems.REINFORCED_FIBER.get()), 16), SNSItems.REINFORCED_FABRIC.get(), 1, 16,
				new ResourceLocation(SacksNSuch.MODID, "loom/reinforced_fabric")));
	}

	// TODO this is gross
	public static class LeatherKnapping implements FinishedRecipe {

		private final Item result;
		private final String[] pattern;

		public LeatherKnapping(final Item result, final String... pattern) {
			this.result = result;
			this.pattern = pattern;
		}

		@Override
		public void serializeRecipeData(final JsonObject jsonObject) {
			jsonObject.addProperty("knapping_type", "tfc:leather");
			final var pattern = new JsonArray();

			Arrays.stream(this.pattern).forEach(pattern::add);

			jsonObject.add("pattern", pattern);

			final var result = new JsonObject();
			//noinspection DataFlowIssue
			result.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
			jsonObject.add("result", result);
		}

		@Override
		public ResourceLocation getId() {
			//noinspection DataFlowIssue
			return ForgeRegistries.ITEMS.getKey(result).withPrefix("leather_knapping/");
		}

		@Override
		public RecipeSerializer<?> getType() {
			return TFCRecipeSerializers.KNAPPING.get();
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}

	public static class Loom implements FinishedRecipe {

		private final ItemStackIngredient ingredient;
		private final Item result;
		private final int count;
		private final int steps;
		private final ResourceLocation texture;

		public Loom(final ItemStackIngredient ingredient, final Item result, final int count, final int steps, final ResourceLocation texture) {
			this.ingredient = ingredient;
			this.result = result;
			this.count = count;
			this.steps = steps;
			this.texture = texture;
		}

		@Override
		public void serializeRecipeData(final JsonObject jsonObject) {
			final var ingredient = new JsonObject();

			ingredient.add("ingredient", this.ingredient.ingredient().toJson());
			ingredient.addProperty("count", this.ingredient.count());

			jsonObject.add("ingredient", ingredient);

			final var result = new JsonObject();
			//noinspection DataFlowIssue
			result.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
			if (this.count > 1) {
				result.addProperty("count", this.count);
			}

			jsonObject.add("result", result);
			jsonObject.addProperty("steps_required", steps);

			jsonObject.addProperty("in_progress_texture", texture.toString());
		}

		@Override
		public ResourceLocation getId() {
			//noinspection DataFlowIssue
			return ForgeRegistries.ITEMS.getKey(result).withPrefix("loom/");
		}

		@Override
		public RecipeSerializer<?> getType() {
			return TFCRecipeSerializers.LOOM.get();
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}