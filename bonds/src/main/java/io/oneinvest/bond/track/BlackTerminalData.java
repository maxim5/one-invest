package io.oneinvest.bond.track;

import io.oneinvest.util.Parser;
import io.oneinvest.util.Parser.ErrorHandling;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record BlackTerminalData(@NotNull Isin isin,
                                int rating,
                                @NotNull String rawYieldCalc) {
    private static final Parser parser = new Parser(ErrorHandling.INFO);
    
    public static @NotNull BlackTerminalData fromHttpEn(@NotNull String rawHttp) {
        String isin = parser.applyOrEmpty(
            rawHttp,
            Parser.extractBetween("ISIN code", "kv-form-attribute"),
            Parser.extractBetween("<div class=\"kv-attribute\">", "</div>")
        );
        String reliability = parser.applyOrEmpty(
            rawHttp,
            Parser.extractBetween("Reliability", "</div>")
        );
        String yieldCalc = parser.at(ErrorHandling.LOG).applyOrEmpty(
            rawHttp,
            Parser.extractBetween("Yield calculation</div>", "<div class=\"widget-header\">"),
            Parser.extractBetween("<div class=\"widget-text mb-4\" style=\"margin-top: 1px; padding: 10px;\">", "</div>")
        );

        return new BlackTerminalData(Isin.of(isin), Parser.countOccurrences(reliability, "fas fa-star"), yieldCalc);
    }

    public @Nullable BlackTerminalParsedYield parseYield() {
        return BlackTerminalParsedYield.parseOrNull(rawYieldCalc);
    }
}
