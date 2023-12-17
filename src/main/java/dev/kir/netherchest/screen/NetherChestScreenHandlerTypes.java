package dev.kir.netherchest.screen;

import dev.kir.netherchest.NetherChest;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class NetherChestScreenHandlerTypes {
    public static final ScreenHandlerType<NetherChestScreenHandler> NETHER_CHEST = register("nether_chest", NetherChestScreenHandler::new);

    public static void init() { }

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return register(NetherChest.locate(id), factory);
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(Identifier id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory));
    }
}
