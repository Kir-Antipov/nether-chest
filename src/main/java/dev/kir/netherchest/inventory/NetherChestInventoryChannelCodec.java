package dev.kir.netherchest.inventory;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Optional;
import java.util.stream.Stream;

final class NetherChestInventoryChannelCodec implements Codec<NetherChestInventoryChannel> {
    public static final NetherChestInventoryChannelCodec INSTANCE = new NetherChestInventoryChannelCodec();

    private final NetherChestInventory inventory;

    private NetherChestInventoryChannelCodec() {
        this(null);
    }

    public NetherChestInventoryChannelCodec(NetherChestInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public <T> DataResult<T> encode(NetherChestInventoryChannel input, DynamicOps<T> ops, T prefix) {
        RecordBuilder<T> channel = ops.mapBuilder();
        channel.add("channel", ItemStack.CODEC.encodeStart(ops, input.getKey()));
        channel.add("items", encodeItems(input, ops, ops.empty()));
        return channel.build(prefix);
    }

    public <T> DataResult<T> encodeItems(NetherChestInventoryChannel input, DynamicOps<T> ops, T prefix) {
        ListBuilder<T> inventory = ops.listBuilder();

        int size = input.size();
        for (int i = 0; i < size; i++) {
            ItemStack stack = input.getStack(i);
            if (stack.isEmpty()) {
                continue;
            }

            RecordBuilder<T> slot = ops.mapBuilder();
            slot.add("Slot", ops.createInt(i));
            DataResult<T> stackEntry = slot.build(ItemStack.CODEC.encodeStart(ops, stack));

            inventory.add(stackEntry);
        }

        return inventory.build(prefix);
    }

    @Override
    public <T> DataResult<Pair<NetherChestInventoryChannel, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> {
            DataResult<Pair<ItemStack, T>> channel = ItemStack.CODEC.decode(ops, Optional.ofNullable(map.get("channel")).orElse(ops.empty()));
            if (channel.error().isPresent()) {
                return channel.map(x -> x.mapFirst(y -> null));
            }

            return channel.flatMap(x -> decodeItems(ops, Optional.ofNullable(map.get("items")).orElse(ops.empty()), x.getFirst()));
        });
    }

    public <T> DataResult<Pair<NetherChestInventoryChannel, T>> decodeItems(DynamicOps<T> ops, T input, ItemStack key) {
        return decodeItems(ops, input, new NetherChestInventoryChannel(this.inventory, key));
    }

    public <T> DataResult<Pair<NetherChestInventoryChannel, T>> decodeItems(DynamicOps<T> ops, T input, NetherChestInventoryChannel channel) {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(consumer -> {
            channel.clear();

            Stream.Builder<T> failed = Stream.builder();

            MutableObject<DataResult<Unit>> result = new MutableObject<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

            consumer.accept(t -> {
                DataResult<Integer> slotResult = ops.get(t, "Slot").map(x -> ops.getNumberValue(x, -1).intValue());
                DataResult<Pair<ItemStack, T>> stackResult = ItemStack.CODEC.decode(ops, t);
                stackResult.error().ifPresent(e -> failed.add(t));

                result.setValue(result.getValue().apply3((unit, slot, stack) -> {
                    if (slot >= 0 && slot < channel.size()) {
                        channel.setStack(slot, stack.getFirst());
                    }
                    return unit;
                }, slotResult, stackResult));
            });

            T errors = ops.createList(failed.build());
            Pair<NetherChestInventoryChannel, T> pair = Pair.of(channel, errors);

            return result.getValue().map(unit -> pair).setPartial(pair);
        });
    }

    @Override
    public String toString() {
        return "NetherChestInventoryChannelCodec";
    }
}