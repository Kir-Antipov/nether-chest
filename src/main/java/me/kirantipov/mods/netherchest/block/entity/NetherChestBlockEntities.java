package me.kirantipov.mods.netherchest.block.entity;

import me.kirantipov.mods.netherchest.NetherChest;
import me.kirantipov.mods.netherchest.block.NetherChestBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public final class NetherChestBlockEntities {
    public static final BlockEntityType<NetherChestBlockEntity> NETHER_CHEST;

    static {
        NETHER_CHEST = register("nether_chest", NetherChestBlockEntity::new, NetherChestBlocks.NETHER_CHEST);
    }

    public static void init() { }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, Supplier<T> supplier, Block block) {
        Identifier trueId = NetherChest.locate(id);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, trueId, BlockEntityType.Builder.create(supplier, block).build(null));
    }
}