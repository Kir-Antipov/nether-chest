package me.kirantipov.mods.netherchest;

import me.kirantipov.mods.netherchest.command.ItemSuggestionProvider;
import me.kirantipov.mods.netherchest.config.Option;

public class NetherChestConfig {
    @Option(suggestions = ItemSuggestionProvider.class)
    public String coreItem = "minecraft:nether_star";

    @Option
    public boolean allowRedstoneIntegration = false;

    @Option
    public boolean updateNeighborsEveryTick = false;
}