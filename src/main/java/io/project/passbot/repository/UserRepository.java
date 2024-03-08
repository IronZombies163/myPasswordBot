package io.project.passbot.repository;

import io.project.passbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByChatId(Long chatId);
}
