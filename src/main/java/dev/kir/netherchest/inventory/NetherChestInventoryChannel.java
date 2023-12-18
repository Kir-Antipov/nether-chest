package dev.kir.netherchest.inventory;

import com.mojang.serialization.Codec;
import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.config.NetherChestConfig;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

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

    public Identifier getId() {
        return Registries.ITEM.getId(this.key.getItem());
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

    public static Codec<NetherChestInventoryChannel> getCodec(NetherChestInventory inventory) {
        if (inventory == null) {
            return NetherChestInventoryChannelCodec.INSTANCE;
        }

        return new NetherChestInventoryChannelCodec(inventory);
    }

    @Override
    public void readNbtList(NbtList tags) {
        NetherChestInventoryChannelCodec.INSTANCE.decodeItems(NbtOps.INSTANCE, tags, this);
    }

    @Override
    public NbtList toNbtList() {
        return (NbtList)NetherChestInventoryChannelCodec.INSTANCE.encodeItems(this, NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).getOrThrow(false, NetherChest.LOGGER::error);
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

        return new NetherChestInventoryChannelWrapper(this, key);
    }
}
