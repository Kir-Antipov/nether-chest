package dev.kir.netherchest.compat.cloth;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.config.NetherChestConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.util.Identifier;

import java.util.*;

@Config(name = NetherChest.MOD_ID)
public final class NetherChestClothConfig implements NetherChestConfig, ConfigData {
    @ConfigEntry.Gui.Excluded
    private static final NetherChestClothConfig INSTANCE;

    @ConfigEntry.Gui.Excluded
    private static final Map<NetherChestClothConfig, List<Identifier>> BLACKLIST;

    @ConfigEntry.Gui.Excluded
    private static final Map<NetherChestClothConfig, List<Identifier>> WHITELIST;

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

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean ignoreNbtInMultichannelMode = NetherChestConfig.super.ignoreNbtInMultichannelMode();

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean ignoreCountInMultichannelMode = NetherChestConfig.super.ignoreCountInMultichannelMode();

    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Gui.Tooltip(count = 2)
    public List<String> channelBlacklist = new ArrayList<>();

    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Gui.Tooltip(count = 2)
    public List<String> channelWhitelist = new ArrayList<>();

    @Override
    public boolean allowHoppers() {
        return this.allowHoppers;
    }

    @Override
    public boolean allowInsertion() {
        return this.allowInsertion;
    }

    @Override
    public boolean allowExtraction() {
        return this.allowExtraction;
    }

    @Override
    public boolean enableMultichannelMode() {
        return this.enableMultichannelMode;
    }

    @Override
    public boolean ignoreNbtInMultichannelMode() {
        return this.ignoreNbtInMultichannelMode;
    }

    @Override
    public boolean ignoreCountInMultichannelMode() {
        return this.ignoreCountInMultichannelMode;
    }

    @Override
    public List<Identifier> channelBlacklist() {
        if (!BLACKLIST.containsKey(this)) {
            BLACKLIST.put(this, this.channelBlacklist.stream().map(Identifier::tryParse).filter(Objects::nonNull).toList());
        }

        return BLACKLIST.get(this);
    }

    @Override
    public List<Identifier> channelWhitelist() {
        if (!WHITELIST.containsKey(this)) {
            WHITELIST.put(this, this.channelWhitelist.stream().map(Identifier::tryParse).filter(Objects::nonNull).toList());
        }

        return WHITELIST.get(this);
    }

    static {
        INSTANCE = AutoConfig.register(NetherChestClothConfig.class, GsonConfigSerializer::new).getConfig();
        BLACKLIST = new WeakHashMap<>();
        WHITELIST = new WeakHashMap<>();
    }
}
