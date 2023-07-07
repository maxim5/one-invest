package io.oneinvest.bond.track;

import io.oneinvest.util.TimeSeries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DohodData(@NotNull Isin isin,
                        @NotNull TimeSeries dailyPrices,
                        @NotNull DohodBondMap bond,
                        @NotNull List<DohodBondMap> replacements) {
}
