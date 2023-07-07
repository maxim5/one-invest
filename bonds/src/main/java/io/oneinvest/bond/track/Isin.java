package io.oneinvest.bond.track;

import org.jetbrains.annotations.NotNull;

public record Isin(@NotNull String value) {
    public static @NotNull Isin of(@NotNull String value) {
        return new Isin(clear(value));
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
