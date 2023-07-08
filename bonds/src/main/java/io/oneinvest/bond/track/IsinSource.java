package io.oneinvest.bond.track;

import io.oneinvest.util.DevPaths;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class IsinSource {
    private static final String ISIN_TXT = "isin.txt";
    private static final Path ISIN_TXT_PATH = Path.of(DevPaths.BONDS_RESOURCES, "isin.txt");

    public static @NotNull List<Isin> fromIsinTxt(@NotNull Path path) {
        try (Stream<String> stream = Files.lines(path)) {
            return stream.map(Isin::of).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static @NotNull List<Isin> fromIsinTxt() {
        return fromIsinTxt(ISIN_TXT_PATH);
    }

    public static void main(String[] args) {
        System.out.println(DevPaths.PROJECT_HOME);
        System.out.println(ISIN_TXT_PATH);
        System.out.println(ISIN_TXT_PATH.toFile().exists());
    }
}
