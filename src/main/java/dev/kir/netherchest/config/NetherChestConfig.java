package dev.kir.netherchest.config;

import dev.kir.netherchest.NetherChest;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = NetherChest.MOD_ID)
public class NetherChestConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean allowHoppers = false;
}
