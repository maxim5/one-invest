package io.oneinvest.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class Parsing {
    @SafeVarargs
    public static @NotNull String apply(@NotNull String input, @NotNull Function<String, String> @NotNull ... chain) {
        String result = input;
        for (Function<String, String> function : chain) {
            result = function.apply(result);
        }
        return result;
    }

    public static @NotNull String extractBetween(@NotNull String input, @NotNull String from, @NotNull String to) {
        int i = input.indexOf(from);
        ParseException.assure(i >= 0, "Parsing failed: from not found `%s`", from);
        i += from.length();
        int j = input.indexOf(to, i);
        ParseException.assure(j >= 0, "Parsing failed: to not found `%s`", to);
        return input.substring(i, j);
    }

    public static @NotNull String extractBetween(@NotNull String input, @NotNull String from, @NotNull String to,
                                                 @NotNull Function<String, String> callback) {
        String result = extractBetween(input, from, to);
        return callback.apply(result);
    }

    public static @NotNull Function<String, String> extractBetween(@NotNull String from, @NotNull String to) {
        return input -> extractBetween(input, from, to);
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
}
