package io.oneinvest.bond.track;

import org.jetbrains.annotations.NotNull;

public record Position(@NotNull Isin isin, int pos) {
}
