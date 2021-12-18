package dev.kir.netherchest.mixin;

import dev.kir.netherchest.recipe.MutableRecipeManager;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.function.Consumer;

@Mixin(RecipeManager.class)
public class MixinRecipeManager implements MutableRecipeManager {
    @Shadow
    private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes;

    @Override
    public <T extends Recipe<?>> void mutateRecipe(RecipeType<T> recipeType, Identifier id, Consumer<T> mutator) {
        Map<Identifier, Recipe<?>> mutableRecipes = this.recipes.get(recipeType);
        if (mutableRecipes != null) {
            Recipe<?> recipe = mutableRecipes.get(id);
            if (recipe != null) {
                mutator.accept((T)recipe);
            }
        }
    }
}