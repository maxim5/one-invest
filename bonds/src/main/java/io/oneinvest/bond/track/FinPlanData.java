package io.oneinvest.bond.track;

import io.oneinvest.util.Parsing;
import org.jetbrains.annotations.NotNull;

import static io.oneinvest.util.Parsing.parseDouble;
import static io.oneinvest.util.Parsing.parseInt;

public record FinPlanData(@NotNull Isin isin,
                          double notional,
                          boolean defaults,
                          double price,
                          double accruedInterest,
                          double effectivePrice,
                          double coupon,
                          double couponPercent,
                          int couponDays,
                          @NotNull String maturityDate,
                          int daysToMaturity,
                          int duration,
                          double annualYield,
                          double totalYield) {
    public static @NotNull FinPlanData fromHttpRu(@NotNull String rawHttp) {
        String form = Parsing.extractBetween(rawHttp, "<h2>Параметры облигации", "</ul>");
        String isin = Parsing.extractBetween(form, "ISIN:", "&nbsp;");
        String notional = Parsing.extractBetween(form, "Номинал: ", " ");
        String defaults = Parsing.extractBetween(form, "Дефолты: ", "&nbsp;").trim();
        String price = Parsing.extractBetween(form, "<span id=\"price_value\">", "</span>");
        String accruedInterest = Parsing.extractBetween(form, "<span id=\"nkd_value\">", " ");
        String effectivePrice = Parsing.extractBetween(form, "Текущая цена с учетом НКД: ", " ");
        String couponArea = Parsing.extractBetween(form, "Размер купона:", "</p>");
        String coupon = Parsing.extractBetween(couponArea, " ", " ");
        String couponPercent = Parsing.extractBetween(couponArea, "(", ")");
        String couponDays = Parsing.extractBetween(form, "Длит. купона:", "</p>").trim();
        String maturityDate = Parsing.extractBetween(form, "Дата погашения облигации:", "</p>").trim();
        String daysToMaturity = Parsing.extractBetween(form, "Кол-во дней до погашения облигации:", "</p>").trim();
        String duration = Parsing.extractBetween(form, "Дюрация:", " дн").trim();
        String annualYield = Parsing.extractBetween(form, "Расчетная годовая доходность:", "&nbsp;").trim();
        String totalYield = Parsing.extractBetween(form, "Общая доходность:", "&nbsp;").trim();
        return new FinPlanData(
            Isin.of(isin),
            parseDouble(notional),
            defaults.equals("нет"),
            parseDouble(price),
            parseDouble(accruedInterest),
            parseDouble(effectivePrice),
            parseDouble(coupon),
            parseDouble(couponPercent),
            parseInt(couponDays),
            maturityDate,
            parseInt(daysToMaturity),
            parseInt(duration),
            parseDouble(annualYield),
            parseDouble(totalYield)
        );
    }
}
