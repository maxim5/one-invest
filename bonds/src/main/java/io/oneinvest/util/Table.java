package io.oneinvest.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Table {
    private static final Table EMPTY_TABLE = new Table(new Cell[0][0]);

    private final Cell[][] cells;

    private Table(@NotNull Cell @NotNull[] @NotNull[] cells) {
        this.cells = cells;
    }

    public static <T> @NotNull Table fromRows(@NotNull List<T> rows, @NotNull Function<T, Object[]> expand) {
        int rowsNum = rows.size();
        int colsNum = -1;
        Cell[][] cells = null;
        int i = 0;
        for (T row : rows) {
            Object[] rawValues = expand.apply(row);
            if (cells == null) {
                colsNum = rawValues.length;
                cells = new Cell[rowsNum][colsNum];
            }
            assert rawValues.length == colsNum : "Column size mismatch: %s vs %s".formatted(colsNum, Arrays.toString(rawValues));
            for (int j = 0; j < colsNum; ++j) {
                cells[i][j] = new Cell(rawValues[j], null);
            }
            i++;
        }
        return cells != null ? new Table(cells) : EMPTY_TABLE;
    }

    public static <T> @NotNull Table fromRows(@NotNull Formats formats,
                                              @NotNull List<T> rows,
                                              @NotNull Function<T, Object[]> expand) {
        int rowsNum = rows.size();
        int colsNum = formats.length();
        Cell[][] cells = new Cell[rowsNum][colsNum];
        int i = 0;
        for (T row : rows) {
            Object[] rawValues = expand.apply(row);
            assert rawValues.length == colsNum : "Column size mismatch: %s vs %s".formatted(formats, Arrays.toString(rawValues));
            for (int j = 0; j < colsNum; ++j) {
                cells[i][j] = new Cell(rawValues[j], formats.formats[j]);
            }
            i++;
        }
        return rowsNum > 0 ? new Table(cells) : EMPTY_TABLE;
    }

    public int rowsNum() {
        return cells.length;
    }

    public int columnsNum() {
        return cells[0].length;
    }

    public @NotNull Table withFormats(@NotNull Formats formats) {
        int columnsNum = columnsNum();
        int rowsNum = rowsNum();
        assert columnsNum == formats.length() : "Column size mismatch: %s vs %s".formatted(columnsNum, formats);

        Cell[][] cells = new Cell[rowsNum][columnsNum];
        for (int i = 0; i < rowsNum; ++i) {
            Cell[] row = this.cells[i];
            Cell[] newRow = cells[i];
            for (int j = 0; j < columnsNum; ++j) {
                newRow[j] = row[j].withFormat(formats.formats[j]);
            }
        }
        return new Table(cells);
    }

    public void println(int padding) {
        String padded = " ".repeat(padding);
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                System.out.print(cell.formatted());
                System.out.print(padded);
            }
            System.out.println();
        }
    }

    public record Formats(@NotNull String[] formats) {
        public static @NotNull Formats of(@NotNull String line) {
            String[] split = line.trim().split(" ");
            return new Formats(split);
        }

        public int length() {
            return formats.length;
        }
    }

    private record Cell(@Nullable Object rawValue, @Nullable String format) {
        public @NotNull Cell withFormat(@Nullable String format) {
            return new Cell(rawValue, format);
        }

        public @NotNull String formatted() {
            return rawValue == null ? "" : format == null ? String.valueOf(rawValue) : format.formatted(rawValue);
        }
    }
}
