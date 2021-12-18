package dev.kir.netherchest;

import dev.kir.netherchest.command.ItemSuggestionProvider;
import dev.kir.netherchest.config.Option;

public class NetherChestConfig {
    @Option(suggestions = ItemSuggestionProvider.class)
    public String coreItem = "minecraft:nether_star";

    @Option
    public boolean allowRedstoneIntegration = false;

    @Option
    public boolean updateNeighborsEveryTick = false;
}