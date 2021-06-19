package me.kirantipov.mods.netherchest.recipe;

import me.kirantipov.mods.netherchest.NetherChest;
import me.kirantipov.mods.netherchest.block.NetherChestBlocks;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class NetherChestRecipes {
    public static void init(RecipeManager recipeManager) {
        if (!(recipeManager instanceof MutableRecipeManager)) {
            return;
        }

        ((MutableRecipeManager)recipeManager).mutateRecipe(RecipeType.CRAFTING, Registry.BLOCK.getId(NetherChestBlocks.NETHER_CHEST), recipe -> {
            Identifier itemId;
            try {
                itemId = new Identifier(NetherChest.getConfig().coreItem);
            } catch (Exception e) {
                itemId = new Identifier(NetherChest.CONFIG_MANAGER.getOptionByName("coreItem").getDefaultValue().toString());
            }
            Item item = Registry.ITEM.get(itemId);
            if (item == null) {
                return;
            }
            recipe.getPreviewInputs().set(4, Ingredient.ofItems(item));
        });
    }
}