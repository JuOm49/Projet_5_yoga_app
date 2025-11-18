package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TeacherTest {

    @Test
    void gettersAndSetters_shouldWork() {
        Teacher t = new Teacher();
        t.setId(4L);
        t.setFirstName("Mario");
        t.setLastName("Castello");
        LocalDateTime now = LocalDateTime.parse("2025-11-12T10:00:00");
        t.setCreatedAt(now);
        t.setUpdatedAt(now);

        assertThat(t.getId()).isEqualTo(4L);
        assertThat(t.getFirstName()).isEqualTo("Mario");
        assertThat(t.getLastName()).isEqualTo("Castello");
        assertThat(t.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void equalsAndHashCode_dependOnId() {
        Teacher teacher1 = Teacher.builder().id(2L).firstName("Alban").lastName("Poto").build();
        Teacher teacher2 = Teacher.builder().id(2L).firstName("Xavier").lastName("Balou").build();

        assertThat(teacher1).isEqualTo(teacher2);
        assertThat(teacher1.hashCode()).isEqualTo(teacher2.hashCode());
    }
}

