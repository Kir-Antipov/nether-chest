package dev.kir.netherchest.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;

import java.util.Optional;

final class ItemStackCodec {
    // TODO: Check if those clowns remembered to place a null check, so ItemStack.Empty can be actually serialized/deserialized.
    public static final Codec<ItemStack> INSTANCE = RecordCodecBuilder.create(instance -> instance.group(
        Registries.ITEM.getCodec().fieldOf("id").forGetter(ItemStack::getItem),
        Codec.INT.fieldOf("Count").forGetter(ItemStack::getCount),
        NbtCompound.CODEC.optionalFieldOf("tag").forGetter(stack -> Optional.ofNullable(stack.getNbt()))
    ).apply(instance, (id, count, nbt) -> {
        ItemStack stack = new ItemStack(id, count);
        nbt.ifPresent(stack::setNbt);
        return stack;
    }));
}