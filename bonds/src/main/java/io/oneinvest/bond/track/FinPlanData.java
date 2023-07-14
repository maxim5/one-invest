package io.oneinvest.bond.track;

import io.oneinvest.util.Parser;
import io.oneinvest.util.Parser.ErrorHandling;
import org.jetbrains.annotations.NotNull;

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
    private static final Parser parser = new Parser(ErrorHandling.LOG);

    public static @NotNull FinPlanData fromHttpRu(@NotNull String rawHttp) {
        String form = parser.at(ErrorHandling.WARN).extractBetweenOrEmpty(rawHttp, "<h2>Параметры облигации", "</ul>");
        String isin = parser.extractBetweenOrEmpty(form, "ISIN:", "&nbsp;");
        String notional = parser.extractBetweenOrEmpty(form, "Номинал: ", " ");
        String defaults = parser.extractBetweenOrEmpty(form, "Дефолты: ", "&nbsp;").trim();
        String price = parser.extractBetweenOrEmpty(form, "<span id=\"price_value\">", "</span>");
        String accruedInterest = parser.extractBetweenOrEmpty(form, "<span id=\"nkd_value\">", " ");
        String effectivePrice = parser.extractBetweenOrEmpty(form, "Текущая цена с учетом НКД: ", " ");
        String couponArea = parser.extractBetweenOrEmpty(form, "Размер купона:", "</p>");
        String coupon = parser.extractBetweenOrEmpty(couponArea, " ", " ");
        String couponPercent = parser.extractBetweenOrEmpty(couponArea, "(", ")");
        String couponDays = parser.extractBetweenOrEmpty(form, "Длит. купона:", "</p>").trim();
        String maturityDate = parser.extractBetweenOrEmpty(form, "Дата погашения облигации:", "</p>").trim();
        String daysToMaturity = parser.extractBetweenOrEmpty(form, "Кол-во дней до погашения облигации:", "</p>").trim();
        String duration = parser.extractBetweenOrEmpty(form, "Дюрация:", " дн").trim();
        String annualYield = parser.extractBetweenOrEmpty(form, "Расчетная годовая доходность:", "&nbsp;").trim();
        String totalYield = parser.extractBetweenOrEmpty(form, "Общая доходность:", "&nbsp;").trim();
        return new FinPlanData(
            Isin.of(isin),
            parser.parseDouble(notional, -1),
            defaults.equals("нет"),
            parser.parseDouble(price, -1),
            parser.parseDouble(accruedInterest, -1),
            parser.parseDouble(effectivePrice, -1),
            parser.parseDouble(coupon, -1),
            parser.parseDouble(couponPercent, -1),
            parser.parseInt(couponDays, -1),
            maturityDate,
            parser.parseInt(daysToMaturity, -1),
            parser.parseInt(duration, -1),
            parser.parseDouble(annualYield, -1),
            parser.parseDouble(totalYield, -1)
        );
    }
}
