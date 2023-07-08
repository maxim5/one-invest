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
        return new SmartlabData(
            Isin.of(isin),
            name,
            shortname,
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
            parseDouble(frequency, -1)
        );
    }

    private static @NotNull Function<String, String> extractDivValue() {
        return s -> Parsing.applyOrEmpty(s, Parsing.extractBetween("<div ", "</div>"), Parsing.extractAfter(">"), String::trim);
    }
}
