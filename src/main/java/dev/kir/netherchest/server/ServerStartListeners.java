package dev.kir.netherchest.server;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public final class ServerStartListeners {
    private static final Set<ServerStartListener> listeners = new HashSet<>();

    public static ImmutableSet<ServerStartListener> getListeners() {
        return ImmutableSet.copyOf(listeners);
    }

    public static void addListener(ServerStartListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(ServerStartListener listener) {
        listeners.remove(listener);
    }
}