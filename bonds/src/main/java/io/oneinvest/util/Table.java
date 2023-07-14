package io.oneinvest.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Table {
    private static final Header EMPTY_HEADER = new Header(null);
    private static final Table EMPTY_TABLE = new Table(new Cell[0][0], EMPTY_HEADER);

    private final Cell[][] cells;
    private final Header header;

    private Table(@NotNull Cell @NotNull[] @NotNull[] cells, @NotNull Header header) {
        this.cells = cells;
        this.header = header;
        assert header.isEmpty() || header.length() == columnsNum() : "Column size mismatch: %s vs %s".formatted(header, columnsNum());
    }

    public static <T> @NotNull Table fromRows(@NotNull List<T> rows, @NotNull Function<T, Object[]> expand) {
        return fromRows(EMPTY_HEADER, rows, expand);
    }

    public static <T> @NotNull Table fromRows(@NotNull Formats formats,
                                              @NotNull List<T> rows,
                                              @NotNull Function<T, Object[]> expand) {
        return fromRows(EMPTY_HEADER, formats, rows, expand);
    }

    public static <T> @NotNull Table fromRows(@NotNull Header header,
                                              @NotNull List<T> rows,
                                              @NotNull Function<T, Object[]> expand) {
        int rowsNum = rows.size();
        int colsNum = header.isEmpty() ? -1 : header.length();
        Cell[][] cells = colsNum < 0 ? null : new Cell[rowsNum][colsNum];
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
        return cells != null ? new Table(cells, header) : EMPTY_TABLE;
    }

    public static <T> @NotNull Table fromRows(@NotNull Header header,
                                              @NotNull Formats formats,
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
        return rowsNum > 0 ? new Table(cells, header) : EMPTY_TABLE;
    }

    public int rowsNum() {
        return cells.length;
    }

    public int columnsNum() {
        return cells.length == 0 ? 0 : cells[0].length;
    }

    public @NotNull Table withHeader(@NotNull Header header) {
        return new Table(cells, header);
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
        return new Table(cells, header);
    }

    public void println(int padding) {
        String padded = " ".repeat(padding);
        for (String col : header.namesOrEmpty()) {
            System.out.print(col);
            System.out.print(padded);
        }
        System.out.println();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                System.out.print(cell.formatted());
                System.out.print(padded);
            }
            System.out.println();
        }
    }

    public void toCsv(@NotNull Writer writer) {
        try {
            writer.write(Arrays.stream(header.namesOrEmpty()).collect(Csv.toCsvLine()));
            for (Cell[] row : cells) {
                writer.write(Arrays.stream(row).map(Cell::formatted).collect(Csv.toCsvLine()));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public record Header(@NotNull String @Nullable[] names) {
        public static @NotNull Header of(@NotNull String @Nullable ... names) {
            return new Header(names);
        }

        public @NotNull String @NotNull[] namesOrEmpty() {
            return names != null ? names : new String[0];
        }

        public boolean isEmpty() {
            return names == null;
        }

        public boolean isPresent() {
            return names != null;
        }

        public int length() {
            return names != null ? names.length : 0;
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
