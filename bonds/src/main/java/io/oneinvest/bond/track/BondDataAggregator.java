package io.oneinvest.bond.track;

import com.google.common.flogger.FluentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class BondDataAggregator {
    private static final FluentLogger log = FluentLogger.forEnclosingClass();

    private final BlackTerminalProvider blackTerminalProvider = new BlackTerminalProvider();
    private final DohodProvider dohodProvider = new DohodProvider();
    private final FinPlanProvider finPlanProvider = new FinPlanProvider();
    private final SmartlabProvider smartlabProvider = new SmartlabProvider();

    public @NotNull BondData fetchAllData(@NotNull Isin isin) {
        DohodData dohodData = fetchDohodData(isin);
        FinPlanData finPlanData = fetchFinPlanData(isin);
        SmartlabData smartlabData = fetchSmartlabData(isin);

        String board = Stream.of(smartlabData, dohodData)
            .filter(Objects::nonNull)
            .findAny()
            .map(BondBasicInfo::board)
            .orElse("TQCB");
        BlackTerminalData blackTerminalData = fetchBlackTerminalData(isin, board);

        return new BondData(isin, dohodData, blackTerminalData, finPlanData, smartlabData);
    }

    private @Nullable BlackTerminalData fetchBlackTerminalData(@NotNull Isin isin, @NotNull String board) {
        try {
            return blackTerminalProvider.fetch(isin, board);
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

    private @Nullable SmartlabData fetchSmartlabData(@NotNull Isin isin) {
        try {
            return smartlabProvider.fetch(isin);
        } catch (Throwable throwable) {
            log.atWarning().withCause(throwable).log("Failed to fetch SmartLab data for: %s", isin);
            return null;
        }
    }
}
