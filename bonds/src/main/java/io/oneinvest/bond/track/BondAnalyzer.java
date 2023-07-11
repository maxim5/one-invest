package io.oneinvest.bond.track;

import io.oneinvest.bond.track.BondCashflowInfo.Payment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BondAnalyzer {
    private final BondDataAggregator aggregator = new BondDataAggregator();

    public void analyze(@NotNull List<Isin> isins) {
        List<BondData> bondData = isins.stream().map(aggregator::fetchAllData).toList();
        for (BondData data : bondData) {
            System.out.println(data.isin() + " " + data.shortname() + " " + data.maturityDate() + " " + data.couponYield());
        }
    }

    public void analyzePortfolio(@NotNull List<Position> positions) {
        List<BondData> bondData = positions.stream().map(Position::isin).map(aggregator::fetchAllData).toList();

        double totalPos = 0;
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < bondData.size(); i++) {
            BondData data = bondData.get(i);
            Position position = positions.get(i);
            double price = data.smartlabData().price();
            totalPos += position.pos() * price;
            double thisYearCashflow = data.knownPaymentsFor(0).stream().mapToDouble(Payment::value).sum();
            double nextYearCashflow = data.knownPaymentsFor(1).stream().mapToDouble(Payment::value).sum();
            rows.add(new Row(position, price, thisYearCashflow, nextYearCashflow));
        }

        for (Row row : rows) {
            System.out.println("%s:  2023=%.0f (%.1f%%)  2024=%.0f (%.1f%%)".formatted(
                row.isin(),
                row.cashflow0() * row.pos(),
                row.cashflow0() * row.pos() / totalPos,
                row.cashflow1() * row.pos(),
                row.cashflow1() * row.pos() / totalPos
            ));
        }
    }

    private record Row(Position position, double price, double cashflow0, double cashflow1) {
        public @NotNull Isin isin() {
            return position.isin();
        }

        public int pos() {
            return position.pos();
        }
    }
}
