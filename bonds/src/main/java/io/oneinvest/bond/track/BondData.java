package io.oneinvest.bond.track;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public record BondData(@NotNull Isin isin,
                       @Nullable DohodData dohodData,
                       @Nullable BlackTerminalData blackTerminalData,
                       @Nullable FinPlanData finPlanData,
                       @Nullable MoexData moexData,
                       @Nullable SmartlabData smartlabData) implements BondBasicInfo, BondCouponInfo, BondCashflowInfo {
    @Override
    public @NotNull String name() {
        return firstNonNull(smartlabData, dohodData).name();
    }

    @Override
    public @NotNull String shortname() {
        return firstNonNull(smartlabData, dohodData).shortname();
    }

    @Override
    public @NotNull String board() {
        return firstNonNull(smartlabData, dohodData).board();
    }

    @Override
    public @NotNull String issueDateStr() {
        return firstNonNull(smartlabData, dohodData).issueDateStr();
    }

    @Override
    public @NotNull Date issueDate() {
        return firstNonNull(smartlabData, dohodData).issueDate();
    }

    @Override
    public @NotNull String maturityDateStr() {
        return firstNonNull(smartlabData, dohodData).maturityDateStr();
    }

    @Override
    public @NotNull Date maturityDate() {
        return firstNonNull(smartlabData, dohodData).maturityDate();
    }

    @Override
    public double notional() {
        return firstNonNull(smartlabData, dohodData).notional();
    }

    @Override
    public double couponAbs() {
        return firstNonNull(smartlabData, dohodData).couponAbs();
    }

    @Override
    public double couponYield() {
        return firstNonNull(smartlabData, dohodData).couponYield();
    }

    @Override
    public double couponAbsAnnual() {
        return firstNonNull(smartlabData, dohodData).couponAbsAnnual();
    }

    @Override
    public @NotNull List<Payment> payments() {
        return firstNonNull(moexData, smartlabData).payments();
    }

    private static <T> @NotNull T firstNonNull(@Nullable T item1, @Nullable T item2) {
        if (item1 != null) {
            return item1;
        }
        if (item2 != null) {
            return item2;
        }
        throw new IllegalStateException("All items are null");
    }

    private static <T> @NotNull T firstNonNull(@Nullable T item1, @Nullable T item2, @Nullable T item3) {
        if (item1 != null) {
            return item1;
        }
        if (item2 != null) {
            return item2;
        }
        if (item3 != null) {
            return item3;
        }
        throw new IllegalStateException("All items are null");
    }

    @SafeVarargs
    private static <T> @NotNull T firstNonNull(@Nullable T @NotNull ... items) {
        for (T item : items) {
            if (item != null) {
                return item;
            }
        }
        throw new IllegalStateException("All items are null");
    }
}
