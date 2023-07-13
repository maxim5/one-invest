package io.oneinvest.bond.track;

import io.oneinvest.util.Http;
import io.oneinvest.util.Http.HttpOptions;
import io.oneinvest.util.HttpCache;
import org.jetbrains.annotations.NotNull;

public class BlackTerminalProvider implements AutoCloseable {
    private final HttpCache cache = new HttpCache("blackterminal");

    public @NotNull BlackTerminalData fetch(@NotNull Isin isin, @NotNull String board, @NotNull HttpOptions options) {
        String url = "https://blackterminal.com/bonds/%s/%s?hl=en".formatted(isin, board);
        String response = Http.httpCall(url, cache, options);
        return BlackTerminalData.fromHttpEn(response);
    }

    @Override
    public void close() {
        cache.close();
    }
}
