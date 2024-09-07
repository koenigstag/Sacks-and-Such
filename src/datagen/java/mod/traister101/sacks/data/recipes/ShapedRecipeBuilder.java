package mod.traister101.sacks.data.recipes;

import com.google.common.collect.*;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.*;
import net.minecraft.data.recipes.ShapedRecipeBuilder.Result;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Custom builder for vanillas shaped recipes. Vanillas doesn't let us directly control the folder name which prevents
 * us from outputting crafting recipes under "recipes/crafting/"
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ShapedRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder {

	protected final String folderName;
	protected final CraftingBookCategory craftingBookCategory;
	protected final Item result;
	protected final int count;
	protected final List<String> rows = Lists.newArrayList();
	protected final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
	protected final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
	@Nullable
	protected String group;
	protected boolean showNotification = true;

	public ShapedRecipeBuilder(final CraftingBookCategory craftingBookCategory, final String folderName, final ItemLike result, final int count) {
		this.craftingBookCategory = craftingBookCategory;
		this.folderName = folderName;
		this.result = result.asItem();
		this.count = count;
	}

	public static ShapedRecipeBuilder shaped(final ItemLike result) {
		return shaped("crafting", result);
	}

	public static ShapedRecipeBuilder shaped(final ItemLike result, final int count) {
		return shaped("crafting", result, count);
	}

	public static ShapedRecipeBuilder shaped(final String folderName, final ItemLike result) {
		return shaped(folderName, result, 1);
	}

	public static ShapedRecipeBuilder shaped(final String folderName, final ItemLike result, final int count) {
		return new ShapedRecipeBuilder(CraftingBookCategory.MISC, folderName, result, count);
	}

	/**
	 * Adds a key to the recipe pattern.
	 */
	public ShapedRecipeBuilder define(final Character symbol, final TagKey<Item> tag) {
		return this.define(symbol, Ingredient.of(tag));
	}

	/**
	 * Adds a key to the recipe pattern.
	 */
	public ShapedRecipeBuilder define(final Character symbol, final ItemLike item) {
		return this.define(symbol, Ingredient.of(item));
	}

	/**
	 * Adds a key to the recipe pattern.
	 */
	public ShapedRecipeBuilder define(final Character symbol, final Ingredient ingredient) {
		if (this.key.containsKey(symbol)) {
			throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
		}

		if (symbol == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		}

		this.key.put(symbol, ingredient);
		return this;
	}

	/**
	 * Adds a new entry to the patterns for this recipe.
	 */
	public ShapedRecipeBuilder pattern(final String pattern) {
		if (!this.rows.isEmpty() && pattern.length() != this.rows.get(0).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		}

		this.rows.add(pattern);
		return this;
	}

	/**
	 * Adds a new entry to the patterns for this recipe.
	 */
	public ShapedRecipeBuilder pattern(final String... pattern) {
		Arrays.stream(pattern).forEach(this::pattern);
		return this;
	}

	@Override
	public ShapedRecipeBuilder unlockedBy(final String criterionName, final CriterionTriggerInstance criterionTrigger) {
		this.advancement.addCriterion(criterionName, criterionTrigger);
		return this;
	}

	@Override
	public ShapedRecipeBuilder group(@Nullable final String groupName) {
		this.group = groupName;
		return this;
	}

	@Override
	public Item getResult() {
		return this.result;
	}

	@Override
	public void save(final Consumer<FinishedRecipe> finishedRecipeConsumer, final ResourceLocation recipeId) {
		this.ensureValid(recipeId);
		this.advancement.parent(ROOT_RECIPE_ADVANCEMENT)
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId))
				.requirements(RequirementsStrategy.OR);
		finishedRecipeConsumer.accept(
				new Result(recipeId, this.result, this.count, this.group == null ? "" : this.group, this.craftingBookCategory, this.rows, this.key,
						this.advancement, recipeId.withPrefix("recipes/" + this.folderName + "/"), this.showNotification));
	}

	@Override
	public void save(final Consumer<FinishedRecipe> finishedRecipeConsumer) {
		this.save(finishedRecipeConsumer, RecipeBuilder.getDefaultRecipeId(this.getResult()).withPrefix(this.folderName + "/"));
	}

	public ShapedRecipeBuilder showNotification(final boolean showNotification) {
		this.showNotification = showNotification;
		return this;
	}

	/**
	 * Makes sure that this recipe is valid and obtainable.
	 */
	protected final void ensureValid(final ResourceLocation recipeId) {
		if (this.rows.isEmpty()) {
			throw new IllegalStateException("No pattern is defined for shaped recipe " + recipeId + "!");
		} else {
			final Set<Character> set = Sets.newHashSet(this.key.keySet());
			set.remove(' ');

			for (final String pattern : this.rows) {
				for (int i = 0; i < pattern.length(); ++i) {
					final char symbol = pattern.charAt(i);
					if (!this.key.containsKey(symbol) && symbol != ' ') {
						throw new IllegalStateException("Pattern in recipe " + recipeId + " uses undefined symbol '" + symbol + "'");
					}

					set.remove(symbol);
				}
			}

			if (!set.isEmpty()) {
				throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + recipeId);
			} else if (this.rows.size() == 1 && this.rows.get(0).length() == 1) {
				throw new IllegalStateException(
						"Shaped recipe " + recipeId + " only takes in a single item - should it be a shapeless recipe instead?");
			} else if (this.advancement.getCriteria().isEmpty()) {
				throw new IllegalStateException("No way of obtaining recipe " + recipeId);
			}
		}
	}
}