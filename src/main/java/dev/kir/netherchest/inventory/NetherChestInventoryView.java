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

public final class NetherChestInventoryView implements SidedInventory {
    private final NetherChestInventory inventory;
    private ItemStack key;
    private KeyedInventory activeChannel;
    private NetherChestBlockEntity activeBlockEntity;
    private InventoryChangedListener channelListener;
    private List<InventoryChangedListener> listeners;

    NetherChestInventoryView(NetherChestInventory inventory, ItemStack key) {
        this.inventory = inventory;
        this.activeChannel = inventory.get(key);
        this.key = this.activeChannel.getKey().copy();
        this.setupListener();
    }

    public KeyedInventory getActiveChannel() {
        return this.activeChannel;
    }

    public NetherChestBlockEntity getActiveBlockEntity() {
        return this.activeBlockEntity;
    }

    public void setActiveBlockEntity(NetherChestBlockEntity blockEntity) {
        this.activeBlockEntity = blockEntity;
    }

    public ItemStack getKey() {
        return this.key;
    }

    public void setKey(ItemStack key) {
        if (ItemStack.areEqual(key, this.activeChannel.getKey())) {
            return;
        }

        this.removeListener();
        this.tryCloseActiveChannel();
        this.activeChannel = this.inventory.get(key);
        this.key = this.activeChannel.getKey().copy();
        this.setupListener();
        this.markDirty();
    }

    @Override
    public int size() {
        return this.activeChannel.size() + 1;
    }

    @Override
    public boolean isEmpty() {
        return this.key.isEmpty() && this.activeChannel.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return isKeySlot(slot) ? this.key : this.activeChannel.getStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (!isKeySlot(slot)) {
            this.activeChannel.setStack(slot, stack);
            return;
        }

        this.setKey(stack);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (!isKeySlot(slot)) {
            return this.activeChannel.removeStack(slot, amount);
        }

        ItemStack keyPart = this.key.split(amount);
        if (!keyPart.isEmpty()) {
            this.setKey(this.key);
        }
        return keyPart;

    }

    @Override
    public ItemStack removeStack(int slot) {
        if (!isKeySlot(slot)) {
            return this.activeChannel.removeStack(slot);
        }

        ItemStack currentKey = this.key;
        this.setKey(ItemStack.EMPTY);
        return currentKey;
    }

    @Override
    public void markDirty() {
        this.setKey(this.key);
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
        this.setKey(ItemStack.EMPTY);
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

    public boolean isKeySlot(int slot) {
        return slot == this.activeChannel.size();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        NetherChestInventoryViewSlots slots = NetherChestInventoryViewSlots.forChannelSize(this.activeChannel.size());

        if (side.getAxis() == Direction.Axis.Y || !NetherChest.getConfig().enableMultichannelMode()) {
            return slots.getVerticalSlots();
        } else {
            return slots.getHorizontalSlots();
        }
    }

    public int getComparatorOutput() {
        return this.activeChannel.getComparatorOutput();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        NetherChestConfig config = NetherChest.getConfig();
        return config.allowInsertion() && (!isKeySlot(slot) || config.isValidChannel(stack));
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return NetherChest.getConfig().allowExtraction();
    }

    public void markRemoved() {
        this.removeListener();
        this.tryCloseActiveChannel();
    }

    public void cancelRemoval() {
        this.activeChannel = this.inventory.get(this.getKey());
        this.setupListener();
    }

    private void tryCloseActiveChannel() {
        if (this.activeChannel.close() && this.activeChannel.isEmpty()) {
            this.inventory.remove(this.activeChannel.getKey());
        }
    }

    private void setupListener() {
        if (this.channelListener != null) {
            return;
        }

        this.channelListener = x -> this.markDirty();
        this.activeChannel.addListener(this.channelListener);
    }

    private void removeListener() {
        if (this.channelListener == null) {
            return;
        }

        this.activeChannel.removeListener(this.channelListener);
        this.channelListener = null;
    }
}