package dev.kir.netherchest.block.entity;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.block.NetherChestBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class NetherChestBlockEntities {
    public static final BlockEntityType<NetherChestBlockEntity> NETHER_CHEST = register("nether_chest", NetherChestBlockEntity::new, NetherChestBlocks.NETHER_CHEST);

    public static void init() { }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
        Identifier trueId = NetherChest.locate(id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, trueId, FabricBlockEntityTypeBuilder.create(factory, block).build());
    }
}