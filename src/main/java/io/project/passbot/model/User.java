package io.project.passbot.model;

import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode
@Getter
@Setter
@Entity(name = "usersData")
public class User {

    @Id
    private Long chatId;
    private String firstName;
    private LocalDateTime registerAt;
    private long countPass;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<SavePass> savePasses;

    public void incrementCountPass() {
        if (countPass == 0) {
            countPass++;
        }
    }


}
