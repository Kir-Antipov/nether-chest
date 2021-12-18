package dev.kir.netherchest.block.entity;

import dev.kir.netherchest.block.NetherChestBlocks;
import dev.kir.netherchest.inventory.NetherChestInventory;
import dev.kir.netherchest.inventory.NetherChestInventoryHolder;
import dev.kir.netherchest.util.InventoryUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestLidAnimator;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;

public class NetherChestBlockEntity extends BlockEntity implements ChestAnimationProgress {
    private static final int VIEWER_COUNT_UPDATE_EVENT = 1;
    private static final SoundEvent OPEN_SOUND = SoundEvents.BLOCK_ENDER_CHEST_OPEN;
    private static final SoundEvent CLOSE_SOUND = SoundEvents.BLOCK_ENDER_CHEST_CLOSE;

    private int syncedOutput = -1;
    private boolean inventoryDirty = true;
    private InventoryChangedListener listener;
    private final ChestLidAnimator lidAnimator = new ChestLidAnimator();
    private final ViewerCountManager stateManager = new ViewerCountManager() {
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            this.playSound(world, pos, OPEN_SOUND);
        }

        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            this.playSound(world, pos, CLOSE_SOUND);
        }

        private void playSound(World world, BlockPos pos, SoundEvent sound) {
            final float VOLUME = 0.5F;
            float pitch = world.random.nextFloat() * 0.1F + 0.9F;

            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, SoundCategory.BLOCKS, VOLUME, pitch);
        }

        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            world.addSyncedBlockEvent(NetherChestBlockEntity.this.pos, NetherChestBlocks.NETHER_CHEST, VIEWER_COUNT_UPDATE_EVENT, newViewerCount);
        }

        protected boolean isPlayerViewing(PlayerEntity player) {
            if (NetherChestBlockEntity.this.world.isClient) {
                return false;
            }

            WorldProperties properties = world.getServer().getOverworld().getLevelProperties();
            if (!(properties instanceof NetherChestInventoryHolder)) {
                return false;
            }

            NetherChestInventory netherChestInventory = ((NetherChestInventoryHolder)properties).getNetherChestInventory();
            if (netherChestInventory == null) {
                return false;
            }

            return netherChestInventory.isActiveBlockEntity(player,NetherChestBlockEntity.this);
        }
    };

    public NetherChestBlockEntity(BlockPos pos, BlockState state) {
        super(NetherChestBlockEntities.NETHER_CHEST, pos, state);
        this.setupListener();
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, NetherChestBlockEntity blockEntity) {
        if (blockEntity.inventoryDirty) {
            world.updateComparators(pos, state.getBlock());
            blockEntity.inventoryDirty = false;
        }
    }

    @SuppressWarnings("unused")
    public static void clientTick(World world, BlockPos pos, BlockState state, NetherChestBlockEntity blockEntity) {
        blockEntity.lidAnimator.step();
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == VIEWER_COUNT_UPDATE_EVENT) {
            this.lidAnimator.setOpen(data > 0);
            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
        }
    }

    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) == this) {
            final double MAX_DISTANCE = 64;
            double distance = player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D);

            return distance <= MAX_DISTANCE;
        }

        return false;
    }

    public void onScheduledTick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        this.removeListener();
    }

    @Override
    public void cancelRemoval() {
        super.cancelRemoval();
        this.inventoryDirty = true;
        this.setupListener();
    }

    private void setupListener() {
        NetherChestInventory netherChestInventory = InventoryUtil.getNetherChestInventory(this.world);
        if (this.listener == null && netherChestInventory != null) {
            this.listener = x -> {
                int output = ((NetherChestInventory)x).getComparatorOutput();
                this.inventoryDirty |= this.syncedOutput != output;
                this.syncedOutput = output;
            };
            netherChestInventory.addListener(listener);
        }
    }

    private void removeListener() {
        NetherChestInventory netherChestInventory = InventoryUtil.getNetherChestInventory(this.world);
        if (this.listener != null && netherChestInventory != null) {
            netherChestInventory.removeListener(listener);
            this.listener = null;
        }
    }

    public float getAnimationProgress(float tickDelta) {
        return this.lidAnimator.getProgress(tickDelta);
    }
}