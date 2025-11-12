package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    void gettersAndSetters_shouldWork() {
        User u = new User();
        u.setId(1L);
        u.setEmail("alban.van@orange.fr");
        u.setFirstName("Alban");
        u.setLastName("Van");
        u.setPassword("password123.;");
        u.setAdmin(true);
        LocalDateTime now = LocalDateTime.parse("2025-11-12T10:00:00");
        u.setCreatedAt(now);
        u.setUpdatedAt(now);

        assertThat(u.getId()).isEqualTo(1L);
        assertThat(u.getEmail()).isEqualTo("alban.van@orange.fr");
        assertThat(u.getFirstName()).isEqualTo("Alban");
        assertThat(u.getLastName()).isEqualTo("Van");
        assertThat(u.getPassword()).isEqualTo("password123.;");
        assertThat(u.isAdmin()).isTrue();
        assertThat(u.getCreatedAt()).isEqualTo(now);
        assertThat(u.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void equalsAndHashCode_dependOnId() {
        User user1 = User.builder().id(10L).email("xavier@wanadoo.fr").firstName("Xavier").lastName("Yago").password("password123;").admin(false).build();
        User user2 = User.builder().id(10L).email("olivier@alcatel.fr").firstName("Olivier").lastName("Ursul").password("password1234;").admin(true).build();

        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void builder_shouldBuildCorrectly() {
        User user = User.builder()
                .id(5L)
                .email("julien@free.fr")
                .firstName("Julien")
                .lastName("Om")
                .password("pwd123;P")
                .admin(false)
                .build();

        assertThat(user.getId()).isEqualTo(5L);
        assertThat(user.getEmail()).isEqualTo("julien@free.fr");
        assertThat(user.getFirstName()).isEqualTo("Julien");
        assertThat(user.getPassword()).isEqualTo(("pwd123;P"));
    }
}

