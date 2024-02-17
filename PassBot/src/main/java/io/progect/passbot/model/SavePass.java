package io.progect.passbot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
@Data
@Entity(name = "passwordsDataTable")
public class SavePass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;

    @ManyToOne
    @JoinColumn(name="chat_id")
    private User user;

    @Override
    public String toString() {
        return  password;
    }
}