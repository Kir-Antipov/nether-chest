package me.kirantipov.mods.netherchest.util;

import me.kirantipov.mods.netherchest.inventory.NetherChestInventory;
import me.kirantipov.mods.netherchest.inventory.NetherChestInventoryHolder;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldProperties;

public final class InventoryUtil {
    public static NetherChestInventory getNetherChestInventory(WorldAccess world) {
        if (!(world instanceof World) || world.isClient()) {
            return null;
        }

        WorldProperties properties = ((World)world).getServer().getOverworld().getLevelProperties();
        if (!(properties instanceof NetherChestInventoryHolder)) {
            return null;
        }

        return ((NetherChestInventoryHolder)properties).getNetherChestInventory();
    }
}