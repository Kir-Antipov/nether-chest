package me.kirantipov.mods.netherchest.command;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@FunctionalInterface
public interface SuggestionProvider {
    SuggestionProvider EMPTY = new EmptySuggestionProvider();

    SuggestionProvider BOOLEAN = new BooleanSuggestionProvider();

    SuggestionProvider ITEMS = new ItemSuggestionProvider();

    static <T extends Enum<?>> SuggestionProvider ENUM(Class<T> type) {
        List<String> values = Arrays.stream(type.getEnumConstants())
            .map(x -> x.name().toLowerCase(Locale.ROOT))
            .collect(Collectors.toList());

        return x -> CommandSource.suggestMatching(values, x);
    }

    static SuggestionProvider CONSTANTS(String... values) {
        return x -> CommandSource.suggestMatching(values, x);
    }

    CompletableFuture<Suggestions> suggest(SuggestionsBuilder suggestionsBuilder);
}