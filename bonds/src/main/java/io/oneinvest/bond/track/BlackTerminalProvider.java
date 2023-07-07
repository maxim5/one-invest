package io.oneinvest.bond.track;

import io.oneinvest.util.Http;
import org.jetbrains.annotations.NotNull;

public class BlackTerminalProvider {
    public @NotNull BlackTerminalData fetch(@NotNull Isin isin) {
        String url = "https://blackterminal.com/bonds/%s/TQCB?hl=en".formatted(isin);
        String response = Http.httpCall(url);
        return BlackTerminalData.fromHttpEn(response);
    }
}
