package io.oneinvest.bond.track;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MoexData(@NotNull Isin isin, @NotNull List<Payment> payments) implements BondCashflowInfo {
}
