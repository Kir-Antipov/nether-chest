package dev.kir.netherchest.config;

import dev.kir.netherchest.compat.cloth.NetherChestClothConfig;
import net.fabricmc.loader.api.FabricLoader;

public interface NetherChestConfig {
    NetherChestConfig DEFAULT = FabricLoader.getInstance().isModLoaded("cloth-config") ? NetherChestClothConfig.getInstance() : new NetherChestConfig() { };

    default boolean allowHoppers() {
        return false;
    }

    default boolean allowInsertion() {
        return true;
    }

    default boolean allowExtraction() {
        return true;
    }

    default boolean enableMultichannelMode() {
        return true;
    }
}
