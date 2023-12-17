package dev.kir.netherchest.inventory;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.config.NetherChestConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NetherChestInventory {
    private final Map<Identifier, List<NetherChestInventoryChannel>> channelsById;
    private final NetherChestInventoryChannel defaultChannel;

    public NetherChestInventory() {
        this.channelsById = new ConcurrentHashMap<>();

        this.defaultChannel = new NetherChestInventoryChannel(this, ItemStack.EMPTY);
        this.channelsById.put(Registries.ITEM.getId(Items.AIR), List.of(this.defaultChannel));
    }

    public NetherChestInventoryChannel channel(ItemStack key) {
        NetherChestConfig config = NetherChest.getConfig();
        if (key.isEmpty() || !config.isValidChannel(key)) {
            this.defaultChannel.use();
            return this.defaultChannel;
        }

        Identifier id = Registries.ITEM.getId(key.getItem());
        List<NetherChestInventoryChannel> channelBucket = this.channelsById.computeIfAbsent(id, x -> new ArrayList<>());
        for (NetherChestInventoryChannel channel : channelBucket) {
            if (channel.isOf(key)) {
                channel.use();
                return channel.with(key);
            }
        }

        NetherChestInventoryChannel channel = new NetherChestInventoryChannel(this, key);
        channelBucket.add(channel);
        channel.use();
        return channel;
    }

    public boolean remove(NetherChestInventoryChannel channel) {
        if (channel == this.defaultChannel) {
            this.defaultChannel.clear();
            return true;
        }

        Identifier id = Registries.ITEM.getId(channel.getKey().getItem());
        List<NetherChestInventoryChannel> channelBucket = this.channelsById.get(id);
        boolean removed = channelBucket != null && channelBucket.remove(channel);
        if (removed && channelBucket.isEmpty()) {
            this.channelsById.remove(id);
        }
        return removed;
    }

    public boolean remove(ItemStack key) {
        NetherChestConfig config = NetherChest.getConfig();
        if (key.isEmpty() || !config.isValidChannel(key)) {
            this.defaultChannel.clear();
            return true;
        }

        Identifier id = Registries.ITEM.getId(key.getItem());
        List<NetherChestInventoryChannel> channelBucket = this.channelsById.get(id);
        boolean removed = channelBucket != null && channelBucket.removeIf(x -> x.isOf(key));
        if (removed) {
            this.channelsById.remove(id);
        }
        return removed;
    }

    public void readNbtList(NbtList tags) {
        this.defaultChannel.readNbtList(new NbtList());

        for (int i = 0; i < tags.size(); ++i) {
            NbtCompound compoundTag = tags.getCompound(i);
            if (compoundTag.contains("channel", NbtElement.COMPOUND_TYPE)) {
                ItemStack channelKey = ItemStack.fromNbt(compoundTag.getCompound("channel"));
                NbtList items = compoundTag.getList("items", NbtElement.COMPOUND_TYPE);
                if (channelKey.isEmpty()) {
                    this.defaultChannel.readNbtList(items);
                } else {
                    NetherChestInventoryChannel channel = new NetherChestInventoryChannel(this, channelKey);
                    channel.readNbtList(items);
                    this.channelsById.computeIfAbsent(Registries.ITEM.getId(channelKey.getItem()), x -> new ArrayList<>()).add(channel);
                }
            } else {
                int slot = compoundTag.getByte("Slot");
                if (slot < this.defaultChannel.size()) {
                    this.defaultChannel.setStack(slot, ItemStack.fromNbt(compoundTag));
                }
            }
        }
    }

    public NbtList toNbtList() {
        NetherChestConfig config = NetherChest.getConfig();
        NbtList listTag = new NbtList();
        for (NetherChestInventoryChannel channel : (Iterable<NetherChestInventoryChannel>)this.channelsById.values().stream().flatMap(Collection::stream)::iterator) {
            if (channel.isEmpty() || !config.isValidChannel(channel.getKey())) {
                continue;
            }

            NbtCompound compoundTag = new NbtCompound();
            compoundTag.put("channel", channel.getKey().writeNbt(new NbtCompound()));
            compoundTag.put("items", channel.toNbtList());
            listTag.add(compoundTag);
        }
        return listTag;
    }
}