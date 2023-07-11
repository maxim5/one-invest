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
    private final MoexProvider moexProvider = new MoexProvider();
    private final SmartlabProvider smartlabProvider = new SmartlabProvider();

    public @NotNull BondData fetchAllData(@NotNull Isin isin) {
        return fetchAllData(isin, Options.ALL);
    }

    public @NotNull BondData fetchAllData(@NotNull Isin isin, @NotNull Options options) {
        DohodData dohodData = options.fetchDohod() ? fetchDohodData(isin) : null;
        FinPlanData finPlanData = options.fetchFinPlan() ? fetchFinPlanData(isin) : null;
        MoexData moexData = options.fetchMoex() ? fetchMoexData(isin) : null;
        SmartlabData smartlabData = options.fetchSmartlab() ? fetchSmartlabData(isin) : null;

        String board = Stream.of(smartlabData, dohodData)
            .filter(Objects::nonNull)
            .findAny()
            .map(BondBasicInfo::board)
            .orElse("TQCB");
        BlackTerminalData blackTerminalData = options.fetchBlackTerminal() ? fetchBlackTerminalData(isin, board) : null;

        return new BondData(isin, dohodData, blackTerminalData, finPlanData, moexData, smartlabData);
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

    private @Nullable MoexData fetchMoexData(@NotNull Isin isin) {
        try {
            return moexProvider.fetch(isin);
        } catch (Throwable throwable) {
            log.atWarning().withCause(throwable).log("Failed to fetch Moex data for: %s", isin);
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

    public record Options(boolean fetchBlackTerminal,
                          boolean fetchDohod,
                          boolean fetchFinPlan,
                          boolean fetchMoex,
                          boolean fetchSmartlab) {
        public static final Options ALL = new Options(true, true, true, false, true);
        public static final Options NONE = new Options(false, false, false, false, false);
        public static final Options JUST_BLACK_TERMINAL = new Options(true, false, false, false, false);
        public static final Options JUST_DOHOD = new Options(false, true, false, false, false);
        public static final Options JUST_FIN_PLAN = new Options(false, false, true, false, false);
        public static final Options JUST_MOEX = new Options(false, false, false, true, false);
        public static final Options JUST_SMARTLAB = new Options(false, false, false, false, true);
    }
}
