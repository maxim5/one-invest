package io.oneinvest.bond.track;

import io.oneinvest.util.Http;
import org.jetbrains.annotations.NotNull;

public class BlackTerminalProvider {
    public @NotNull BlackTerminalData fetch(@NotNull Isin isin, @NotNull String board) {
        String url = "https://blackterminal.com/bonds/%s/%s?hl=en".formatted(isin, board);
        String response = Http.httpCall(url);
        return BlackTerminalData.fromHttpEn(response);
    }
}
