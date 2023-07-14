package io.oneinvest.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collector;

public class Csv {
    public static @NotNull Collector<String, ?, String> toCsvLine() {
        return Collector.of(
            () -> new ArrayList<String>(),
            (list, e) -> list.add(escapeSpecialCharacters(e)),
            (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            },
            list -> String.join(",", list) + "\n"
        );
    }

    private static @NotNull String escapeSpecialCharacters(@NotNull String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
