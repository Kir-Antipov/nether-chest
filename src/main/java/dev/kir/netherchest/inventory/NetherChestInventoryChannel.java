package dev.kir.netherchest.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.config.NetherChestConfig;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;

import java.util.concurrent.atomic.AtomicInteger;

final class NetherChestInventoryChannel extends SimpleInventory implements KeyedInventory {
    private static final int DEFAULT_SIZE = 27;
    public static final Codec<NetherChestInventoryChannel> CODEC = KeyedInventory.codec(NetherChestInventoryChannel::new);

    private final ItemStack key;
    private final AtomicInteger inUse = new AtomicInteger();
    private int comparatorOutput;

    public NetherChestInventoryChannel(ItemStack key) {
        this(DEFAULT_SIZE, key);
    }

    private NetherChestInventoryChannel(int size, ItemStack key) {
        super(size);
        this.key = key;
    }

    @Override
    public ItemStack getKey() {
        return this.key;
    }

    @Override
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

    @Override
    public boolean isOpen() {
        return this.inUse.get() != 0;
    }

    @Override
    public boolean open() {
        return this.inUse.incrementAndGet() != 0;
    }

    @Override
    public boolean close() {
        return this.inUse.decrementAndGet() == 0;
    }

    @Override
    public void readNbtList(NbtList tags) {
        KeyedInventoryCodec<NetherChestInventoryChannel> codec = (KeyedInventoryCodec<NetherChestInventoryChannel>)CODEC;
        codec.decodeItems(NbtOps.INSTANCE, tags, this);
    }

    @Override
    public NbtList toNbtList() {
        KeyedInventoryCodec<NetherChestInventoryChannel> codec = (KeyedInventoryCodec<NetherChestInventoryChannel>)CODEC;
        DataResult<NbtElement> result = codec.encodeItems(this, NbtOps.INSTANCE, NbtOps.INSTANCE.empty());
        return (NbtList)result.getOrThrow(false, NetherChest.LOGGER::error);
    }

    @Override
    public void markDirty() {
        this.comparatorOutput = KeyedInventory.super.getComparatorOutput();
        super.markDirty();
    }

    @Override
    public int getComparatorOutput() {
        return this.comparatorOutput;
    }
}
