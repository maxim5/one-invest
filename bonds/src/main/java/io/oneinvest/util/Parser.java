package io.oneinvest.util;

import com.google.common.flogger.FluentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

public class Parser {
    private static final FluentLogger log = FluentLogger.forEnclosingClass();

    private final ErrorHandling errorHandling;

    public Parser(@NotNull ErrorHandling errorHandling) {
        this.errorHandling = errorHandling;
    }

    public @NotNull Parser at(@NotNull ErrorHandling errorHandling) {
        return new Parser(errorHandling);
    }

    @SafeVarargs
    public static @NotNull String apply(@NotNull String input, @NotNull Function<String, String> @NotNull ... chain) {
        String result = input;
        for (Function<String, String> function : chain) {
            result = function.apply(result);
        }
        return result;
    }

    @SafeVarargs
    public final @NotNull String applyOrEmpty(@NotNull String input, @NotNull Function<String, String> @NotNull ... chain) {
        try {
            return apply(input, chain);
        } catch (Throwable e) {
            handleError(e, log -> log.log("Failed to parse the input string"));
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

    public @NotNull String extractBetweenOrEmpty(@NotNull String input, @NotNull String from, @NotNull String to) {
        try {
            return extractBetween(input, from, to);
        } catch (Throwable e) {
            handleError(e, log -> log.log("Failed to parse the input string"));
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

    public static @NotNull List<String> extractAll(@NotNull String input, @NotNull String from, @NotNull String to) {
        List<String> result = new ArrayList<>();
        int i = 0;
        while (true) {
            i = input.indexOf(from, i);
            if (i < 0) {
                break;
            }
            i += from.length();
            int j = input.indexOf(to, i);
            if (j < 0) {
                break;
            }
            String extracted = input.substring(i, j);
            result.add(extracted);
            i = j + to.length();
        }
        return result;
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

    public int parseInt(@NotNull String s, int def) {
        if (s.isEmpty()) {
            return def;
        }
        try {
            return Integer.parseInt(s.trim().replaceAll("[^0-9.+-]", ""));
        } catch (NumberFormatException e) {
            handleError(e, log -> log.log("Failed to parse the integer: `%s`", s));
            return def;
        }
    }

    public double parseDouble(@NotNull String s, double def) {
        if (s.isEmpty()) {
            return def;
        }
        try {
            return Double.parseDouble(s.trim().replaceAll("[^0-9.+-]", ""));
        } catch (NumberFormatException e) {
            handleError(e, log -> log.log("Failed to parse the double: `%s`", s));
            return def;
        }
    }

    public static final Date NO_DATE = new Date(0);

    public @NotNull Date parseDate(@NotNull DateFormat format, @NotNull String s, @NotNull Date def) {
        try {
            return format.parse(s);
        } catch (java.text.ParseException e) {
            handleError(e, log -> log.log("Failed to parse date: `%s`", s));
            return def;
        }
    }

    public @Nullable Date parseDate(@NotNull DateFormat format, @NotNull String s) {
        try {
            return format.parse(s);
        } catch (java.text.ParseException e) {
            handleError(e, log -> log.log("Failed to parse date: `%s`", s));
            return null;
        }
    }

    private void handleError(@NotNull Throwable e, @NotNull Consumer<FluentLogger.Api> consumer) {
        switch (errorHandling) {
            case SKIP -> {}
            case LOG -> consumer.accept(log.at(Level.FINE));
            case INFO -> consumer.accept(log.at(Level.INFO).withCause(e));
            case WARN -> consumer.accept(log.at(Level.WARNING).withCause(e));
            case FAIL -> throwAny(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwAny(Throwable exception) throws T {
        throw (T) exception;
    }

    public enum ErrorHandling {
        SKIP,
        LOG,
        INFO,
        WARN,
        FAIL,
    }
}
