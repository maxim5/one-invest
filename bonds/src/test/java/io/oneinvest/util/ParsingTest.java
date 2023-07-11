package io.oneinvest.util;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class ParsingTest {
    @Test
    public void extractAll_simple() {
        assertThat(Parsing.extractAll("", "<", ">")).isEmpty();
        assertThat(Parsing.extractAll(">", "<", ">")).isEmpty();
        assertThat(Parsing.extractAll("<", "<", ">")).isEmpty();
        assertThat(Parsing.extractAll("><", "<", ">")).isEmpty();
        assertThat(Parsing.extractAll("<>", "<", ">")).containsExactly("");

        assertThat(Parsing.extractAll("<<<<", "<", ">")).isEmpty();
        assertThat(Parsing.extractAll(">>>>", "<", ">")).isEmpty();

        assertThat(Parsing.extractAll("<foo>", "<", ">")).containsExactly("foo");
        assertThat(Parsing.extractAll("<foo><bar>", "<", ">")).containsExactly("foo", "bar");

        assertThat(Parsing.extractAll("<foo> <bar> <foo>", "<", ">")).containsExactly("foo", "bar", "foo");
        assertThat(Parsing.extractAll("<foo> <bar> <foo><", "<", ">")).containsExactly("foo", "bar", "foo");
        assertThat(Parsing.extractAll("<foo> <bar> <foo>>", "<", ">")).containsExactly("foo", "bar", "foo");
    }
}
