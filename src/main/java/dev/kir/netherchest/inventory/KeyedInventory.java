package dev.kir.netherchest.inventory;

import com.mojang.serialization.Codec;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public interface KeyedInventory extends Inventory {
    static <T extends KeyedInventory> Codec<T> codec(Function<ItemStack, T> factory) {
        return new KeyedInventoryCodec<>(factory);
    }

    static KeyedInventory wrap(KeyedInventory inventory, ItemStack key) {
        if (ItemStack.areEqual(inventory.getKey(), key)) {
            return inventory;
        }

        return new KeyedInventoryWrapper(inventory, key);
    }

    default Identifier getId() {
        return Registries.ITEM.getId(this.getKey().getItem());
    }

    default boolean isOf(ItemStack key) {
        return ItemStack.areEqual(this.getKey(), key);
    }

    default int getComparatorOutput() {
        return ScreenHandler.calculateComparatorOutput(this);
    }

    ItemStack getKey();

    boolean isOpen();

    boolean open();

    boolean close();

    void addListener(InventoryChangedListener listener);

    void removeListener(InventoryChangedListener listener);
}