package io.oneinvest.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParseException extends RuntimeException {
    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    public static void assure(boolean cond, @NotNull String message, @Nullable Object @NotNull ... args) {
        if (!cond) {
            throw new ParseException(message.formatted(args));
        }
    }

    public static void failIf(boolean cond, @NotNull String message, @Nullable Object @NotNull ... args) {
        if (cond) {
            throw new ParseException(message.formatted(args));
        }
    }
}
