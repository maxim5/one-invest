package io.oneinvest.util;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class ParserTest {
    @Test
    public void extractAll_simple() {
        assertThat(Parser.extractAll("", "<", ">")).isEmpty();
        assertThat(Parser.extractAll(">", "<", ">")).isEmpty();
        assertThat(Parser.extractAll("<", "<", ">")).isEmpty();
        assertThat(Parser.extractAll("><", "<", ">")).isEmpty();
        assertThat(Parser.extractAll("<>", "<", ">")).containsExactly("");

        assertThat(Parser.extractAll("<<<<", "<", ">")).isEmpty();
        assertThat(Parser.extractAll(">>>>", "<", ">")).isEmpty();

        assertThat(Parser.extractAll("<foo>", "<", ">")).containsExactly("foo");
        assertThat(Parser.extractAll("<foo><bar>", "<", ">")).containsExactly("foo", "bar");

        assertThat(Parser.extractAll("<foo> <bar> <foo>", "<", ">")).containsExactly("foo", "bar", "foo");
        assertThat(Parser.extractAll("<foo> <bar> <foo><", "<", ">")).containsExactly("foo", "bar", "foo");
        assertThat(Parser.extractAll("<foo> <bar> <foo>>", "<", ">")).containsExactly("foo", "bar", "foo");
    }
}
