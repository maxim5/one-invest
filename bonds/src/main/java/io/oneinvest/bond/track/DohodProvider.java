package io.oneinvest.bond.track;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.google.common.flogger.FluentLogger;
import io.oneinvest.util.Http;
import io.oneinvest.util.TimeSeries;
import org.jetbrains.annotations.NotNull;

import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

public class DohodProvider {
    private static final FluentLogger log = FluentLogger.forEnclosingClass();

    public @NotNull DohodData fetch(@NotNull Isin isin) {
        DohodBondMap[] bonds = fetchReplacement(isin);
        DohodBondMap thisBond = Arrays.stream(bonds).filter(bond -> bond.matches(isin)).findFirst().orElseThrow();
        List<DohodBondMap> replacements = Arrays.stream(bonds).filter(bond -> !bond.matches(isin)).toList();
        TimeSeries dailyPrices = fetchRates(isin, thisBond.boardid());
        return new DohodData(isin, dailyPrices, thisBond, replacements);
    }

    private static @NotNull TimeSeries fetchRates(@NotNull Isin isin, @NotNull String boardid) {
        String url = "https://www.dohod.ru/assets/components/dohodbonds/connectorweb.php" +
                     "?action=rates&secid=%s&boardid=%s".formatted(isin, boardid);
        String response = Http.httpCall(url);

        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        record Point(long timestamp, double value) {}
        record Payload(@NotNull List<Point> items, double max, double min) {}

        ObjectMapper mapper = new ObjectMapper();
        try {
            Payload payload = mapper.readValue(response, Payload.class);
            return TimeSeries.from(payload.items, Point::timestamp, Point::value);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static @NotNull DohodBondMap[] fetchReplacement(@NotNull Isin isin) {
        String url = "https://www.dohod.ru/assets/components/dohodbonds/connectorweb.php" +
                     "?action=replacement&isin=%s&mode=regular".formatted(isin);
        String response = Http.httpCall(url);

        ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .addHandler(new DeserializationProblemHandler() {
                @Override
                public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType,
                                                     String valueToConvert, String failureMsg) {
                    log.atWarning().log("Failed to parse the string: %s", failureMsg);
                    if (Number.class.isAssignableFrom(targetType)) {
                        return -1;
                    }
                    return null;
                }
                @Override
                public Object handleWeirdNumberValue(DeserializationContext ctxt, Class<?> targetType,
                                                     Number valueToConvert, String failureMsg) {
                    log.atWarning().log("Failed to parse the number: %s", failureMsg);
                    return -1;
                }
            });

        try {
            return mapper.readValue(response, DohodBondMap[].class);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
