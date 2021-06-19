package me.kirantipov.mods.netherchest.command;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.registry.Registry;

import java.util.concurrent.CompletableFuture;

public class ItemSuggestionProvider implements SuggestionProvider {
    @Override
    public CompletableFuture<Suggestions> suggest(SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestIdentifiers(Registry.ITEM.stream().map(Registry.ITEM::getId), suggestionsBuilder);
    }
}