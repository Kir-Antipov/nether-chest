package dev.kir.netherchest.config;

@FunctionalInterface
public interface ConfigChangedListener<T> {
    void onConfigChanged(T sender);
}