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
        String form = Parsing.extractBetweenOrEmpty(rawHttp, "<h2>Параметры облигации", "</ul>");
        String isin = Parsing.extractBetweenOrEmpty(form, "ISIN:", "&nbsp;");
        String notional = Parsing.extractBetweenOrEmpty(form, "Номинал: ", " ");
        String defaults = Parsing.extractBetweenOrEmpty(form, "Дефолты: ", "&nbsp;").trim();
        String price = Parsing.extractBetweenOrEmpty(form, "<span id=\"price_value\">", "</span>");
        String accruedInterest = Parsing.extractBetweenOrEmpty(form, "<span id=\"nkd_value\">", " ");
        String effectivePrice = Parsing.extractBetweenOrEmpty(form, "Текущая цена с учетом НКД: ", " ");
        String couponArea = Parsing.extractBetweenOrEmpty(form, "Размер купона:", "</p>");
        String coupon = Parsing.extractBetweenOrEmpty(couponArea, " ", " ");
        String couponPercent = Parsing.extractBetweenOrEmpty(couponArea, "(", ")");
        String couponDays = Parsing.extractBetweenOrEmpty(form, "Длит. купона:", "</p>").trim();
        String maturityDate = Parsing.extractBetweenOrEmpty(form, "Дата погашения облигации:", "</p>").trim();
        String daysToMaturity = Parsing.extractBetweenOrEmpty(form, "Кол-во дней до погашения облигации:", "</p>").trim();
        String duration = Parsing.extractBetweenOrEmpty(form, "Дюрация:", " дн").trim();
        String annualYield = Parsing.extractBetweenOrEmpty(form, "Расчетная годовая доходность:", "&nbsp;").trim();
        String totalYield = Parsing.extractBetweenOrEmpty(form, "Общая доходность:", "&nbsp;").trim();
        return new FinPlanData(
            Isin.of(isin),
            parseDouble(notional, -1),
            defaults.equals("нет"),
            parseDouble(price, -1),
            parseDouble(accruedInterest, -1),
            parseDouble(effectivePrice, -1),
            parseDouble(coupon, -1),
            parseDouble(couponPercent, -1),
            parseInt(couponDays, -1),
            maturityDate,
            parseInt(daysToMaturity, -1),
            parseInt(duration, -1),
            parseDouble(annualYield, -1),
            parseDouble(totalYield, -1)
        );
    }
}
