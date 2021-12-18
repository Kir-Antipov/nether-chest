package dev.kir.netherchest.server;

import net.minecraft.server.MinecraftServer;

@FunctionalInterface
public interface ServerShutdownListener {
    void onServerShutdown(MinecraftServer server);
}