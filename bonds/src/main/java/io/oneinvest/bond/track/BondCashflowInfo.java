package io.oneinvest.bond.track;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public interface BondCashflowInfo {
    @NotNull List<Payment> payments();

    record Payment(@NotNull Date date, double value, double annualYield) {
        public Payment {
            assert value > 0 == annualYield > 0;
        }

        public boolean isKnown() {
            return value > 0;
        }

        @Override
        public String toString() {
            return "Payment{date=%tF, value=%s, annualYield=%s}".formatted(date, value, annualYield);
        }
    }
}
