package me.kirantipov.mods.netherchest.config;

@FunctionalInterface
public interface ConfigChangedListener<T> {
    void onConfigChanged(T sender);
}