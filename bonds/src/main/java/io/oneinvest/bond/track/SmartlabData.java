package io.oneinvest.bond.track;

import io.oneinvest.util.Parsing;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static io.oneinvest.util.Parsing.*;

public record SmartlabData(@NotNull Isin isin,
                           @NotNull String name,
                           @NotNull String shortname,
                           double price,
                           double yield,
                           double yearsToMaturity,
                           @NotNull String issueDate,
                           @NotNull String maturityDate,
                           int duration,
                           double notional,
                           double couponYield,
                           double couponAbs,
                           double couponAbsAnnual,
                           double frequency) implements BondBasicInfo, BondCouponInfo {
    public static @NotNull SmartlabData fromHttpRu(@NotNull String rawHttp) {
        String info = extractBetween(rawHttp, "<section class=\"quotes-info-list\">", "</section>");
        String price = Parsing.apply(info, extractAfter("\tКотировка облигации"), extractDivValue());
        String yield = Parsing.apply(info, extractAfter("\tДоходность"), extractDivValue());
        String yearsToMaturity = Parsing.apply(info, extractAfter("\tЛет до погашения"), extractDivValue());
        String issueDate = Parsing.apply(info, extractAfter("\tДата размещения"), extractDivValue());
        String maturityDate = Parsing.apply(info, extractAfter("\tДата погашения"), extractDivValue());
        String duration = Parsing.apply(info, extractAfter("\tДюрация"), extractDivValue());
        String notional = Parsing.apply(info, extractAfter("\tНоминал"), extractDivValue());
        String couponYield = Parsing.apply(info, extractAfter("\tТекущая доходность купона"), extractDivValue());
        String couponAbs = Parsing.apply(info, extractAfter("Купон,"), extractDivValue());
        String couponAbsAnnual = Parsing.apply(info, extractAfter("Годовой купон"), extractDivValue());
        String frequency = Parsing.apply(info, extractAfter("Частота купона"), extractDivValue());
        String shortname = Parsing.apply(info, extractAfter("\tНазвание"), extractDivValue());
        String name = Parsing.apply(info, extractAfter("\tИмя облигации"), extractDivValue());
        String isin = Parsing.apply(info, extractAfter("\tISIN"), extractDivValue());
        return new SmartlabData(
            Isin.of(isin),
            name,
            shortname,
            parseDouble(price),
            parseDouble(yield),
            parseDouble(yearsToMaturity),
            issueDate,
            maturityDate,
            parseInt(duration),
            parseDouble(notional),
            parseDouble(couponYield),
            parseDouble(couponAbs),
            parseDouble(couponAbsAnnual),
            parseDouble(frequency)
        );
    }

    private static @NotNull Function<String, String> extractDivValue() {
        return s -> Parsing.apply(s, Parsing.extractBetween("<div ", "</div>"), Parsing.extractAfter(">"), String::trim);
    }
}
