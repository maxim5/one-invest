package io.oneinvest.bond.track;

import io.oneinvest.bond.track.BondCashflowInfo.Payment;
import io.oneinvest.util.Table;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BondAnalyzer implements AutoCloseable {
    private final BondDataAggregator aggregator = new BondDataAggregator();

    public void analyze(@NotNull List<Isin> isins) {
        List<BondData> bondData = isins.stream().map(aggregator::fetchAllData).toList();
        Table table = Table.fromRows(
            bondData,
            data -> toArray(data.isin(), data.shortname(), data.maturityDate(), data.couponYield(), data.annualYieldIfBuyNow())
        );
        table
            .withHeader(Table.Header.of("ISIN", "Name", "Maturity", "Coupon", "Annual Yield"))
            .withFormats(Table.Formats.of("%s %-10s %tF %5.2f %5.2f"))
            .println(2);
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

        double norm = totalPos;
        Table table = Table.fromRows(
            rows,
            row -> toArray(
                row.isin(),
                row.cashflow0() * row.pos(),
                row.cashflow0() * row.pos() / norm,
                row.cashflow1() * row.pos(),
                row.cashflow1() * row.pos() / norm
            )
        );
        table
            .withHeader(Table.Header.of("ISIN", "2023", "2023 Percent", "2024", "2024 Percent"))
            .withFormats(Table.Formats.of("%s %5.0f %3.1f%% %5.0f %3.1f%%"))
            .println(2);
    }

    @Override
    public void close() {
        aggregator.close();
    }

    private record Row(Position position, double price, double cashflow0, double cashflow1) {
        public @NotNull Isin isin() {
            return position.isin();
        }

        public int pos() {
            return position.pos();
        }
    }

    private static @NotNull Object[] toArray(@NotNull Object ... objects) {
        return objects;
    }
}
