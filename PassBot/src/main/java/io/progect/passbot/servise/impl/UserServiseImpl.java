package io.progect.passbot.servise.impl;

import io.progect.passbot.Exception.PasswordSaveException;
import io.progect.passbot.model.SavePass;
import io.progect.passbot.model.User;
import io.progect.passbot.repository.PasswordRepository;
import io.progect.passbot.repository.UserRepository;

import io.progect.passbot.servise.UserServise;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiseImpl implements UserServise {

    private final PasswordRepository passwordRepository;
    private  final UserRepository repository;


    @Override
    public void deletePasswordById(Long passwordId) {
        passwordRepository.deleteById(passwordId);
    }

    @Override
    public List<Long> getAllIdSavePassword(Long chatId) {
       User user=repository.findByChatId(chatId);
       if(user!=null){
           List<SavePass> userPasswords=passwordRepository.findByUser_ChatId(chatId);
           return userPasswords.stream()
                  .map(SavePass::getId)
                  .collect(Collectors.toList());
       }
       else {throw  new IllegalStateException("Пользователь не найден");
       }
    }

    @Override
    public int getUserSavePasswordCount(Long chatId) {
        User user=repository.findByChatId(chatId);
        if(user!=null){
            return passwordRepository.countByUser(user);
        } else{

            throw new IllegalStateException("Пользователь не найден");
        }
    }

    @Override
    @Transactional
    public void deleteAllPassword(Long chatId) {
       passwordRepository.deleteByUser_ChatId(chatId);
    }

    @Override
    public List<SavePass> getAllSavePass(Long chatId) {
        return passwordRepository.findByUser_ChatId(chatId);
    }

    @Override
    public void setSavePassword(Long chatId, String password) {
        User user=repository.findByChatId(chatId);
        if(user!=null){

            SavePass pass=new SavePass();
            pass.setUser(user);
            pass.setPassword(password);
            try{ passwordRepository.save(pass);}
            catch (Exception e){
                throw new PasswordSaveException(" Ошибка сохранения пароля: "+ e.getMessage());
            }
        } else {
            throw new PasswordSaveException(" Пользователь не найден по chatId: "+chatId);
        }
    }

    @Override
    public User getUserByChatId(Long chatId) {
        return repository.findByChatId(chatId);
    }
}


