package io.oneinvest.bond.track;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public interface BondCashflowInfo {
    @NotNull List<Payment> payments();

    default @NotNull List<Payment> knownPaymentsFor(int year) {
        return payments().stream().filter(Payment::isKnown).filter(payment -> payment.year() == toCalendarYear(year)).toList();
    }

    static int toCalendarYear(int year) {
        assert year < 1000 || year > 2000;
        return year < 1000 ? Calendar.getInstance().get(Calendar.YEAR) + year : year;
    }

    record Payment(@NotNull Date date, double value, double annualYield) {
        public Payment {
            assert value > 0 == annualYield > 0;
        }

        public @NotNull LocalDate localDate() {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        public int year() {
            return localDate().getYear();
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
