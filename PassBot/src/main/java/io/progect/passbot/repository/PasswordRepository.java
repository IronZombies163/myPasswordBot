package io.progect.passbot.repository;

import io.progect.passbot.model.SavePass;
import io.progect.passbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface PasswordRepository extends JpaRepository<SavePass, Long> {

    int countByUser(User user);
    void deleteByUser_ChatId(Long chatId);
    List<SavePass> findByUser_ChatId(Long chatId);
}
