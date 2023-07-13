package io.oneinvest.bond.track;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.google.common.flogger.FluentLogger;
import io.oneinvest.bond.track.BondCashflowInfo.Payment;
import io.oneinvest.util.Http;
import io.oneinvest.util.Http.HttpOptions;
import io.oneinvest.util.HttpCache;
import org.jetbrains.annotations.NotNull;

import java.io.UncheckedIOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MoexProvider implements AutoCloseable {
    private static final FluentLogger log = FluentLogger.forEnclosingClass();

    private final HttpCache cache = new HttpCache("moex");

    public @NotNull MoexData fetch(@NotNull Isin isin, @NotNull HttpOptions options) {
        List<Payment> payments = fetchCoupons(isin, options);
        return new MoexData(isin, payments);
    }

    private @NotNull List<Payment> fetchCoupons(@NotNull Isin isin, @NotNull HttpOptions options) {
        String url = "https://iss.moex.com/iss/securities/%s/bondization.json?iss.json=extended&iss.meta=off&iss.only=coupons&lang=ru&limit=unlimited".formatted(isin);
        String response = Http.httpCall(url, cache, options);

        record Meta(@NotNull Map<String, Object> charsetInfo) {}
        record Coupon(String isin, String name, Date couponDate, double value, double valuePrc) {}
        record Coupons(@NotNull List<Coupon> coupons) {}
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        record Payload(@NotNull Meta meta, @NotNull Coupons coupons) {}

        ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CASE);
        try {
            Payload payload = mapper.readValue(response, Payload.class);
            return payload.coupons().coupons().stream()
                .map(coupon -> new Payment(coupon.couponDate(), coupon.value(), coupon.valuePrc()))
                .toList();
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        cache.close();
    }
}
