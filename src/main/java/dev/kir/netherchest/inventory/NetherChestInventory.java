package dev.kir.netherchest.inventory;

import com.mojang.serialization.Codec;
import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.config.NetherChestConfig;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldProperties;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class NetherChestInventory {
    public static final Codec<NetherChestInventory> CODEC;

    private final Map<Identifier, List<NetherChestInventoryChannel>> channelsById;
    private NetherChestInventoryChannel defaultChannel;

    public NetherChestInventory() {
        this.channelsById = new ConcurrentHashMap<>();
        this.defaultChannel = new NetherChestInventoryChannel(this, ItemStack.EMPTY);
        this.channelsById.put(this.defaultChannel.getId(), List.of(this.defaultChannel));
    }

    public static Optional<NetherChestInventory> of(WorldAccess world) {
        if (!(world instanceof World) || world.isClient()) {
            return Optional.empty();
        }

        WorldProperties properties = world.getServer().getOverworld().getLevelProperties();
        if (!(properties instanceof NetherChestInventoryHolder)) {
            return Optional.empty();
        }

        return Optional.ofNullable(((NetherChestInventoryHolder)properties).getNetherChestInventory());
    }

    public Iterable<NetherChestInventoryChannel> channels() {
        return this.channelsById.values().stream().flatMap(Collection::stream)::iterator;
    }

    public NetherChestInventoryChannel get(ItemStack key) {
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

        Identifier id = channel.getId();
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

    public void clear() {
        channels().forEach(SimpleInventory::clear);
        this.channelsById.clear();
        this.channelsById.put(this.defaultChannel.getId(), List.of(this.defaultChannel));
    }

    static {
        CODEC = NetherChestInventoryChannel.CODEC.listOf().xmap(
            channels -> {
                NetherChestInventory inventory = new NetherChestInventory();

                for (NetherChestInventoryChannel channel : channels) {
                    if (channel.getKey().isEmpty()) {
                        inventory.defaultChannel = channel;
                        inventory.channelsById.put(channel.getId(), List.of(channel));
                    } else {
                        inventory.channelsById.computeIfAbsent(channel.getId(), x -> new ArrayList<>()).add(channel);
                    }
                }

                return inventory;
            },

            inventory -> {
                NetherChestConfig config = NetherChest.getConfig();
                Stream<NetherChestInventoryChannel> channels = StreamSupport.stream(inventory.channels().spliterator(), false);
                Stream<NetherChestInventoryChannel> validChannels = channels.filter(x -> config.isValidChannel(x.getKey()) && !x.isEmpty());

                return validChannels.collect(Collectors.toList());
            }
        );
    }
}