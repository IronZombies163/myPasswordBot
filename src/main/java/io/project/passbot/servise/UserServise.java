package io.project.passbot.servise;

import io.project.passbot.model.SavePass;
import io.project.passbot.model.User;
import jakarta.transaction.Transactional;

import java.util.*;


public interface UserServise {
    @Transactional
    void deletePasswordById(Long chatId);

    @Transactional
    List<Long> getAllIdSavePassword(Long chatId);

    @Transactional
    int getUserSavePasswordCount(Long chatId);

    @Transactional
    void deleteAllPassword(Long chatId);

    @Transactional
    List<SavePass> getAllSavePass(Long chatId);

    void setSavePassword(Long chatId, String password);

}

