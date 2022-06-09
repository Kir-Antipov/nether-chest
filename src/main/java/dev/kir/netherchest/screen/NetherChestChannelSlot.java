package dev.kir.netherchest.screen;

import dev.kir.netherchest.NetherChest;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public final class NetherChestChannelSlot extends Slot {
    public NetherChestChannelSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return NetherChest.getConfig().isValidChannel(stack);
    }
}
