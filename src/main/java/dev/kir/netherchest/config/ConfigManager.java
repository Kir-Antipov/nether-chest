package dev.kir.netherchest.config;

import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.loader.lib.gson.JsonReader;
import net.fabricmc.loader.lib.gson.JsonToken;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigManager<T> {
    public static final String CONFIG_EXTENSION = ".json";
    public static final String CONFIG_DIRECTORY = "config";

    public final String id;
    private final T config;
    private final Map<String, OptionManager> options;
    private final Set<ConfigChangedListener<T>> listeners;

    public ConfigManager(String id, Supplier<T> factory) {
        this.id = id;
        this.config = factory.get();
        this.options = parseOptions(this.config).collect(Collectors.toMap(OptionManager::getName, x -> x));
        this.listeners = new HashSet<>();
    }

    public T getConfig() {
        return this.config;
    }

    public OptionManager getOptionByName(String name) {
        return this.options.getOrDefault(name, null);
    }

    public void load(MinecraftServer server) {
        for (OptionManager option : this.options.values()) {
            option.reset();
        }

        Path path = getConfigPath(server);
        try {
            JsonReader reader = new JsonReader(Files.newBufferedReader(path));
            if (reader.peek() != JsonToken.BEGIN_OBJECT) {
                return;
            }
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                String value = reader.nextString();
                OptionManager option = this.options.getOrDefault(name, null);
                if (option != null) {
                    option.setValue(value);
                }
            }
            reader.close();
        } catch (Exception e) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ie) {
                // Java is shit
            }
        }

        pingListeners();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save(MinecraftServer server) {
        Path path = getConfigPath(server);
        try {
            path.getParent().toFile().mkdirs();
            JsonWriter writer = new JsonWriter(Files.newBufferedWriter(path));
            writer.setIndent("  ");
            writer.beginObject();
            for (OptionManager option : options.values()) {
                writer.name(option.getName());
                writer.value(String.valueOf(option.getValue()));
            }
            writer.endObject();
            writer.close();
        } catch (Exception e) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ie) {
                // Java is shit
            }
        }
    }

    public void addListener(ConfigChangedListener<T> listener) {
        this.listeners.add(listener);
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }

    private void pingListeners() {
        for (ConfigChangedListener<T> listener : this.listeners) {
            listener.onConfigChanged(this.config);
        }
    }

    public void registerCommands(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> commandBuilder = LiteralArgumentBuilder.literal(this.id);
        commandBuilder
            .requires(ConfigManager::hasPermissions)
            .then(CommandManager.argument("option", StringArgumentType.word())
                .requires(ConfigManager::hasPermissions)
                .suggests((a, b) -> CommandSource.suggestMatching(this.options.keySet(), b))
                .executes(this::displayDescription)
                .then(CommandManager.argument("value", StringArgumentType.greedyString())
                    .requires(ConfigManager::hasPermissions)
                    .suggests(this::getSuggestionsForCommandContext)
                    .executes(this::updateOption)
                )
            );
        commandDispatcher.register(commandBuilder);
    }

    private CompletableFuture<Suggestions> getSuggestionsForCommandContext(CommandContext<ServerCommandSource> commandContext, SuggestionsBuilder builder) {
        OptionManager option = this.options.getOrDefault(StringArgumentType.getString(commandContext, "option"), null);
        return option == null ? builder.buildFuture() : option.getSuggestionProvider().suggest(builder);
    }

    private int displayDescription(CommandContext<ServerCommandSource> commandContext) {
        ServerCommandSource source = commandContext.getSource();
        String optionName = StringArgumentType.getString(commandContext, "option");

        OptionManager option = this.options.getOrDefault(optionName, null);
        if (option == null) {
            return 0;
        }

        source.sendFeedback(option.formatDescription(), false);
        return 1;
    }

    private int updateOption(CommandContext<ServerCommandSource> commandContext) {
        ServerCommandSource source = commandContext.getSource();
        String optionName = StringArgumentType.getString(commandContext, "option");
        String value = StringArgumentType.getString(commandContext, "value");

        OptionManager option = this.options.getOrDefault(optionName, null);
        if (option == null) {
            return 0;
        }

        option.setValue(value);
        pingListeners();

        source.sendFeedback(option.formatResult(), true);
        return 1;
    }

    private Path getConfigPath(MinecraftServer server) {
        return server.getSavePath(WorldSavePath.ROOT).resolve(CONFIG_DIRECTORY).resolve(this.id + CONFIG_EXTENSION);
    }

    private static boolean hasPermissions(ServerCommandSource source) {
        final int OP_LEVEL = 2;
        return source.hasPermissionLevel(OP_LEVEL);
    }

    private static Stream<OptionManager> parseOptions(Object config) {
        return Arrays.stream(config.getClass().getFields())
            .filter(field -> field.getAnnotation(Option.class) != null)
            .map(field -> new OptionManager(config, field, field.getAnnotation(Option.class)));
    }
}