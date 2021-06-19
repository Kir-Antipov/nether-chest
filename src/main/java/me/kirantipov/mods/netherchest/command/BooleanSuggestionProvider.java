package me.kirantipov.mods.netherchest.command;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;

import java.util.concurrent.CompletableFuture;

public class BooleanSuggestionProvider implements SuggestionProvider {
    private static final String[] BOOLEANS = {"true", "false"};

    @Override
    public CompletableFuture<Suggestions> suggest(SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestMatching(BOOLEANS, suggestionsBuilder);
    }
}