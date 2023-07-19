package io.oneinvest.bond.track;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IsinTest {
    @Test
    public void valid_value() {
        assertThat(Isin.of("RU000A105YQ9").matches("RU000A105YQ9"));
        assertThat(Isin.of("ru000a105yq9").matches("RU000A105YQ9"));
        assertThat(Isin.of("Ru000a105Yq9").matches("RU000A105YQ9"));
        assertThat(Isin.of("RU000A105YQ9").matches("ru000a105yq9"));
    }

    @Test
    public void invalid_value() {
        assertThrows(AssertionError.class, () -> Isin.of("foo"));
        assertThrows(AssertionError.class, () -> Isin.of("RU000A105YQ91"));
        assertThrows(AssertionError.class, () -> Isin.of("RU000A105YQ"));
        assertThrows(AssertionError.class, () -> Isin.of("RU000A105YQ9 RU000A105YQ9"));
    }
}
