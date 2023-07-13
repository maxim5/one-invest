package io.oneinvest.bond.track;

import com.google.common.flogger.FluentLogger;
import io.oneinvest.util.Http.HttpOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class BondDataAggregator implements AutoCloseable {
    private static final FluentLogger log = FluentLogger.forEnclosingClass();

    private final HttpOptions httpOptions = new HttpOptions(TimeUnit.DAYS.toMillis(7));
    private final BlackTerminalProvider blackTerminalProvider = new BlackTerminalProvider();
    private final DohodProvider dohodProvider = new DohodProvider();
    private final FinPlanProvider finPlanProvider = new FinPlanProvider();
    private final MoexProvider moexProvider = new MoexProvider();
    private final SmartlabProvider smartlabProvider = new SmartlabProvider();

    public @NotNull BondData fetchAllData(@NotNull Isin isin) {
        return fetchAllData(isin, Options.ALL);
    }

    public @NotNull BondData fetchAllData(@NotNull Isin isin, @NotNull Options options) {
        DohodData dohodData = fetchDohodData(isin, options);
        FinPlanData finPlanData = fetchFinPlanData(isin, options);
        MoexData moexData = fetchMoexData(isin, options);
        SmartlabData smartlabData = fetchSmartlabData(isin, options);

        String board = Stream.of(smartlabData, dohodData)
            .filter(Objects::nonNull)
            .findAny()
            .map(BondBasicInfo::board)
            .orElse("TQCB");
        BlackTerminalData blackTerminalData = fetchBlackTerminalData(isin, board, options);

        return new BondData(isin, dohodData, blackTerminalData, finPlanData, moexData, smartlabData);
    }

    private @Nullable BlackTerminalData fetchBlackTerminalData(@NotNull Isin isin, @NotNull String board, @NotNull Options options) {
        try {
            if (options.fetchBlackTerminal()) {
                return blackTerminalProvider.fetch(isin, board, httpOptions);
            }
            return null;
        } catch (Throwable throwable) {
            log.atWarning().withCause(throwable).log("Failed to fetch BlackTerminal data for: %s", isin);
            return null;
        }
    }

    private @Nullable DohodData fetchDohodData(@NotNull Isin isin, @NotNull Options options) {
        try {
            if (options.fetchDohod()) {
                return dohodProvider.fetch(isin, httpOptions);
            }
            return null;
        } catch (Throwable throwable) {
            log.atWarning().withCause(throwable).log("Failed to fetch Dohod data for: %s", isin);
            return null;
        }
    }

    private @Nullable FinPlanData fetchFinPlanData(@NotNull Isin isin, @NotNull Options options) {
        try {
            if (options.fetchFinPlan()) {
                return finPlanProvider.fetch(isin, httpOptions);
            }
            return null;
        } catch (Throwable throwable) {
            log.atWarning().withCause(throwable).log("Failed to fetch FinPlan data for: %s", isin);
            return null;
        }
    }

    private @Nullable MoexData fetchMoexData(@NotNull Isin isin, @NotNull Options options) {
        try {
            if (options.fetchMoex()) {
                return moexProvider.fetch(isin, httpOptions);
            }
            return null;
        } catch (Throwable throwable) {
            log.atWarning().withCause(throwable).log("Failed to fetch Moex data for: %s", isin);
            return null;
        }
    }

    private @Nullable SmartlabData fetchSmartlabData(@NotNull Isin isin, @NotNull Options options) {
        try {
            if (options.fetchSmartlab()) {
                return smartlabProvider.fetch(isin, httpOptions);
            }
            return null;
        } catch (Throwable throwable) {
            log.atWarning().withCause(throwable).log("Failed to fetch SmartLab data for: %s", isin);
            return null;
        }
    }

    @Override
    public void close() {
        blackTerminalProvider.close();
        dohodProvider.close();
        finPlanProvider.close();
        moexProvider.close();
        smartlabProvider.close();
    }

    public record Options(boolean fetchBlackTerminal,
                          boolean fetchDohod,
                          boolean fetchFinPlan,
                          boolean fetchMoex,
                          boolean fetchSmartlab) {
        public static final Options ALL = new Options(true, true, true, true, true);
        public static final Options NONE = new Options(false, false, false, false, false);
        public static final Options JUST_BLACK_TERMINAL = new Options(true, false, false, false, false);
        public static final Options JUST_DOHOD = new Options(false, true, false, false, false);
        public static final Options JUST_FIN_PLAN = new Options(false, false, true, false, false);
        public static final Options JUST_MOEX = new Options(false, false, false, true, false);
        public static final Options JUST_SMARTLAB = new Options(false, false, false, false, true);
    }
}
