package io.oneinvest.bond.track;

import io.oneinvest.util.Parser;
import io.oneinvest.util.Parser.ErrorHandling;
import io.oneinvest.util.TimeSeries;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public record DohodData(@NotNull Isin isin,
                        @NotNull TimeSeries dailyPrices,
                        @NotNull DohodBondMap bond,
                        @NotNull List<DohodBondMap> replacements) implements BondBasicInfo, BondCouponInfo {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final Parser parser = new Parser(ErrorHandling.INFO);

    @Override
    public @NotNull String name() {
        return bond.name();
    }

    @Override
    public @NotNull String shortname() {
        return bond.shortname();
    }

    @Override
    public @NotNull String board() {
        return bond.boardid();
    }

    @Override
    public @NotNull String issueDateStr() {
        return Objects.requireNonNull(bond.issueDate());
    }

    @Override
    public @NotNull Date issueDate() {
        return parser.parseDate(FORMAT, issueDateStr(), Parser.NO_DATE);
    }

    @Override
    public @NotNull String maturityDateStr() {
        return Objects.requireNonNull(bond.maturityDate());
    }

    @Override
    public @NotNull Date maturityDate() {
        return parser.parseDate(FORMAT, maturityDateStr(), Parser.NO_DATE);
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
