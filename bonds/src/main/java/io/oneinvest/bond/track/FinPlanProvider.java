package io.oneinvest.bond.track;

import io.oneinvest.util.Http;
import org.jetbrains.annotations.NotNull;

public class FinPlanProvider {
    public @NotNull FinPlanData fetch(@NotNull Isin isin) {
        String url = "https://fin-plan.org/lk/obligations/%s/".formatted(isin);
        String response = Http.httpCall(url);
        return FinPlanData.fromHttpRu(response);
    }
}
