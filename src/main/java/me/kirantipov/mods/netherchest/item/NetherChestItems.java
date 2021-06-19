package me.kirantipov.mods.netherchest.item;

import me.kirantipov.mods.netherchest.NetherChest;
import me.kirantipov.mods.netherchest.block.NetherChestBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public final class NetherChestItems {
    public static final Item NETHER_CHEST;

    static {
        NETHER_CHEST = register("nether_chest", NetherChestBlocks.NETHER_CHEST, new FabricItemSettings().group(ItemGroup.DECORATIONS).rarity(Rarity.RARE).maxCount(64));
    }

    public static void init() { }

    private static Item register(String id, Block block, Item.Settings settings) {
        Identifier trueId = NetherChest.locate(id);

        BlockItem item = new BlockItem(block, settings);
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        return Registry.register(Registry.ITEM, trueId, item);
    }
}