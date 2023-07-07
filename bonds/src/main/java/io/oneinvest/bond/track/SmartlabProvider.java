package io.oneinvest.bond.track;

import io.oneinvest.util.Http;
import org.jetbrains.annotations.NotNull;

public class SmartlabProvider {
    public @NotNull SmartlabData fetch(@NotNull Isin isin) {
        String url = "https://smart-lab.ru/q/bonds/%s/".formatted(isin);
        String response = Http.httpCall(url);
        return SmartlabData.fromHttpRu(response);
    }
}
