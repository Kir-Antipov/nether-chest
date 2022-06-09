package dev.kir.netherchest;

import dev.kir.netherchest.block.NetherChestBlocks;
import dev.kir.netherchest.block.entity.NetherChestBlockEntities;
import dev.kir.netherchest.client.render.NetherChestRenderers;
import dev.kir.netherchest.config.NetherChestConfig;
import dev.kir.netherchest.item.NetherChestItems;
import dev.kir.netherchest.screen.NetherChestScreenHandlerTypes;
import dev.kir.netherchest.screen.client.NetherChestScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class NetherChest implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "netherchest";
    private static final NetherChestConfig CONFIG = NetherChestConfig.DEFAULT;

    public static Identifier locate(String location) {
        return new Identifier(MOD_ID, location);
    }

    public static NetherChestConfig getConfig() {
        return CONFIG;
    }

    @Override
    public void onInitialize() {
        NetherChestBlocks.init();
        NetherChestItems.init();
        NetherChestScreenHandlerTypes.init();
        NetherChestBlockEntities.init();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        NetherChestScreens.initClient();
        NetherChestRenderers.initClient();
    }
}