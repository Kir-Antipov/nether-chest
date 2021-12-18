package dev.kir.netherchest.config;

import dev.kir.netherchest.command.EmptySuggestionProvider;
import dev.kir.netherchest.command.SuggestionProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {
    String name() default "";

    String description() default "Rule $name is currently set to: $value";

    String result() default "Rule $name is now set to: $value";

    String[] options() default { };

    Class<? extends SuggestionProvider> suggestions() default EmptySuggestionProvider.class;
}