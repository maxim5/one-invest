package io.oneinvest.bond.track;

import org.jetbrains.annotations.NotNull;

public interface BondBasicInfo {
    @NotNull Isin isin();

    @NotNull String name();

    @NotNull String shortname();

    @NotNull String board();

    @NotNull String issueDate();

    @NotNull String maturityDate();
}
