package dev.kir.netherchest.inventory;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.block.entity.NetherChestBlockEntity;
import dev.kir.netherchest.config.NetherChestConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public final class NetherChestInventoryView implements SidedInventory {
    private static final int KEY_SLOT = NetherChestInventoryChannel.SIZE;
    private static final int[] HORIZONTAL_SLOTS = new int[] { KEY_SLOT };
    private static final int[] VERTICAL_SLOTS = IntStream.range(0, NetherChestInventoryChannel.SIZE).toArray();
    public static final int SIZE = NetherChestInventoryChannel.SIZE + 1;

    private ItemStack key;
    private NetherChestInventoryChannel activeChannel;
    private NetherChestBlockEntity activeBlockEntity;
    private InventoryChangedListener channelListener;
    private List<InventoryChangedListener> listeners;

    public NetherChestInventoryView(NetherChestInventory netherChestInventory, ItemStack key) {
        this.activeChannel = netherChestInventory.get(key);
        this.key = this.activeChannel.getKey().copy();
        this.setupListener();
    }

    public ItemStack getKey() {
        return this.key;
    }

    public NetherChestInventoryChannel getActiveChannel() {
        return this.activeChannel;
    }

    public void setActiveBlockEntity(NetherChestBlockEntity blockEntity) {
        this.activeBlockEntity = blockEntity;
    }

    public boolean isActiveBlockEntity(NetherChestBlockEntity blockEntity) {
        return this.activeBlockEntity == blockEntity;
    }

    public void changeChannel(ItemStack key) {
        if (ItemStack.areEqual(key, this.activeChannel.getKey())) {
            return;
        }

        this.removeListener();
        this.activeChannel.dispose();
        this.activeChannel = this.activeChannel.getNetherChestInventory().get(key);
        this.key = this.activeChannel.getKey().copy();
        this.setupListener();
        this.markDirty();
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        return this.key.isEmpty() && this.activeChannel.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot == KEY_SLOT ? this.key : this.activeChannel.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot == KEY_SLOT) {
            ItemStack keyPart = this.key.split(amount);
            if (!keyPart.isEmpty()) {
                this.changeChannel(this.key);
            }
            return keyPart;
        }
        return this.activeChannel.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot == KEY_SLOT) {
            ItemStack currentKey = this.key;
            this.changeChannel(ItemStack.EMPTY);
            return currentKey;
        }
        return this.activeChannel.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == KEY_SLOT) {
            this.changeChannel(stack);
        } else {
            this.activeChannel.setStack(slot, stack);
        }
    }

    @Override
    public void markDirty() {
        this.changeChannel(this.key);
        if (this.listeners == null) {
            return;
        }

        for (InventoryChangedListener listener : this.listeners) {
            listener.onInventoryChanged(this);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.activeBlockEntity != null && this.activeBlockEntity.canPlayerUse(player);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.activeChannel.onOpen(player);
        if (this.activeBlockEntity != null) {
            this.activeBlockEntity.onOpen(player);
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        this.activeChannel.onClose(player);
        if (this.activeBlockEntity != null) {
            this.activeBlockEntity.onClose(player);
        }
        this.activeBlockEntity = null;
    }

    @Override
    public void clear() {
        this.activeChannel.clear();
        this.changeChannel(ItemStack.EMPTY);
    }

    public void addListener(InventoryChangedListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<>();
        }
        this.listeners.add(listener);
    }

    public void removeListener(InventoryChangedListener listener) {
        if (this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return side == Direction.UP || side == Direction.DOWN || !NetherChest.getConfig().enableMultichannelMode() ? VERTICAL_SLOTS : HORIZONTAL_SLOTS;
    }

    public int getComparatorOutput() {
        return this.activeChannel.getComparatorOutput();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        NetherChestConfig config = NetherChest.getConfig();
        return config.allowInsertion() && (slot != KEY_SLOT || config.isValidChannel(stack));
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return NetherChest.getConfig().allowExtraction();
    }

    public void markRemoved() {
        this.removeListener();
        this.activeChannel.dispose();
    }

    public void cancelRemoval() {
        this.activeChannel = this.activeChannel.getNetherChestInventory().get(this.getKey());
        this.setupListener();
    }

    private void setupListener() {
        if (this.channelListener == null) {
            this.channelListener = x -> this.markDirty();
            this.activeChannel.addListener(this.channelListener);
        }
    }

    private void removeListener() {
        if (this.channelListener != null) {
            this.activeChannel.removeListener(this.channelListener);
            this.channelListener = null;
        }
    }
}