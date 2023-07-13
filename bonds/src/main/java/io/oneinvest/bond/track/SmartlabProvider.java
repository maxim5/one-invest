package io.oneinvest.bond.track;

import io.oneinvest.util.Http;
import io.oneinvest.util.Http.HttpOptions;
import io.oneinvest.util.HttpCache;
import org.jetbrains.annotations.NotNull;

public class SmartlabProvider implements AutoCloseable {
    private final HttpCache cache = new HttpCache("smartlab");

    public @NotNull SmartlabData fetch(@NotNull Isin isin, @NotNull HttpOptions options) {
        String url = "https://smart-lab.ru/q/bonds/%s/".formatted(isin);
        String response = Http.httpCall(url, cache, options);
        return SmartlabData.fromHttpRu(response);
    }

    @Override
    public void close() {
        cache.close();
    }
}
