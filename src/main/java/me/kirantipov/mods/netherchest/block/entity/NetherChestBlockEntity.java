package me.kirantipov.mods.netherchest.block.entity;

import me.kirantipov.mods.netherchest.NetherChest;
import me.kirantipov.mods.netherchest.NetherChestConfig;
import me.kirantipov.mods.netherchest.block.NetherChestBlocks;
import me.kirantipov.mods.netherchest.inventory.NetherChestInventory;
import me.kirantipov.mods.netherchest.util.InventoryUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@EnvironmentInterfaces({@EnvironmentInterface(
    value = EnvType.CLIENT,
    itf = ChestAnimationProgress.class
)})
public class NetherChestBlockEntity extends ChestBlockEntity implements ChestAnimationProgress, Tickable {
    private static final int VIEWER_COUNT_UPDATE_EVENT = 1;
    private static final SoundEvent OPEN_SOUND = SoundEvents.BLOCK_ENDER_CHEST_OPEN;
    private static final SoundEvent CLOSE_SOUND = SoundEvents.BLOCK_ENDER_CHEST_CLOSE;

    public float animationProgress;
    public float lastAnimationProgress;
    public int viewerCount;
    private int ticks;
    private InventoryChangedListener listener;

    public NetherChestBlockEntity() {
        super(NetherChestBlockEntities.NETHER_CHEST);
    }

    @Override
    public void tick() {
        if (++this.ticks % 20 * 4 == 0) {
            syncViewerCount();
        }

        setupRedstoneIntegration();

        this.lastAnimationProgress = this.animationProgress;
        float progress = this.animationProgress;
        int x = this.pos.getX();
        int y = this.pos.getY();
        int z = this.pos.getZ();

        if (this.viewerCount > 0 && progress == 0.0F) {
            playSound(x, y, z, OPEN_SOUND);
        }

        if (this.viewerCount == 0 && progress > 0.0F || this.viewerCount > 0 && progress < 1.0F) {
            progress += 0.1F * (this.viewerCount > 0 ? 1 : -1);

            if (this.animationProgress >= 0.5F && progress < 0.5F) {
                playSound(x, y, z, CLOSE_SOUND);
            }
        }

        this.animationProgress = Math.min(Math.max(progress, 0), 1);
    }

    private void setupRedstoneIntegration() {
        NetherChestConfig config = NetherChest.getConfig();
        NetherChestInventory netherChestInventory = InventoryUtil.getNetherChestInventory(this.world);

        if (config.allowRedstoneIntegration) {
            if (config.updateNeighborsEveryTick) {
                removeListener();
                this.updateNeighbors();
            } else {
                if (this.listener == null && netherChestInventory != null) {
                    this.listener = x -> this.updateNeighbors();
                    netherChestInventory.addListener(listener);
                }
            }
        } else {
            removeListener();
        }
    }

    private void updateNeighbors() {
        if (!this.world.isClient) {
            BlockPos sourcePos = this.getPos();
            Direction[] directions = Direction.values();
            for (Direction direction : directions) {
                for (int i = 1; i <= 2; ++i) {
                    BlockPos targetPos = sourcePos.offset(direction, i);
                    BlockState targetState = this.world.getBlockState(targetPos);
                    Block targetBlock = targetState.getBlock();
                    if (targetBlock == Blocks.COMPARATOR) {
                        targetState.neighborUpdate(this.world, targetPos, targetBlock, sourcePos, false);
                    }
                }
            }
        }
    }

    private void playSound(int x, int y, int z, SoundEvent sound) {
        final float VOLUME = 0.5F;
        float pitch = this.world.random.nextFloat() * 0.1F + 0.9F;

        this.world.playSound(null, x + 0.5, y + 0.5, z + 0.5, sound, SoundCategory.BLOCKS, VOLUME, pitch);
    }

    private void syncViewerCount() {
        this.world.addSyncedBlockEvent(this.pos, NetherChestBlocks.NETHER_CHEST, VIEWER_COUNT_UPDATE_EVENT, this.viewerCount);
    }

    private void removeListener() {
        if (this.listener != null) {
            NetherChestInventory netherChestInventory = InventoryUtil.getNetherChestInventory(this.world);
            if (netherChestInventory != null) {
                netherChestInventory.removeListener(listener);
            }
        }
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == VIEWER_COUNT_UPDATE_EVENT) {
            this.viewerCount = data;
            return true;
        }

        return super.onSyncedBlockEvent(type, data);
    }

    @Override
    public void markRemoved() {
        this.resetBlock();
        this.removeListener();
        super.markRemoved();
    }

    public void onOpen() {
        ++this.viewerCount;
        syncViewerCount();
    }

    public void onClose() {
        --this.viewerCount;
        syncViewerCount();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity playerEntity) {
        if (this.world.getBlockEntity(this.pos) == this) {
            final double MAX_DISTANCE = 64;
            double distance = playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D);

            return distance <= MAX_DISTANCE;
        }

        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float getAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastAnimationProgress, this.animationProgress);
    }
}