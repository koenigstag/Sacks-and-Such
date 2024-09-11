package mod.traister101.sns.datagen.recipes;

import com.google.gson.JsonObject;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;

import net.minecraft.advancements.AdvancementRewards.Builder;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder.Result;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class DamageInputShapedRecipeBuilder extends ShapedRecipeBuilder {

	public DamageInputShapedRecipeBuilder(final CraftingBookCategory craftingBookCategory, final String folderName, final ItemLike result,
			final int count) {
		super(craftingBookCategory, folderName, result, count);
	}

	public static DamageInputShapedRecipeBuilder shaped(final ItemLike result) {
		return shaped("crafting", result);
	}

	public static DamageInputShapedRecipeBuilder shaped(final ItemLike result, final int count) {
		return shaped("crafting", result, count);
	}

	public static DamageInputShapedRecipeBuilder shaped(final String folderName, final ItemLike result) {
		return shaped(folderName, result, 1);
	}

	public static DamageInputShapedRecipeBuilder shaped(final String folderName, final ItemLike result, final int count) {
		return new DamageInputShapedRecipeBuilder(CraftingBookCategory.MISC, folderName, result, count);
	}

	@Override
	public void save(final Consumer<FinishedRecipe> finishedRecipeConsumer, final ResourceLocation recipeId) {
		this.ensureValid(recipeId);
		this.advancement.parent(ROOT_RECIPE_ADVANCEMENT)
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(Builder.recipe(recipeId))
				.requirements(RequirementsStrategy.OR);
		final Result recipe = new Result(recipeId, this.result, this.count, this.group == null ? "" : this.group, this.craftingBookCategory,
				this.rows, this.key, this.advancement, recipeId.withPrefix("recipes/" + this.folderName + "/"), this.showNotification);

		finishedRecipeConsumer.accept(new DamageResult(recipe));
	}

	public static final class DamageResult implements FinishedRecipe {

		private final FinishedRecipe finishedRecipe;

		public DamageResult(final FinishedRecipe finishedRecipe) {
			this.finishedRecipe = finishedRecipe;
		}

		@Override
		public void serializeRecipeData(final JsonObject jsonObject) {
			final JsonObject recipe = new JsonObject();
			//noinspection DataFlowIssue
			recipe.addProperty("type", ForgeRegistries.RECIPE_SERIALIZERS.getKey(finishedRecipe.getType()).toString());
			finishedRecipe.serializeRecipeData(recipe);
			jsonObject.add("recipe", recipe);
		}

		@Override
		public ResourceLocation getId() {
			return finishedRecipe.getId();
		}

		@Override
		public RecipeSerializer<?> getType() {
			return TFCRecipeSerializers.DAMAGE_INPUT_SHAPED_CRAFTING.get();
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return finishedRecipe.serializeAdvancement();
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return finishedRecipe.getAdvancementId();
		}
	}
}