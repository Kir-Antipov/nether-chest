package me.kirantipov.mods.netherchest;

import me.kirantipov.mods.netherchest.block.NetherChestBlocks;
import me.kirantipov.mods.netherchest.block.entity.NetherChestBlockEntities;
import me.kirantipov.mods.netherchest.client.render.NetherChestRenderers;
import me.kirantipov.mods.netherchest.item.NetherChestItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class NetherChest implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "netherchest";

    public static Identifier locate(String location) {
        return new Identifier(MOD_ID, location);
    }

    @Override
    public void onInitialize() {
        NetherChestBlocks.init();
        NetherChestItems.init();
        NetherChestBlockEntities.init();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        NetherChestRenderers.initClient();
    }
}