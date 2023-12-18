package dev.kir.netherchest.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeMatcher;

import java.util.List;

final class NetherChestInventoryChannelWrapper extends NetherChestInventoryChannel {
    private final NetherChestInventoryChannel channel;

    public NetherChestInventoryChannelWrapper(NetherChestInventoryChannel channel, ItemStack key) {
        super(channel.getNetherChestInventory(), key);
        this.channel = channel;
    }

    @Override
    void use() {
        this.channel.use();
    }

    @Override
    NetherChestInventoryChannel with(ItemStack key) {
        if (ItemStack.areEqual(this.getKey(), key)) {
            return this;
        }
        return this.channel.with(key);
    }

    @Override
    public void dispose() {
        this.channel.dispose();
    }

    @Override
    public void clear() {
        this.channel.clear();
    }

    @Override
    public void readNbtList(NbtList tags) {
        this.channel.readNbtList(tags);
    }

    @Override
    public NbtList toNbtList() {
        return this.channel.toNbtList();
    }

    @Override
    public void markDirty() {
        this.channel.markDirty();
    }

    @Override
    public int getComparatorOutput() {
        return this.channel.getComparatorOutput();
    }

    @Override
    public void addListener(InventoryChangedListener listener) {
        this.channel.addListener(listener);
    }

    @Override
    public void removeListener(InventoryChangedListener listener) {
        this.channel.removeListener(listener);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.channel.getStack(slot);
    }

    @Override
    public List<ItemStack> clearToList() {
        return this.channel.clearToList();
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.channel.removeStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.channel.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeItem(Item item, int count) {
        return this.channel.removeItem(item, count);
    }

    @Override
    public ItemStack addStack(ItemStack stack) {
        return this.channel.addStack(stack);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return this.channel.canInsert(stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.channel.canPlayerUse(player);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.channel.setStack(slot, stack);
    }

    @Override
    public int size() {
        return this.channel.size();
    }

    @Override
    public boolean isEmpty() {
        return this.channel.isEmpty();
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        this.channel.provideRecipeInputs(finder);
    }

    @Override
    public String toString() {
        return this.channel.toString();
    }
}