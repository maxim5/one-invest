package io.oneinvest.bond.track;

import io.oneinvest.util.Http;
import io.oneinvest.util.Http.HttpOptions;
import io.oneinvest.util.HttpCache;
import org.jetbrains.annotations.NotNull;

public class FinPlanProvider implements AutoCloseable {
    private final HttpCache cache = new HttpCache("finplan");

    public @NotNull FinPlanData fetch(@NotNull Isin isin, @NotNull HttpOptions options) {
        String url = "https://fin-plan.org/lk/obligations/%s/".formatted(isin);
        String response = Http.httpCall(url, cache, options);
        return FinPlanData.fromHttpRu(response);
    }

    @Override
    public void close() {
        cache.close();
    }
}
