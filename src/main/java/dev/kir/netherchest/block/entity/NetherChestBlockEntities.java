package dev.kir.netherchest.block.entity;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.block.NetherChestBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class NetherChestBlockEntities {
    public static final BlockEntityType<NetherChestBlockEntity> NETHER_CHEST = register("nether_chest", NetherChestBlockEntity::new, NetherChestBlocks.NETHER_CHEST);

    public static void init() { }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
        Identifier trueId = NetherChest.locate(id);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, trueId, FabricBlockEntityTypeBuilder.create(factory, block).build());
    }
}