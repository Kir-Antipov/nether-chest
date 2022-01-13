package dev.kir.netherchest.screen;

import dev.kir.netherchest.NetherChest;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class NetherChestScreenHandlerTypes {
    public static final ScreenHandlerType<NetherChestScreenHandler> NETHER_CHEST = register("nether_chest", NetherChestScreenHandler::new);

    public static void init() { }

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerRegistry.SimpleClientHandlerFactory<T> factory) {
        return register(NetherChest.locate(id), factory);
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(Identifier id, ScreenHandlerRegistry.SimpleClientHandlerFactory<T> factory) {
        return ScreenHandlerRegistry.registerSimple(id, factory);
    }
}
