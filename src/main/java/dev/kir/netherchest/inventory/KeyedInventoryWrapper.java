package dev.kir.netherchest.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Set;
import java.util.function.Predicate;

final class KeyedInventoryWrapper implements KeyedInventory {
    private final KeyedInventory inventory;
    private final ItemStack key;

    public KeyedInventoryWrapper(KeyedInventory inventory, ItemStack key) {
        this.inventory = inventory;
        this.key = key;
    }

    @Override
    public Identifier getId() {
        return this.inventory.getId();
    }

    @Override
    public ItemStack getKey() {
        return this.key;
    }

    @Override
    public boolean isOf(ItemStack key) {
        return ItemStack.areEqual(key, this.key) || this.inventory.isOf(key);
    }

    @Override
    public boolean isOpen() {
        return this.inventory.isOpen();
    }

    @Override
    public boolean open() {
        return this.inventory.open();
    }

    @Override
    public boolean close() {
        return this.inventory.close();
    }

    @Override
    public int getComparatorOutput() {
        return this.inventory.getComparatorOutput();
    }

    @Override
    public void addListener(InventoryChangedListener listener) {
        this.inventory.addListener(listener);
    }

    @Override
    public void removeListener(InventoryChangedListener listener) {
        this.inventory.removeListener(listener);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.inventory.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.inventory.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.setStack(slot, stack);
    }

    @Override
    public int getMaxCountPerStack() {
        return this.inventory.getMaxCountPerStack();
    }

    @Override
    public void markDirty() {
        this.inventory.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.inventory.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        this.inventory.onClose(player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return this.inventory.isValid(slot, stack);
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return this.inventory.canTransferTo(hopperInventory, slot, stack);
    }

    @Override
    public int count(Item item) {
        return this.inventory.count(item);
    }

    @Override
    public boolean containsAny(Set<Item> items) {
        return this.inventory.containsAny(items);
    }

    @Override
    public boolean containsAny(Predicate<ItemStack> predicate) {
        return this.inventory.containsAny(predicate);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}