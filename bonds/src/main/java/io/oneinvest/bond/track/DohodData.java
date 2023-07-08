package io.oneinvest.bond.track;

import io.oneinvest.util.TimeSeries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public record DohodData(@NotNull Isin isin,
                        @NotNull TimeSeries dailyPrices,
                        @NotNull DohodBondMap bond,
                        @NotNull List<DohodBondMap> replacements) implements BondBasicInfo, BondCouponInfo {
    @Override
    public @NotNull String name() {
        return bond.name();
    }

    @Override
    public @NotNull String shortname() {
        return bond.shortname();
    }

    @Override
    public @NotNull String issueDate() {
        return Objects.requireNonNull(bond.issueDate());
    }

    @Override
    public @NotNull String maturityDate() {
        return Objects.requireNonNull(bond.maturityDate());
    }

    @Override
    public double notional() {
        return bond.nominal();
    }

    @Override
    public double couponAbs() {
        return bond.couponvalue();
    }

    @Override
    public double couponYield() {
        return bond.coupon();
    }

    @Override
    public double couponAbsAnnual() {
        return 0;
    }
}
