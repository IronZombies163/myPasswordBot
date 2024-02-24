package io.progect.passbot.servise;

import io.progect.passbot.model.SavePass;
import io.progect.passbot.model.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
    @Transactional
    void setSavePassword(Long chatId, String password);

