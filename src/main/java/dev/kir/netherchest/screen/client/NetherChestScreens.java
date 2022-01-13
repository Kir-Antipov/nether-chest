package dev.kir.netherchest.screen.client;

import dev.kir.netherchest.screen.NetherChestScreenHandlerTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

@Environment(EnvType.CLIENT)
public final class NetherChestScreens {
    static {
        register(NetherChestScreenHandlerTypes.NETHER_CHEST, NetherChestScreen::new);
    }

    public static void initClient() { }

    private static <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void register(ScreenHandlerType<? extends H> type, ScreenRegistry.Factory<H, S> screenFactory) {
        ScreenRegistry.register(type, screenFactory);
    }
}
