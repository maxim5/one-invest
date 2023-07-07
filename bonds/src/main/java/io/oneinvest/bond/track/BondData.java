package io.oneinvest.bond.track;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record BondData(@NotNull Isin isin,
                       @Nullable DohodData dohodData,
                       @Nullable BlackTerminalData blackTerminalData,
                       @Nullable FinPlanData finPlanData) {
}
