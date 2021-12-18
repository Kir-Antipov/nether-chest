package dev.kir.netherchest.server;

import net.minecraft.server.MinecraftServer;

@FunctionalInterface
public interface ServerStartListener {
    void onServerStart(MinecraftServer server);
}