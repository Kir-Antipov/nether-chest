package me.kirantipov.mods.netherchest.command;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.kirantipov.mods.netherchest.command.SuggestionProvider;

import java.util.concurrent.CompletableFuture;

public class EmptySuggestionProvider implements SuggestionProvider {
    @Override
    public CompletableFuture<Suggestions> suggest(SuggestionsBuilder suggestionsBuilder) {
        return suggestionsBuilder.buildFuture();
    }
}