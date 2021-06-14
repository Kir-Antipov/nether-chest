package me.kirantipov.mods.netherchest.client.render;

import me.kirantipov.mods.netherchest.block.entity.NetherChestBlockEntities;
import me.kirantipov.mods.netherchest.client.render.block.entity.NetherChestBlockEntityRenderer;
import me.kirantipov.mods.netherchest.item.NetherChestItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;

@Environment(EnvType.CLIENT)
public final class NetherChestRenderers {
    static {
        BlockEntityRendererRegistry.INSTANCE.register(NetherChestBlockEntities.NETHER_CHEST, NetherChestBlockEntityRenderer::new);

        BlockEntity renderEntity = NetherChestBlockEntities.NETHER_CHEST.instantiate();
        BuiltinItemRendererRegistry.INSTANCE.register(NetherChestItems.NETHER_CHEST, (itemStack, transform, stack, source, light, overlay) ->
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(renderEntity, stack, source, light, overlay)
        );
    }

    public static void initClient() { }
}