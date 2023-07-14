package io.oneinvest.util;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

public class CsvTest {
    @Test
    public void foo() {
        assertThat(Stream.of("foo").collect(Csv.toCsvLine())).isEqualTo("foo\n");
        assertThat(Stream.of("foo bar").collect(Csv.toCsvLine())).isEqualTo("foo bar\n");
        assertThat(Stream.of("foo,bar").collect(Csv.toCsvLine())).isEqualTo("\"foo,bar\"\n");

        assertThat(Stream.of("foo", "bar").collect(Csv.toCsvLine())).isEqualTo("foo,bar\n");
        assertThat(Stream.of("foo bar", "baz").collect(Csv.toCsvLine())).isEqualTo("foo bar,baz\n");
        assertThat(Stream.of("foo,bar", "baz").collect(Csv.toCsvLine())).isEqualTo("\"foo,bar\",baz\n");
    }
}
