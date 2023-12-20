package dev.kir.netherchest.client.render;

import dev.kir.netherchest.block.entity.NetherChestBlockEntities;
import dev.kir.netherchest.client.render.block.entity.NetherChestBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public final class NetherChestRenderers {
    static {
        register(NetherChestBlockEntities.NETHER_CHEST, NetherChestBlockEntityRenderer::new);
    }

    public static void initClient() { }

    private static <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRendererFactory<? super E> rendererFactory) {
        BlockEntityRendererFactories.register(blockEntityType, rendererFactory);

        Identifier id = Registries.BLOCK_ENTITY_TYPE.getId(blockEntityType);
        Block block = Registries.BLOCK.get(id);
        Item item = Registries.ITEM.get(id);
        if (Registries.BLOCK.getId(block).equals(Registries.BLOCK.getDefaultId()) || Registries.ITEM.getId(item).equals(Registries.ITEM.getDefaultId())) {
            return;
        }

        BlockEntity renderEntity = blockEntityType.instantiate(BlockPos.ORIGIN, block.getDefaultState());
        BuiltinItemRendererRegistry.INSTANCE.register(item, (itemStack, transform, stack, source, light, overlay) ->
            MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(renderEntity, stack, source, light, overlay)
        );
    }
}