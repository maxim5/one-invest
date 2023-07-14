package io.oneinvest.util;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

public class CsvTest {
    @Test
    public void foo() {
        assertThat(Stream.of("foo").collect(Csv.toCsvLine())).isEqualTo("foo");
        assertThat(Stream.of("foo bar").collect(Csv.toCsvLine())).isEqualTo("foo bar");
        assertThat(Stream.of("foo,bar").collect(Csv.toCsvLine())).isEqualTo("\"foo,bar\"");

        assertThat(Stream.of("foo", "bar").collect(Csv.toCsvLine())).isEqualTo("foo,bar");
        assertThat(Stream.of("foo bar", "baz").collect(Csv.toCsvLine())).isEqualTo("foo bar,baz");
        assertThat(Stream.of("foo,bar", "baz").collect(Csv.toCsvLine())).isEqualTo("\"foo,bar\",baz");
    }
}
