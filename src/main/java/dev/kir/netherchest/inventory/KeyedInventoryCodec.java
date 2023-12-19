package dev.kir.netherchest.inventory;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

final class KeyedInventoryCodec<TInventory extends KeyedInventory> implements Codec<TInventory> {
    private static final String KEY = "channel";
    private static final String ITEMS = "items";
    private static final String SLOT = "Slot";

    private final Function<ItemStack, TInventory> factory;

    public KeyedInventoryCodec(Function<ItemStack, TInventory> factory) {
        this.factory = factory;
    }

    @Override
    public <TFormat> DataResult<TFormat> encode(TInventory input, DynamicOps<TFormat> ops, TFormat prefix) {
        RecordBuilder<TFormat> channel = ops.mapBuilder();
        channel.add(KEY, ItemStackCodec.INSTANCE.encode(input.getKey(), ops, ops.empty()));
        channel.add(ITEMS, encodeItems(input, ops, ops.empty()));
        return channel.build(prefix);
    }

    public <TFormat> DataResult<TFormat> encodeItems(TInventory input, DynamicOps<TFormat> ops, TFormat prefix) {
        ListBuilder<TFormat> inventory = ops.listBuilder();

        int size = input.size();
        for (int i = 0; i < size; i++) {
            ItemStack stack = input.getStack(i);
            if (stack.isEmpty()) {
                continue;
            }

            RecordBuilder<TFormat> slot = ops.mapBuilder();
            slot.add(SLOT, ops.createInt(i));
            DataResult<TFormat> stackEntry = slot.build(ItemStack.CODEC.encodeStart(ops, stack));

            inventory.add(stackEntry);
        }

        return inventory.build(prefix);
    }

    @Override
    public <TFormat> DataResult<Pair<TInventory, TFormat>> decode(DynamicOps<TFormat> ops, TFormat input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> {
            DataResult<Pair<ItemStack, TFormat>> channel = ItemStack.CODEC.decode(ops, Optional.ofNullable(map.get(KEY)).orElse(ops.empty()));
            if (channel.error().isPresent()) {
                return channel.map(x -> x.mapFirst(y -> null));
            }

            return channel.flatMap(x -> decodeItems(ops, Optional.ofNullable(map.get(ITEMS)).orElse(ops.empty()), x.getFirst()));
        });
    }

    public <TFormat> DataResult<Pair<TInventory, TFormat>> decodeItems(DynamicOps<TFormat> ops, TFormat input, ItemStack key) {
        return decodeItems(ops, input, this.factory.apply(key));
    }

    public <TFormat> DataResult<Pair<TInventory, TFormat>> decodeItems(DynamicOps<TFormat> ops, TFormat input, TInventory inventory) {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(list -> {
            Stream.Builder<TFormat> failed = Stream.builder();
            MutableObject<DataResult<Unit>> result = new MutableObject<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

            inventory.clear();

            list.accept(entry -> {
                DataResult<Integer> slotResult = ops.get(entry, SLOT).map(x -> ops.getNumberValue(x, -1).intValue());
                DataResult<Pair<ItemStack, TFormat>> stackResult = ItemStack.CODEC.decode(ops, entry);
                stackResult.error().ifPresent(e -> failed.add(entry));

                result.setValue(result.getValue().apply3((unit, slot, stack) -> {
                    if (slot >= 0 && slot < inventory.size()) {
                        inventory.setStack(slot, stack.getFirst());
                    }
                    return unit;
                }, slotResult, stackResult));
            });

            TFormat errors = ops.createList(failed.build());
            Pair<TInventory, TFormat> pair = Pair.of(inventory, errors);

            return result.getValue().map(unit -> pair).setPartial(pair);
        });
    }

    @Override
    public String toString() {
        return KeyedInventoryCodec.class.getSimpleName();
    }
}