package io.oneinvest.bond.track;

import io.oneinvest.util.Parser;
import io.oneinvest.util.Parser.ErrorHandling;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record BlackTerminalParsedYield(double notional,
                                       double price,
                                       double defaultCommissionRate,
                                       double commissionPaid,
                                       double accruedInterest,
                                       double totalPaid,
                                       double totalIncome,
                                       double totalInterest) {
    private static final Parser parser = new Parser(ErrorHandling.INFO);

    public static @Nullable BlackTerminalParsedYield parseOrNull(@NotNull String yieldCalc) {
        yieldCalc = Normalizer.normalize(yieldCalc, Normalizer.Form.NFD);
        yieldCalc = fixWhitespace(yieldCalc).trim();
        Matcher matcher = YIELD_REGEX.matcher(yieldCalc);
        if (!matcher.matches()) {
            return null;
        }
        try {
            return new BlackTerminalParsedYield(
                parser.parseDouble(matcher.group(3), -1),
                parser.parseDouble(matcher.group(4), -1),
                parser.parseDouble(matcher.group(7), -1),
                parser.parseDouble(matcher.group(12), -1),
                parser.parseDouble(matcher.group(14), -1),
                parser.parseDouble(matcher.group(22), -1),
                parser.parseDouble(matcher.group(30), -1),
                parser.parseDouble(matcher.group(37), -1)
            );
        } catch (Exception e) {
            return null;
        }
    }

    private static final String YIELD_PATTERN = """
        <b>Bond</b>: (.+)<br/>
        <b>Face value</b>: (RUB |)(.+)<br/>
        <b>Price</b>: (.+)% of face value =
        (RUB |)(.+)<br/>
        <b>Broker commission</b>,
        by default    (.+)%:
        (RUB |)(.+) \\*
        (.+)% =
        (RUB |)(.+)    <br/>
        
        <b>Accrued interest</b>: (RUB |)(.+)    <br/>

        <b>You will pay</b>:
        (RUB |)(.+) \\+
        (RUB |)(.+) \\+
        (RUB |)(.+) =
        (RUB |)(.+)    for 1 pcs.<br/>
        By maturity date    <b>(.+)</b>
        \\(in (.+) days\\)    you will receive coupons \\(inclusive of taxes 13%\\)    (RUB |)(.+),
        as well as the body of the bond net of tax from the redemption of the bond    (RUB |)(.+)    total:
        (RUB |)(.+)    <br/>
        <b>Your profit</b>.?
        for all time will be:
        (RUB |)(.+)    -
        (RUB |)(.+) =
        (RUB |)(.+)    or    (.+)%    per annum.<br/>
        .? Provided that the last known coupon remains unchanged<br/>
        """;
    private static final Pattern YIELD_REGEX = Pattern.compile(fixWhitespace(YIELD_PATTERN));

    private static @NotNull String fixWhitespace(@NotNull String s) {
        return s.replace("\u00A0", " ").replaceAll("\\s+", " ").trim();
    }
}
