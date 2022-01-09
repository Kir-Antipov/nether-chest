package dev.kir.netherchest.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;

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
}
