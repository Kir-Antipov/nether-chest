package dev.kir.netherchest.server;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public final class ServerShutdownListeners {
    private static final Set<ServerShutdownListener> listeners = new HashSet<>();

    public static ImmutableSet<ServerShutdownListener> getListeners() {
        return ImmutableSet.copyOf(listeners);
    }

    public static void addListener(ServerShutdownListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(ServerShutdownListener listener) {
        listeners.remove(listener);
    }
}