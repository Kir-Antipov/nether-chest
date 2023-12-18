package dev.kir.netherchest.config;

import dev.kir.netherchest.compat.cloth.NetherChestClothConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public interface NetherChestConfig {
    NetherChestConfig DEFAULT = FabricLoader.getInstance().isModLoaded("cloth-config") ? NetherChestClothConfig.getInstance() : new NetherChestConfig() { };

    default boolean allowHoppers() {
        return true;
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

    default boolean ignoreNbtInMultichannelMode() {
        return false;
    }

    default boolean ignoreCountInMultichannelMode() {
        return false;
    }

    default List<Identifier> channelBlacklist() {
        return List.of();
    }

    default List<Identifier> channelWhitelist() {
        return List.of();
    }

    default boolean isValidChannel(ItemStack itemStack) {
        return this.isValidChannel(itemStack.getItem());
    }

    default boolean isValidChannel(Item item) {
        return this.isValidChannel(Registries.ITEM.getId(item));
    }

    default boolean isValidChannel(Identifier id) {
        List<Identifier> whitelist = this.channelWhitelist();
        if (whitelist.size() != 0 && !whitelist.contains(id)) {
            return false;
        }

        List<Identifier> blacklist = this.channelBlacklist();
        return blacklist.size() == 0 || !blacklist.contains(id);
    }
}
