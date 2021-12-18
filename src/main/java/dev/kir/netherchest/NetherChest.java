package dev.kir.netherchest;

import dev.kir.netherchest.server.ServerShutdownListeners;
import dev.kir.netherchest.server.ServerStartListeners;
import dev.kir.netherchest.block.NetherChestBlocks;
import dev.kir.netherchest.block.entity.NetherChestBlockEntities;
import dev.kir.netherchest.client.render.NetherChestRenderers;
import dev.kir.netherchest.config.ConfigManager;
import dev.kir.netherchest.item.NetherChestItems;
import dev.kir.netherchest.recipe.NetherChestRecipes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public class NetherChest implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "netherchest";
    public static final ConfigManager<NetherChestConfig> CONFIG_MANAGER = new ConfigManager<>(MOD_ID, NetherChestConfig::new);

    public static Identifier locate(String location) {
        return new Identifier(MOD_ID, location);
    }

    public static NetherChestConfig getConfig() {
        return CONFIG_MANAGER.getConfig();
    }

    @Override
    public void onInitialize() {
        NetherChestBlocks.init();
        NetherChestItems.init();
        NetherChestBlockEntities.init();
        ServerStartListeners.addListener(NetherChest::onServerStart);
        ServerShutdownListeners.addListener(NetherChest::onServerStop);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        NetherChestRenderers.initClient();
    }

    private static void onServerStart(MinecraftServer server) {
        CONFIG_MANAGER.removeAllListeners();
        CONFIG_MANAGER.load(server);
        CONFIG_MANAGER.registerCommands(server.getCommandManager().getDispatcher());
        NetherChestRecipes.init(server.getRecipeManager());
        CONFIG_MANAGER.addListener(x -> NetherChestRecipes.init(server.getRecipeManager()));
    }

    private static void onServerStop(MinecraftServer server) {
        CONFIG_MANAGER.save(server);
    }
}