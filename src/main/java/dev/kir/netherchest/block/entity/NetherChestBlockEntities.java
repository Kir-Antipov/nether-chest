package dev.kir.netherchest.block.entity;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.block.NetherChestBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.function.BiFunction;

public final class NetherChestBlockEntities {
    public static final BlockEntityType<NetherChestBlockEntity> NETHER_CHEST;

    static {
        NETHER_CHEST = register("nether_chest", NetherChestBlockEntity::new, NetherChestBlocks.NETHER_CHEST);
    }

    public static void init() { }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BiFunction<BlockPos, BlockState, T> supplier, Block block) {
        Identifier trueId = NetherChest.locate(id);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, trueId, BlockEntityType.Builder.create(supplier::apply, block).build(null));
    }
}