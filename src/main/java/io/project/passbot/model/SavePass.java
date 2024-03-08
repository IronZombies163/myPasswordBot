package io.project.passbot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode
@Setter
@Entity(name = "passwordsData")
public class SavePass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private User user;

    @Override
    public String toString() {
        return password;
    }
}