package dev.kir.netherchest.compat.cloth;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.config.NetherChestConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = NetherChest.MOD_ID)
public final class NetherChestClothConfig implements NetherChestConfig, ConfigData {
    @ConfigEntry.Gui.Excluded
    private static final NetherChestClothConfig INSTANCE;

    public static NetherChestClothConfig getInstance() {
        return INSTANCE;
    }

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean allowHoppers = NetherChestConfig.super.allowHoppers();

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean allowInsertion = NetherChestConfig.super.allowInsertion();

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean allowExtraction = NetherChestConfig.super.allowExtraction();

    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean enableMultichannelMode = NetherChestConfig.super.enableMultichannelMode();

    public boolean allowHoppers() {
        return this.allowHoppers;
    }

    public boolean allowInsertion() {
        return this.allowInsertion;
    }

    public boolean allowExtraction() {
        return this.allowExtraction;
    }

    public boolean enableMultichannelMode() {
        return this.enableMultichannelMode;
    }

    static {
        INSTANCE = AutoConfig.register(NetherChestClothConfig.class, GsonConfigSerializer::new).getConfig();
    }
}
