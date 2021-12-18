package dev.kir.netherchest;

import dev.kir.netherchest.block.NetherChestBlocks;
import dev.kir.netherchest.block.entity.NetherChestBlockEntities;
import dev.kir.netherchest.client.render.NetherChestRenderers;
import dev.kir.netherchest.config.NetherChestConfig;
import dev.kir.netherchest.item.NetherChestItems;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
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

    public static NetherChestConfig getConfig() {
        return AutoConfig.getConfigHolder(NetherChestConfig.class).getConfig();
    }

    @Override
    public void onInitialize() {
        AutoConfig.register(NetherChestConfig.class, GsonConfigSerializer::new);
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