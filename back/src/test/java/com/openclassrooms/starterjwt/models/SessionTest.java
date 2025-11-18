package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionTest {

    @Test
    void gettersAndSetters_shouldWork() {
        Session s = new Session();
        s.setId(1L);
        s.setName("Session yoga test");
        s.setDescription("Description session yoga test");

        LocalDate localDate = LocalDate.of(2025, 12, 31);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        s.setDate(date);

        Teacher t = new Teacher();
        t.setId(4L);
        t.setFirstName("Mario");
        t.setLastName("Castello");
        s.setTeacher(t);
        s.setUsers(Collections.emptyList());

        assertThat(s.getId()).isEqualTo(1L);
        assertThat(s.getName()).isEqualTo("Session yoga test");
        assertThat(s.getDescription()).isEqualTo("Description session yoga test");
        assertThat(s.getDate()).isEqualTo(date);
        assertThat(s.getTeacher()).isEqualTo(t);
    }

    @Test
    void equalsAndHashCode_dependOnId() {
        // different ids -> should NOT be equal because equals/hashCode are based on id
        Session a = Session.builder().id(7L).name("Session yoga").date(new Date()).description("a new session of yoga").build();
        Session b = Session.builder().id(8L).name("Other yoga").date(new Date()).description("an other session of yoga").build();

        assertThat(a).isNotEqualTo(b);
        assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
    }

    @Test
    void equalsAndHashCode_sameId_areEqual() {
        // same id -> should be equal (equals/hashCode use id only)
        Session a = Session.builder().id(7L).name("Session yoga").date(new Date()).description("a new session of yoga").build();
        Session b = Session.builder().id(7L).name("Different name").date(new Date()).description("different description").build();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
