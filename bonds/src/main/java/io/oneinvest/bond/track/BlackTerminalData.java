package io.oneinvest.bond.track;

import io.oneinvest.util.Parsing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record BlackTerminalData(@NotNull Isin isin,
                                int rating,
                                @NotNull String rawYieldCalc) {
    public static @NotNull BlackTerminalData fromHttpEn(@NotNull String rawHttp) {
        String isin = Parsing.apply(
            rawHttp,
            Parsing.extractBetween("ISIN code", "kv-form-attribute"),
            Parsing.extractBetween("<div class=\"kv-attribute\">", "</div>")
        );
        String reliability = Parsing.apply(
            rawHttp,
            Parsing.extractBetween("Reliability", "</div>")
        );
        String yieldCalc = Parsing.apply(
            rawHttp,
            Parsing.extractBetween("Yield calculation</div>", "<div class=\"widget-header\">"),
            Parsing.extractBetween("<div class=\"widget-text mb-4\" style=\"margin-top: 1px; padding: 10px;\">", "</div>")
        );

        return new BlackTerminalData(Isin.of(isin), Parsing.countOccurrences(reliability, "fas fa-star"), yieldCalc);
    }

    public @Nullable BlackTerminalParsedYield parseYield() {
        return BlackTerminalParsedYield.parseOrNull(rawYieldCalc);
    }
}
