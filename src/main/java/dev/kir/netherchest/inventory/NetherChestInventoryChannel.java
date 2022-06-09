package dev.kir.netherchest.inventory;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.config.NetherChestConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.ScreenHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NetherChestInventoryChannel extends SimpleInventory {
    public static final int SIZE = 27;

    private final NetherChestInventory netherChestInventory;
    private final ItemStack key;
    private final AtomicInteger inUse = new AtomicInteger();
    private int comparatorOutput;

    public NetherChestInventoryChannel(NetherChestInventory netherChestInventory, ItemStack key) {
        super(SIZE);
        this.netherChestInventory = netherChestInventory;
        this.key = key;
    }

    public NetherChestInventory getNetherChestInventory() {
        return this.netherChestInventory;
    }

    public ItemStack getKey() {
        return this.key;
    }

    public boolean isOf(ItemStack key) {
        NetherChestConfig config = NetherChest.getConfig();
        if (this.key.isEmpty() && (key.isEmpty() || !config.isValidChannel(key))) {
            return true;
        }

        if (!this.key.isOf(key.getItem())) {
            return false;
        }

        boolean countEq = config.ignoreCountInMultichannelMode() || this.key.getCount() == key.getCount();
        boolean nbtEq = config.ignoreNbtInMultichannelMode() || this.key.getNbt() == null && key.getNbt() == null || this.key.getNbt() != null && key.getNbt() != null && this.key.getNbt().equals(key.getNbt());
        return countEq && nbtEq;
    }

    void use() {
        this.inUse.incrementAndGet();
    }

    public void dispose() {
        if (this.inUse.decrementAndGet() == 0 && this.isEmpty()) {
            this.netherChestInventory.remove(this);
        }
    }

    @Override
    public void clear() {
        if (!this.isEmpty()) {
            super.clear();
        }
    }

    @Override
    public void readNbtList(NbtList tags) {
        for (int i = 0; i < this.size(); ++i) {
            this.setStack(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < tags.size(); ++i) {
            NbtCompound compoundTag = tags.getCompound(i);
            int slot = compoundTag.getByte("Slot");
            if (slot < this.size()) {
                this.setStack(slot, ItemStack.fromNbt(compoundTag));
            }
        }

        this.markDirty();
    }

    @Override
    public NbtList toNbtList() {
        NbtList listTag = new NbtList();

        for (int i = 0; i < this.size(); ++i) {
            ItemStack itemStack = this.getStack(i);
            if (!itemStack.isEmpty()) {
                NbtCompound compoundTag = new NbtCompound();
                compoundTag.putByte("Slot", (byte)i);
                itemStack.writeNbt(compoundTag);
                listTag.add(compoundTag);
            }
        }

        return listTag;
    }

    @Override
    public void markDirty() {
        this.comparatorOutput = ScreenHandler.calculateComparatorOutput(this);
        super.markDirty();
    }

    public int getComparatorOutput() {
        return this.comparatorOutput;
    }

    NetherChestInventoryChannel with(ItemStack key) {
        if (ItemStack.areEqual(this.key, key)) {
            return this;
        }

        NetherChestInventoryChannel wrapped = this;
        return new NetherChestInventoryChannel(this.netherChestInventory, key) {
            @Override
            void use() {
                wrapped.use();
            }

            @Override
            NetherChestInventoryChannel with(ItemStack key) {
                if (ItemStack.areEqual(this.getKey(), key)) {
                    return this;
                }
                return wrapped.with(key);
            }

            @Override
            public void dispose() {
                wrapped.dispose();
            }

            @Override
            public void clear() {
                wrapped.clear();
            }

            @Override
            public void readNbtList(NbtList tags) {
                wrapped.readNbtList(tags);
            }

            @Override
            public NbtList toNbtList() {
                return wrapped.toNbtList();
            }

            @Override
            public void markDirty() {
                wrapped.markDirty();
            }

            @Override
            public int getComparatorOutput() {
                return wrapped.getComparatorOutput();
            }

            @Override
            public void addListener(InventoryChangedListener listener) {
                wrapped.addListener(listener);
            }

            @Override
            public void removeListener(InventoryChangedListener listener) {
                wrapped.removeListener(listener);
            }

            @Override
            public ItemStack getStack(int slot) {
                return wrapped.getStack(slot);
            }

            @Override
            public List<ItemStack> clearToList() {
                return wrapped.clearToList();
            }

            @Override
            public ItemStack removeStack(int slot) {
                return wrapped.removeStack(slot);
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                return wrapped.removeStack(slot, amount);
            }

            @Override
            public ItemStack removeItem(Item item, int count) {
                return wrapped.removeItem(item, count);
            }

            @Override
            public ItemStack addStack(ItemStack stack) {
                return wrapped.addStack(stack);
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                return wrapped.canInsert(stack);
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return wrapped.canPlayerUse(player);
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                wrapped.setStack(slot, stack);
            }

            @Override
            public int size() {
                return wrapped.size();
            }

            @Override
            public boolean isEmpty() {
                return wrapped.isEmpty();
            }

            @Override
            public void provideRecipeInputs(RecipeMatcher finder) {
                wrapped.provideRecipeInputs(finder);
            }

            @Override
            public String toString() {
                return wrapped.toString();
            }
        };
    }
}
