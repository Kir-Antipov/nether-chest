package me.kirantipov.mods.netherchest.client.render.block.entity;

import me.kirantipov.mods.netherchest.NetherChest;
import me.kirantipov.mods.netherchest.block.NetherChestBlock;
import me.kirantipov.mods.netherchest.block.NetherChestBlocks;
import me.kirantipov.mods.netherchest.block.entity.NetherChestBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class NetherChestBlockEntityRenderer extends ChestBlockEntityRenderer<NetherChestBlockEntity> {
    private static final Identifier NETHER_CHEST_SPRITE_ID = NetherChest.locate("textures/entity/chest/nether.png");
    private static final BlockState DEFAULT_STATE = NetherChestBlocks.NETHER_CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);

    private final ModelPart singleChestLid;
    private final ModelPart singleChestBase;
    private final ModelPart singleChestLatch;

    public NetherChestBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);

        this.singleChestBase = new ModelPart(64, 64, 0, 19);
        this.singleChestBase.addCuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);

        this.singleChestLid = new ModelPart(64, 64, 0, 0);
        this.singleChestLid.addCuboid(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
        this.singleChestLid.pivotY = 9.0F;
        this.singleChestLid.pivotZ = 1.0F;

        this.singleChestLatch = new ModelPart(64, 64, 0, 0);
        this.singleChestLatch.addCuboid(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
        this.singleChestLatch.pivotY = 8.0F;
    }

    @Override
    public void render(NetherChestBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();

        BlockState blockState = world != null ? entity.getCachedState() : DEFAULT_STATE;
        Block block = blockState.getBlock();

        if (block instanceof NetherChestBlock) {
            matrices.push();
            NetherChestBlock netherChest = (NetherChestBlock)block;

            float rotation = blockState.get(ChestBlock.FACING).asRotation();
            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-rotation));
            matrices.translate(-0.5D, -0.5D, -0.5D);

            DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> properties;
            if (world == null) {
                properties = DoubleBlockProperties.PropertyRetriever::getFallback;
            } else {
                properties = netherChest.getBlockEntitySource(blockState, world, entity.getPos(), true);
            }

            VertexConsumer vertexConsumer = getVertexConsumer(vertexConsumers);
            float openFactor = computeOpenFactor(properties, entity, tickDelta);
            int lightFactor = computeLight(properties, light);
            renderMatrices(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, openFactor, lightFactor, overlay);

            matrices.pop();
        } else {
            super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay);
        }
    }

    private static VertexConsumer getVertexConsumer(VertexConsumerProvider vertexConsumers) {
        return vertexConsumers.getBuffer(RenderLayer.getEntityCutout(NETHER_CHEST_SPRITE_ID));
    }

    private static float computeOpenFactor(DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> propertySource, ChestBlockEntity chestBlockEntity, float tickDelta) {
        float factor = 1.0F - propertySource.apply(ChestBlock.getAnimationProgressRetriever(chestBlockEntity)).get(tickDelta);
        return 1.0F - factor * factor * factor;
    }

    private static int computeLight(DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> propertySource, int light) {
        return propertySource.apply(new LightmapCoordinatesRetriever<ChestBlockEntity>()).applyAsInt(light);
    }

    private static void renderMatrices(MatrixStack matrices, VertexConsumer vertices, ModelPart lid, ModelPart latch, ModelPart base, float openFactor, int light, int overlay) {
        lid.pitch = -openFactor * 1.5707964F;
        latch.pitch = lid.pitch;
        lid.render(matrices, vertices, light, overlay);
        latch.render(matrices, vertices, light, overlay);
        base.render(matrices, vertices, light, overlay);
    }
}