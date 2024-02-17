package io.progect.passbot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.progect.passbot.model.SavePass;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
@Data
@Entity(name = "usersDataTable")
public class User {

    @Id
    private Long chatId;
    private String firstName;
    private Timestamp registerAt;
    private long countPass;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL )//,fetch = FetchType.EAGER
    @Size(max=10)
    private Set<SavePass> savePasses;
    public void incrementCountPass(){
        if(countPass ==0){
            countPass =1;
        }
        else{
            countPass++;
        }
    } //прибавляет 1 при каждой генераций паролей


}
