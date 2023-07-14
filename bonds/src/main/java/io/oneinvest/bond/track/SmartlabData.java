package io.oneinvest.bond.track;

import io.oneinvest.util.Parser;
import io.oneinvest.util.Parser.ErrorHandling;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static io.oneinvest.util.Parser.*;

public record SmartlabData(@NotNull Isin isin,
                           @NotNull String name,
                           @NotNull String shortname,
                           @NotNull String board,
                           double price,
                           double yield,
                           double yearsToMaturity,
                           @NotNull String issueDateStr,
                           @NotNull String maturityDateStr,
                           int duration,
                           double notional,
                           double couponYield,
                           double couponAbs,
                           double couponAbsAnnual,
                           double frequency,
                           @NotNull List<Payment> payments) implements BondBasicInfo, BondCouponInfo, BondCashflowInfo {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final Parser parser = new Parser(ErrorHandling.INFO);

    @Override
    public @NotNull Date issueDate() {
        return parser.parseDate(FORMAT, issueDateStr(), Parser.NO_DATE);
    }

    @Override
    public @NotNull Date maturityDate() {
        return parser.parseDate(FORMAT, maturityDateStr(), Parser.NO_DATE);
    }

    public static @NotNull SmartlabData fromHttpRu(@NotNull String rawHttp) {
        String info = parser.at(ErrorHandling.WARN).extractBetweenOrEmpty(rawHttp, "<section class=\"quotes-info-list\">", "</section>");
        String price = parser.applyOrEmpty(info, extractAfter("\tКотировка облигации"), extractDivValue());
        String yield = parser.applyOrEmpty(info, extractAfter("\tДоходность"), extractDivValue());
        String yearsToMaturity = parser.applyOrEmpty(info, extractAfter("\tЛет до погашения"), extractDivValue());
        String issueDate = parser.applyOrEmpty(info, extractAfter("\tДата размещения"), extractDivValue());
        String maturityDate = parser.applyOrEmpty(info, extractAfter("\tДата погашения"), extractDivValue());
        String duration = parser.applyOrEmpty(info, extractAfter("\tДюрация"), extractDivValue());
        String notional = parser.applyOrEmpty(info, extractAfter("\tНоминал"), extractDivValue());
        String couponYield = parser.applyOrEmpty(info, extractAfter("\tТекущая доходность купона"), extractDivValue());
        String couponAbs = parser.applyOrEmpty(info, extractAfter("Купон,"), extractDivValue());
        String couponAbsAnnual = parser.applyOrEmpty(info, extractAfter("Годовой купон"), extractDivValue());
        String frequency = parser.applyOrEmpty(info, extractAfter("Частота купона"), extractDivValue());
        String shortname = parser.applyOrEmpty(info, extractAfter("\tНазвание"), extractDivValue());
        String name = parser.applyOrEmpty(info, extractAfter("\tИмя облигации"), extractDivValue());
        String isin = parser.applyOrEmpty(info, extractAfter("\tISIN"), extractDivValue());
        String board = parser.applyOrEmpty(info, extractAfter("\tРежим торгов"), extractDivValue());
        List<Payment> payments = extractPayments(rawHttp);
        return new SmartlabData(
            Isin.of(isin),
            name,
            shortname,
            board,
            parser.parseDouble(price, -1),
            parser.parseDouble(yield, -1),
            parser.parseDouble(yearsToMaturity, -1),
            issueDate,
            maturityDate,
            parser.parseInt(duration, -1),
            parser.parseDouble(notional, -1),
            parser.parseDouble(couponYield, -1),
            parser.parseDouble(couponAbs, -1),
            parser.parseDouble(couponAbsAnnual, -1),
            parser.parseDouble(frequency, -1),
            payments
        );
    }

    private static @NotNull List<Payment> extractPayments(@NotNull String rawHttp) {
        String table = parser.at(ErrorHandling.LOG).applyOrEmpty(
            rawHttp,
            Parser.extractAfter("Календарь выплаты купонов"),
            Parser.extractBetween("custom-table", "</table>"),
            Parser.extractBetween("<tbody", "</tbody>")
        );
        return Parser.extractAll(table, "<tr>", "</tr>").stream().map(line -> {
            List<String> row = Parser.extractAll(line, "<td>", "</td>");
            Date date = parser.parseDate(FORMAT, row.get(1), NO_DATE);
            double value = parseTableValueOrIgnore(row.get(2));
            double annualYield = parseTableValueOrIgnore(row.get(3));
            return new Payment(date, value, annualYield);
        }).toList();
    }

    private static @NotNull Function<String, String> extractDivValue() {
        return extractTagValue("div");
    }

    private static @NotNull Function<String, String> extractTagValue(@NotNull String tag) {
        return s -> parser.applyOrEmpty(
            s,
            Parser.extractBetween("<%s".formatted(tag), "</%s>".formatted(tag)),
            Parser.extractAfter(">"),
            String::trim
        );
    }

    private static double parseTableValueOrIgnore(@NotNull String s) {
        return s.contains("&mdash;") ? -1 : parser.parseDouble(s, -1);
    }
}
