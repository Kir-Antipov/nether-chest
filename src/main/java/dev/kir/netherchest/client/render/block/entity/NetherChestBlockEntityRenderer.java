package dev.kir.netherchest.client.render.block.entity;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.block.NetherChestBlock;
import dev.kir.netherchest.block.NetherChestBlocks;
import dev.kir.netherchest.block.entity.NetherChestBlockEntity;
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
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathConstants;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class NetherChestBlockEntityRenderer extends ChestBlockEntityRenderer<NetherChestBlockEntity> {
    private static final Identifier NETHER_CHEST_SPRITE_ID = NetherChest.locate("textures/entity/chest/nether.png");
    private static final BlockState DEFAULT_STATE = NetherChestBlocks.NETHER_CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);

    private final ModelPart singleChestLid;
    private final ModelPart singleChestBase;
    private final ModelPart singleChestLatch;

    public NetherChestBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);

        ModelPart modelPart = context.getLayerModelPart(EntityModelLayers.CHEST);
        this.singleChestBase = modelPart.getChild("bottom");
        this.singleChestLid = modelPart.getChild("lid");
        this.singleChestLatch = modelPart.getChild("lock");
    }

    @Override
    public void render(NetherChestBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();

        BlockState blockState = world != null ? entity.getCachedState() : DEFAULT_STATE;
        Block block = blockState.getBlock();

        if (!(block instanceof NetherChestBlock)) {
            super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay);
            return;
        }

        matrices.push();

        float rotation = blockState.get(ChestBlock.FACING).asRotation();
        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rotation));
        matrices.translate(-0.5D, -0.5D, -0.5D);

        DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> properties;
        if (world == null) {
            properties = DoubleBlockProperties.PropertyRetriever::getFallback;
        } else {
            properties = ((NetherChestBlock)block).getBlockEntitySource(blockState, world, entity.getPos(), true);
        }

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(NETHER_CHEST_SPRITE_ID));
        int lightFactor = properties.apply(new LightmapCoordinatesRetriever<ChestBlockEntity>()).applyAsInt(light);
        float openFactor = 1.0F - properties.apply(ChestBlock.getAnimationProgressRetriever(entity)).get(tickDelta);
        openFactor = 1.0F - openFactor * openFactor * openFactor;

        this.singleChestLid.pitch = -openFactor * MathConstants.PI * 0.5F;
        this.singleChestLatch.pitch = this.singleChestLid.pitch;
        this.singleChestLid.render(matrices, vertexConsumer, lightFactor, overlay);
        this.singleChestLatch.render(matrices, vertexConsumer, lightFactor, overlay);
        this.singleChestBase.render(matrices, vertexConsumer, lightFactor, overlay);

        matrices.pop();
    }
}