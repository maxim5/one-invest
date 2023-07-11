package io.oneinvest.bond.track;

import io.oneinvest.util.Parsing;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static io.oneinvest.util.Parsing.*;

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

    @Override
    public @NotNull Date issueDate() {
        return Parsing.parseDate(FORMAT, issueDateStr(), Parsing.NO_DATE);
    }

    @Override
    public @NotNull Date maturityDate() {
        return Parsing.parseDate(FORMAT, maturityDateStr(), Parsing.NO_DATE);
    }

    public static @NotNull SmartlabData fromHttpRu(@NotNull String rawHttp) {
        String info = Parsing.extractBetweenOrEmpty(rawHttp, "<section class=\"quotes-info-list\">", "</section>");
        String price = Parsing.applyOrEmpty(info, extractAfter("\tКотировка облигации"), extractDivValue());
        String yield = Parsing.applyOrEmpty(info, extractAfter("\tДоходность"), extractDivValue());
        String yearsToMaturity = Parsing.applyOrEmpty(info, extractAfter("\tЛет до погашения"), extractDivValue());
        String issueDate = Parsing.applyOrEmpty(info, extractAfter("\tДата размещения"), extractDivValue());
        String maturityDate = Parsing.applyOrEmpty(info, extractAfter("\tДата погашения"), extractDivValue());
        String duration = Parsing.applyOrEmpty(info, extractAfter("\tДюрация"), extractDivValue());
        String notional = Parsing.applyOrEmpty(info, extractAfter("\tНоминал"), extractDivValue());
        String couponYield = Parsing.applyOrEmpty(info, extractAfter("\tТекущая доходность купона"), extractDivValue());
        String couponAbs = Parsing.applyOrEmpty(info, extractAfter("Купон,"), extractDivValue());
        String couponAbsAnnual = Parsing.applyOrEmpty(info, extractAfter("Годовой купон"), extractDivValue());
        String frequency = Parsing.applyOrEmpty(info, extractAfter("Частота купона"), extractDivValue());
        String shortname = Parsing.applyOrEmpty(info, extractAfter("\tНазвание"), extractDivValue());
        String name = Parsing.applyOrEmpty(info, extractAfter("\tИмя облигации"), extractDivValue());
        String isin = Parsing.applyOrEmpty(info, extractAfter("\tISIN"), extractDivValue());
        String board = Parsing.applyOrEmpty(info, extractAfter("\tРежим торгов"), extractDivValue());
        List<Payment> payments = extractPayments(rawHttp);
        return new SmartlabData(
            Isin.of(isin),
            name,
            shortname,
            board,
            parseDouble(price, -1),
            parseDouble(yield, -1),
            parseDouble(yearsToMaturity, -1),
            issueDate,
            maturityDate,
            parseInt(duration, -1),
            parseDouble(notional, -1),
            parseDouble(couponYield, -1),
            parseDouble(couponAbs, -1),
            parseDouble(couponAbsAnnual, -1),
            parseDouble(frequency, -1),
            payments
        );
    }

    private static @NotNull List<Payment> extractPayments(@NotNull String rawHttp) {
        String table = Parsing.applyOrEmpty(
            rawHttp,
            Parsing.extractAfter("Календарь выплаты купонов"),
            Parsing.extractBetween("custom-table", "</table>"),
            Parsing.extractBetween("<tbody", "</tbody>")
        );
        return Parsing.extractAll(table, "<tr>", "</tr>").stream().map(line -> {
            List<String> row = extractAll(line, "<td>", "</td>");
            Date date = parseDate(FORMAT, row.get(1), NO_DATE);
            double value = parseTableValueOrIgnore(row.get(2));
            double annualYield = parseTableValueOrIgnore(row.get(3));
            return new Payment(date, value, annualYield);
        }).toList();
    }

    private static @NotNull Function<String, String> extractDivValue() {
//        return s -> Parsing.applyOrEmpty(s, Parsing.extractBetween("<div ", "</div>"), Parsing.extractAfter(">"), String::trim);
        return extractTagValue("div");
    }

    private static @NotNull Function<String, String> extractTagValue(@NotNull String tag) {
        return s -> Parsing.applyOrEmpty(
            s,
            Parsing.extractBetween("<%s".formatted(tag), "</%s>".formatted(tag)),
            Parsing.extractAfter(">"),
            String::trim
        );
    }

    private static double parseTableValueOrIgnore(@NotNull String s) {
        return s.contains("&mdash;") ? -1 : parseDouble(s, -1);
    }
}
