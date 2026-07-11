package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.valueobject.BirthDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class BithDateTest {

    @Test
    void shouldGenerate() {
        BirthDate birthDate = new BirthDate(LocalDate.of(1989, 12, 21));
        Assertions.assertThat(birthDate).hasToString("1989-12-21");
    }

    @Test
    void shouldReturnCorrectAge() {
        var myAge = LocalDate.now().minusYears(36);
        BirthDate birthDate = new BirthDate(myAge);

        Assertions.assertThat(birthDate.age()).isEqualTo(36);
    }

    @Test
    void shouldNotGenerateBecauseIsAfterToday() {
        var tomorrow = LocalDate.now().plusDays(1);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new BirthDate(tomorrow));
    }
}
