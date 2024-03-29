package dev.kir.netherchest.block.entity;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.block.NetherChestBlocks;
import dev.kir.netherchest.inventory.NetherChestInventory;
import dev.kir.netherchest.inventory.NetherChestInventoryView;
import dev.kir.netherchest.screen.NetherChestScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestLidAnimator;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NetherChestBlockEntity extends BlockEntity implements LidOpenable {
    private static final int VIEWER_COUNT_UPDATE_EVENT = 1;

    private int syncedOutput = -1;
    private boolean inventoryDirty = true;
    private ItemStack key = ItemStack.EMPTY;
    private NetherChestInventoryView inventory = null;
    private InventoryChangedListener listener = null;
    private final ChestLidAnimator lidAnimator = new ChestLidAnimator();
    private final ViewerCountManager stateManager = new ViewerCountManager() {
        @Override
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            world.addSyncedBlockEvent(NetherChestBlockEntity.this.pos, NetherChestBlocks.NETHER_CHEST, VIEWER_COUNT_UPDATE_EVENT, newViewerCount);
        }

        @Override
        protected boolean isPlayerViewing(PlayerEntity player) {
            return (
                player.currentScreenHandler instanceof NetherChestScreenHandler && ((NetherChestScreenHandler)player.currentScreenHandler).getInventory() == NetherChestBlockEntity.this.getInventory() ||
                player.currentScreenHandler instanceof GenericContainerScreenHandler && ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory() == NetherChestBlockEntity.this.getInventory()
            );
        }
    };

    public NetherChestBlockEntity(BlockPos pos, BlockState state) {
        super(NetherChestBlockEntities.NETHER_CHEST, pos, state);
    }

    public @Nullable NetherChestInventoryView getInventory() {
        return this.inventory;
    }

    public ItemStack getKey() {
        return this.inventory == null ? this.key : this.inventory.getKey();
    }

    public int getComparatorOutput() {
        return this.inventory == null ? 0 : this.inventory.getComparatorOutput();
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, NetherChestBlockEntity blockEntity) {
        blockEntity.refreshInventory();
        if (blockEntity.inventory == null) {
            return;
        }

        if (blockEntity.key != blockEntity.inventory.getKey()) {
            blockEntity.key = blockEntity.inventory.getKey();
            blockEntity.inventoryDirty = false;
            blockEntity.markDirty();
        } else if (blockEntity.inventoryDirty) {
            blockEntity.inventoryDirty = false;
            world.updateComparators(pos, state.getBlock());
        }
    }

    @SuppressWarnings("unused")
    public static void clientTick(World world, BlockPos pos, BlockState state, NetherChestBlockEntity blockEntity) {
        blockEntity.lidAnimator.step();
    }

    private void refreshInventory() {
        if (this.inventory != null || this.key == null || this.world == null) {
            return;
        }

        Optional<NetherChestInventoryView> inventory = NetherChestInventory.viewOf(this.world, this.key);
        if (inventory.isPresent()) {
            this.inventory = inventory.get();
            this.setupListener();
        }
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == VIEWER_COUNT_UPDATE_EVENT) {
            this.lidAnimator.setOpen(data > 0);
            return true;
        }

        return super.onSyncedBlockEvent(type, data);
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
        return Inventory.canPlayerUse(this, player);
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
        if (this.inventory != null) {
            this.inventory.markRemoved();
        }
    }

    @Override
    public void cancelRemoval() {
        super.cancelRemoval();
        this.inventoryDirty = true;
        if (this.inventory != null) {
            this.inventory.cancelRemoval();
        }
        this.setupListener();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.key = NetherChest.getConfig().enableMultichannelMode() && nbt.contains("key", NbtElement.COMPOUND_TYPE) ? ItemStack.fromNbt(nbt.getCompound("key")) : ItemStack.EMPTY;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("key", this.getKey().writeNbt(new NbtCompound()));
    }

    private void setupListener() {
        if (this.listener != null || this.inventory == null) {
            return;
        }

        this.listener = x -> {
            int output = ((NetherChestInventoryView)x).getComparatorOutput();
            this.inventoryDirty |= this.syncedOutput != output;
            this.syncedOutput = output;
        };
        this.inventory.addListener(this.listener);
    }

    private void removeListener() {
        if (this.listener == null || this.inventory == null) {
            return;
        }

        this.inventory.removeListener(this.listener);
        this.listener = null;
    }

    @Override
    public float getAnimationProgress(float tickDelta) {
        return this.lidAnimator.getProgress(tickDelta);
    }
}