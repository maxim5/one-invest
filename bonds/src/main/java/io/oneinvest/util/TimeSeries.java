package io.oneinvest.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public class TimeSeries {
    private final long[] times;
    private final double[] values;

    public TimeSeries(long[] times, double[] values) {
        this.times = times;
        this.values = values;
    }

    public static <T> @NotNull TimeSeries from(@NotNull Collection<T> items,
                                               @NotNull ToLongFunction<T> timeGetter,
                                               @NotNull ToDoubleFunction<T> valueGetter) {
        long[] times = new long[items.size()];
        double[] values = new double[items.size()];
        int i = 0;
        for (T item : items) {
            times[i] = timeGetter.applyAsLong(item);
            values[i] = valueGetter.applyAsDouble(item);
            i++;
        }
        return new TimeSeries(times, values);
    }

    @Override
    public String toString() {
        return "TimeSeries{times=%s, values=%s}".formatted(Arrays.toString(times), Arrays.toString(values));
    }
}
