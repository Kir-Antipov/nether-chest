package dev.kir.netherchest.block;

import dev.kir.netherchest.NetherChest;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class NetherChestBlocks {
    public static final Block NETHER_CHEST;

    static {
        NETHER_CHEST = register("nether_chest", new NetherChestBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.DARK_RED).requiresTool().strength(17F, 450F).sounds(BlockSoundGroup.NETHER_BRICKS).luminance(x -> 7)));
    }

    public static void init() { }

    private static Block register(String id, Block block) {
        Identifier trueId = NetherChest.locate(id);
        return Registry.register(Registries.BLOCK, trueId, block);
    }
}