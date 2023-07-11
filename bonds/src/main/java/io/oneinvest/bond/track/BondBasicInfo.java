package io.oneinvest.bond.track;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public interface BondBasicInfo {
    @NotNull Isin isin();

    @NotNull String name();

    @NotNull String shortname();

    @NotNull String board();

    @NotNull String issueDateStr();

    @NotNull Date issueDate();

    @NotNull String maturityDateStr();

    @NotNull Date maturityDate();
}
