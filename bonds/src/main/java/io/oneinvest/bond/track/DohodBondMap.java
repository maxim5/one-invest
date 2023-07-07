package io.oneinvest.bond.track;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DohodBondMap(@NotNull String isin,
                           @NotNull String name,
                           @NotNull String shortname,
                           @NotNull String emitent,
                           @NotNull String boardid,

                           double faceValue,
                           double nominal,
                           @NotNull String currency,

                           @Nullable String issueDate,
                           @Nullable String effectiveDate,
                           @Nullable String maturityDate,
                           double duration,

                           @NotNull String liquidity,
                           @Nullable String akra,
                           int creditRating,
                           @Nullable String creditRatingText,
                           double quality,

                           double accruedint,
                           double coupon,
                           double couponvalue,
                           int couponperiod,
                           double yield,
                           double totalReturn) {
    public boolean matches(@NotNull Isin isin) {
        return isin.matches(this.isin);
    }
}
