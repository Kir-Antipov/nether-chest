package me.kirantipov.mods.netherchest.config;

import me.kirantipov.mods.netherchest.command.SuggestionProvider;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class OptionManager {
    private static final Map<String, Function<OptionManager, Text>> FORMATTERS = new HashMap<>();
    static {
        BiFunction<Object, Formatting, Text> toText = (x, c) -> new LiteralText(String.valueOf(x)).styled(t -> t.withColor(c));
        FORMATTERS.put("$name", x -> toText.apply(x.getName(), Formatting.GREEN));
        FORMATTERS.put("$value", x -> toText.apply(x.getValue(), Formatting.YELLOW));
        FORMATTERS.put("$defaultValue", x -> toText.apply(x.getDefaultValue(), Formatting.GOLD));
    }

    private final Object instance;
    private final Field field;
    private final String name;
    private final String description;
    private final String result;
    private final SuggestionProvider suggestionProvider;
    private final Object defaultValue;

    public OptionManager(Object instance, Field field, Option annotation) {
        this.instance = instance;
        this.field = field;
        this.name = annotation.name().isEmpty() ? field.getName() : annotation.name();
        this.description = annotation.description();
        this.result = annotation.result();
        this.suggestionProvider = getSuggestionProvider(field.getType(), annotation);
        this.defaultValue = getValue();
    }

    private static SuggestionProvider getSuggestionProvider(Class<?> type, Option annotation) {
        if (annotation.options().length == 0) {
            if (type == boolean.class) {
                return SuggestionProvider.BOOLEAN;
            } else if (type.isEnum()) {
                return SuggestionProvider.ENUM((Class<? extends Enum<?>>)type);
            } else {
                try {
                    return annotation.suggestions().getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    return SuggestionProvider.EMPTY;
                }
            }
        } else {
            return SuggestionProvider.CONSTANTS(annotation.options());
        }
    }

    public String getName() {
        return this.name;
    }

    public SuggestionProvider getSuggestionProvider() {
        return this.suggestionProvider;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public Text formatDescription() {
        return format(this, this.description);
    }

    public Text formatResult() {
        return format(this, this.result);
    }

    private static Text format(OptionManager manager, String value) {
        MutableText result = new LiteralText("");
        for (String key : FORMATTERS.keySet()) {
            int i = value.indexOf(key);
            if (i == -1) {
                continue;
            }
            Function<OptionManager, Text> formatter = FORMATTERS.get(key);
            result = result.append(value.substring(0, i)).append(formatter.apply(manager));
            value = value.substring(i + key.length());
        }
        return result.append(new LiteralText(value));
    }

    public Object getValue() {
        try {
            return this.field.get(instance);
        } catch (IllegalAccessException e) {
            // Java is shit
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public void setValue(String value) {
        Class<?> type = this.field.getType();
        if (type == String.class) {
            this.setValue((Object)value);
        } else if (type == boolean.class) {
            this.setValue(Boolean.parseBoolean(value));
        } else if (type == int.class) {
            this.setValue(Integer.parseInt(value));
        } else if (type == double.class) {
            this.setValue(Double.parseDouble(value));
        } else if (type.isEnum()) {
            this.setValue(Enum.valueOf((Class<? extends Enum>)type, value.toUpperCase(Locale.ROOT)));
        }
    }

    public void setValue(Object value) {
        try {
            this.field.set(instance, value);
        } catch (IllegalAccessException e) {
            // Java is shit
            throw new IllegalStateException(e);
        }
    }

    public void reset() {
        this.setValue(this.defaultValue);
    }
}