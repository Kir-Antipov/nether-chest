package dev.kir.netherchest.util;

import dev.kir.netherchest.inventory.NetherChestInventory;
import dev.kir.netherchest.inventory.NetherChestInventoryHolder;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldProperties;

public final class InventoryUtil {
    public static NetherChestInventory getNetherChestInventory(WorldAccess world) {
        if (!(world instanceof World) || world.isClient()) {
            return null;
        }

        WorldProperties properties = world.getServer().getOverworld().getLevelProperties();
        if (!(properties instanceof NetherChestInventoryHolder)) {
            return null;
        }

        return ((NetherChestInventoryHolder)properties).getNetherChestInventory();
    }
}