package dev.kir.netherchest.item;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.block.NetherChestBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.BiConsumer;

@SuppressWarnings("UnstableApiUsage")
public final class NetherChestItems {
    public static final Item NETHER_CHEST;

    static {
        NETHER_CHEST = register("nether_chest", NetherChestBlocks.NETHER_CHEST, new Item.Settings().rarity(Rarity.RARE).maxCount(64), ItemGroups.FUNCTIONAL, (group, item) -> group.addAfter(Items.ENDER_CHEST, item));
    }

    public static void init() { }

    private static Item register(String id, Block block, Item.Settings settings, ItemGroup group, BiConsumer<FabricItemGroupEntries, Item> groupConsumer) {
        Identifier trueId = NetherChest.locate(id);

        BlockItem item = new BlockItem(block, settings);
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> groupConsumer.accept(entries, item));

        return Registry.register(Registries.ITEM, trueId, item);
    }
}