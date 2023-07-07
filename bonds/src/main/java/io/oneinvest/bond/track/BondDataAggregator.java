package io.oneinvest.bond.track;

import com.google.common.flogger.FluentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BondDataAggregator {
    private static final FluentLogger log = FluentLogger.forEnclosingClass();

    private final BlackTerminalProvider blackTerminalProvider = new BlackTerminalProvider();
    private final DohodProvider dohodProvider = new DohodProvider();
    private final FinPlanProvider finPlanProvider = new FinPlanProvider();

    public @NotNull BondData fetchAllData(@NotNull Isin isin) {
        BlackTerminalData blackTerminalData = fetchBlackTerminalData(isin);
        DohodData dohodData = fetchDohodData(isin);
        FinPlanData finPlanData = fetchFinPlanData(isin);
        return new BondData(isin, dohodData, blackTerminalData, finPlanData);
    }

    private @Nullable BlackTerminalData fetchBlackTerminalData(@NotNull Isin isin) {
        try {
            return blackTerminalProvider.fetch(isin);
        } catch (Throwable throwable) {
            log.atWarning().withCause(throwable).log("Failed to fetch BlackTerminal data for: %s", isin);
            return null;
        }
    }

    private @Nullable DohodData fetchDohodData(@NotNull Isin isin) {
        try {
            return dohodProvider.fetch(isin);
        } catch (Throwable throwable) {
            log.atWarning().withCause(throwable).log("Failed to fetch Dohod data for: %s", isin);
            return null;
        }
    }

    private @Nullable FinPlanData fetchFinPlanData(@NotNull Isin isin) {
        try {
            return finPlanProvider.fetch(isin);
        } catch (Throwable throwable) {
            log.atWarning().withCause(throwable).log("Failed to fetch FinPlan data for: %s", isin);
            return null;
        }
    }
}
