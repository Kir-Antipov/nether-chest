package dev.kir.netherchest.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public interface MutableRecipeManager {
    <T extends Recipe<?>> void mutateRecipe(RecipeType<T> recipeType, Identifier id, Consumer<T> mutator);
}