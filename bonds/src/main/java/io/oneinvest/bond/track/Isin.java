package io.oneinvest.bond.track;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public record Isin(@NotNull String value) {
    private static final Pattern ISIN_PATTERN = Pattern.compile("[A-Z]{2}[A-Z0-9]{10}");

    public static @NotNull Isin of(@NotNull String value) {
        String clean = clear(value);
        assert ISIN_PATTERN.matcher(clean).matches() : "Isin is invalid: " + value;
        return new Isin(clean);
    }

    public boolean matches(@NotNull String isin) {
        return value.equals(clear(isin));
    }

    @Override
    public String toString() {
        return value;
    }

    private static @NotNull String clear(@NotNull String value) {
        return value.toUpperCase().trim();
    }
}
