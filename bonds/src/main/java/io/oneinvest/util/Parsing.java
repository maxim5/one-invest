package io.oneinvest.util;

import com.google.common.flogger.FluentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class Parsing {
    private static final FluentLogger log = FluentLogger.forEnclosingClass();

    @SafeVarargs
    public static @NotNull String apply(@NotNull String input, @NotNull Function<String, String> @NotNull ... chain) {
        String result = input;
        for (Function<String, String> function : chain) {
            result = function.apply(result);
        }
        return result;
    }

    @SafeVarargs
    public static @NotNull String applyOrEmpty(@NotNull String input, @NotNull Function<String, String> @NotNull ... chain) {
        try {
            return apply(input, chain);
        } catch (Throwable e) {
            log.atWarning().withCause(e).log("Failed to parse the input string");
            return "";
        }
    }

    public static @NotNull String extractBetween(@NotNull String input, @NotNull String from, @NotNull String to) {
        int i = input.indexOf(from);
        ParseException.assure(i >= 0, "Parsing failed: from not found `%s`", from);
        i += from.length();
        int j = input.indexOf(to, i);
        ParseException.assure(j >= 0, "Parsing failed: to not found `%s`", to);
        return input.substring(i, j);
    }

    public static @NotNull String extractBetweenOrEmpty(@NotNull String input, @NotNull String from, @NotNull String to) {
        try {
            return extractBetween(input, from, to);
        } catch (Throwable e) {
            log.atWarning().withCause(e).log("Failed to parse the input string");
            return "";
        }
    }

    public static @NotNull String extractBetween(@NotNull String input, @NotNull String from, @NotNull String to,
                                                 @NotNull Function<String, String> callback) {
        String result = extractBetween(input, from, to);
        return callback.apply(result);
    }

    public static @NotNull Function<String, String> extractBetween(@NotNull String from, @NotNull String to) {
        return input -> extractBetween(input, from, to);
    }

    public static @NotNull String extractAfter(@NotNull String input, @NotNull String from) {
        int i = input.indexOf(from);
        ParseException.assure(i >= 0, "Parsing failed: from not found `%s`", from);
        i += from.length();
        return input.substring(i);
    }

    public static @NotNull String extractAfter(@NotNull String input, @NotNull String from,
                                                 @NotNull Function<String, String> callback) {
        String result = extractAfter(input, from);
        return callback.apply(result);
    }

    public static @NotNull Function<String, String> extractAfter(@NotNull String from) {
        return input -> extractAfter(input, from);
    }

    public static int countOccurrences(@Nullable String str, @Nullable String sub) {
        if (str == null || str.length() == 0 || sub == null || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int i = 0;
        while ((i = str.indexOf(sub, i)) != -1) {
            count++;
            i += sub.length();
        }
        return count;
    }

    public static int parseInt(@NotNull String s, int def) {
        try {
            return Integer.parseInt(s.trim().replaceAll("[^0-9.+-]", ""));
        } catch (NumberFormatException e) {
            log.atWarning().withCause(e).log("Failed to parse the integer: `%s`", s);
            return def;
        }
    }

    public static double parseDouble(@NotNull String s, double def) {
        try {
            return Double.parseDouble(s.trim().replaceAll("[^0-9.+-]", ""));
        } catch (NumberFormatException e) {
            log.atWarning().withCause(e).log("Failed to parse the double: `%s`", s);
            return def;
        }
    }
}
