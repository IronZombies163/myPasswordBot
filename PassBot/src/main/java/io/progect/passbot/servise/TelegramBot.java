package io.progect.passbot.servise;

import com.vdurmont.emoji.EmojiParser;
import io.progect.passbot.Exception.PasswordSaveException;
import io.progect.passbot.config.BotConfig;
import io.progect.passbot.config.passCreator;
import io.progect.passbot.model.SavePass;
import io.progect.passbot.model.User;
import io.progect.passbot.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component

public class TelegramBot extends TelegramLongPollingBot { //класс со всем бекондом бота
   @Autowired
    UserRepository userRepository;
   @Autowired
   UserServise userServise;

    private boolean deleteMode;

    final BotConfig botConfig;
   final passCreator passCreator;

private final Map<Long, String> userPass=new HashMap<>();
private final ScheduledExecutorService scheduled;


private final String smile=EmojiParser.parseToUnicode(":love_letter:");
private final String emojiPartyPopper=EmojiParser.parseToUnicode(":tada:");
private final String checkMark = EmojiParser.parseToUnicode(":white_check_mark:");
private final String okayEmoji=EmojiParser.parseToUnicode(":ok:");

    private  final String helpText="Еще раз Алоха, это твой бот-помощник по управлению надежными паролями !^^ \nС вопросами/предложениями писать @sassZombie\n" +
            "Поддержать проект "+smile+"\n" +"Сбербанк:свойномер"+
            "\nEthereum:0x6CefCd4b8C46f07a12eCFc6C48C3DfcE6cf3aa8c";
    public TelegramBot(BotConfig config, passCreator passCreator) {
        this.botConfig = config;
        this.passCreator = passCreator;

        this.scheduled=Executors.newScheduledThreadPool(1);

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/help", "об этом боте" ));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bots command list: " + e.getMessage());
        }

    }
    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
    @Override
    public void onUpdateReceived(Update update) {// проверяем нам что то прислали и есть ли там текст

            if (update.hasMessage() && update.getMessage().hasText()){
                String messageText=update.getMessage().getText();
                long chatId=update.getMessage().getChatId();
                if(messageText.contains("/send")&& botConfig.getOwner()==chatId){
                    String sendText=EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                    var users=userRepository.findAll();
                    for(User user: users){
                        sendMessage(sendText,user.getChatId());
                    }

                }
                else{

                switch (messageText){

                    case "/start": registerUser(update.getMessage());
                    startCommandReceived(update.getMessage().getChat().getFirstName(), chatId); break;

                    case "/help": sendMessage(helpText, chatId); break;

                    case "сохранить": savePassword(chatId); break;
                    case "удаление паролей": deleteMenuKeyboard(chatId, "Внимание! После удаления пароли нельзя восстановить =("); break;
                    //case "количество паролей": countPass(chatId); break;
                    case "назад": mainMenuKeyboard(chatId, okayEmoji); break;
                    case "отмена": deleteMenuKeyboard(chatId, checkMark);break;
                    case "Отмена": deleteMenuKeyboard(chatId, okayEmoji); break;
                    case "перезаписать пароль":  rewritePassword(chatId);
                    break;



                    case "сохраненные пароли":
                        String cloud=EmojiParser.parseToUnicode(":cloud:");
                        String lending=cloud+" Ваши сохраненные праоли "+cloud+"\n";
                        saveMenuKeyboard(chatId, lending);
                        getSavePassword(chatId); break;


                    case "удалить все пароли": deleteAllPassword(chatId);break;
                    case "удалить один пароль":
                        deleteMode = true; // Активируем режим удаления паролей
                        deleteKeyboard(chatId);
                        break;

                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                    case "10":
                        if (deleteMode) {

                            int passwordNumber = Integer.parseInt(messageText);
                            deleteOnePassword(chatId, passwordNumber);
                            deleteMode = false;
                        }
                        break;

                    case "новый пароль":

                        selectionWindow(chatId);
                        checkup(chatId);

                        //считает кол-во сгенерированных паролей
                        User user= userRepository.findById(chatId).orElse(null);
                        Objects.requireNonNull(user).incrementCountPass();
                        userRepository.save(user);
                        break;


                    default: handleDeleteCommand(chatId, messageText);
                }

            }
         }
             if(update.hasCallbackQuery()){
                String data=update.getCallbackQuery().getData();
                long chatId=update.getCallbackQuery().getMessage().getChatId();

                if(data.equals("start_Button")){
                    selectionWindow(chatId);
                }

            }
    }
    private void handleDeleteCommand(long chatId, String userInput) {
        if ("удалить все пароли".equalsIgnoreCase(userInput)) {

            deleteAllPassword(chatId);
        } else if ("назад".equalsIgnoreCase(userInput)) {

            selectionWindow(chatId);
        } else {

            try {
                int passwordNumber = Integer.parseInt(userInput);
                deleteOnePassword(chatId, passwordNumber);
            } catch (NumberFormatException e) {

                sendMessage("Введите корректное число или команду", chatId);
            }
        }
    }
        private void checkup(long chatId){
        User user=userRepository.findByChatId(chatId);
            if(user.getCountPass()==100){
                sendMessage(EmojiParser.parseToUnicode(":space_invader:"),chatId);
                sendMessage("Поздравляю, это твой 100-й пароль ты молодец! <3"+"\nТеперь твой лимит для сохранненых " +
                        "паролей увеличин до 15 "+EmojiParser.parseToUnicode(":relaxed:"), chatId);
            }
        }
        private void rewritePassword(long chatId) {

            int countSavePassword = userServise.getUserSavePasswordCount(chatId);
            if (countSavePassword == 10 || countSavePassword==15) {
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));
                List<Long> listId = userServise.getAllIdSavePassword(chatId);
                Map<Integer, Long> idSavePassword = new HashMap<>();
                for (int i = 0; i < listId.size(); i++) {
                    idSavePassword.put(i, listId.get(i));
                }

                try {

                    userServise.deletePasswordById(idSavePassword.get(9));
                    String saveMessage = userPass.get(chatId);
                    userServise.setSavePassword(chatId, saveMessage);
                    mainMenuKeyboard(chatId, checkMark);
                    

                } catch (EmptyResultDataAccessException e) {
                    sendMessage("пароль не найден", chatId);
                }
                catch (Exception e) {
                    log.error("Ошибка при перезаписи пароля", e);
                }

                }
            }

        private void deleteMenuKeyboard(long chatId, String message){
            SendMessage sendMessage=new SendMessage(String.valueOf(chatId), message);
            //настройка клавиатуры
            ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup();
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);
            //создание кнопок
            List<KeyboardRow> keyboardRows=new ArrayList<>();
            KeyboardRow secondRow= new KeyboardRow();
            secondRow.add("новый пароль");
            KeyboardRow row= new KeyboardRow();
            row.add("удалить все пароли");
            row.add("удалить один пароль");
            keyboardRows.add(row);
            keyboardRows.add(secondRow);
            replyKeyboardMarkup.setKeyboard(keyboardRows);
            try {
                execute(sendMessage);
            } catch (EmptyResultDataAccessException e){
                sendMessage("пароль не найден", chatId);
            }
            catch (Exception e){
                log.error("Ошибка при удаление пароля", e);
            }

        }
        private void deleteAllPassword(long chatId){
            List<SavePass> savePasses=userServise.getAllSavePass(chatId);
        if(savePasses.isEmpty()){
            String sadEmoji=EmojiParser.parseToUnicode(":disappointed_relieved:");
            sendMessage("Извини" +sadEmoji+"\nНо прежде чем пытаться удалить пароли, убедитесь, что у вас уже есть" +
                    " сохраненные", chatId);
        } else {

            try {
                userServise.deleteAllPassword(chatId);
                sendMessage(emojiPartyPopper + " Все пароли удалены " + emojiPartyPopper, chatId);

            } catch (EmptyResultDataAccessException e) {
                sendMessage("пароль не найден", chatId);
            } catch (Exception e) {
                log.info("Ошибка при удаление пароля", e);
            }
        }
    }
    private void deleteOnePassword(long chatId, int numDeleteId){
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            List<Long> listId = userServise.getAllIdSavePassword(chatId);
            Map<Integer, Long> idSavePassword = new HashMap<>();
            for (int i = 0; i < listId.size(); i++) {
                idSavePassword.put(i, listId.get(i));
            }

            try {

                userServise.deletePasswordById(idSavePassword.get(numDeleteId - 1));
                deleteMenuKeyboard(chatId, emojiPartyPopper);
                sendMessage("Пароль успешно удален", chatId);

            } catch (EmptyResultDataAccessException e) {
                sendMessage("пароль не найден", chatId);
            } catch (Exception e) {
                sendMessage("Введите корректное число или команду",chatId);
                log.error("Ошибка при удаление пароля", e);
            }

        }

    private void deleteKeyboard(long chatId){
        List<SavePass> savePasses=userServise.getAllSavePass(chatId);
        if(savePasses.isEmpty()){
            String pray=EmojiParser.parseToUnicode(":pray:");
            sendMessage("Пожалуйста"+pray+"\nПрежде чем пытаться удалить пароль, убедитесь, что вы сохранили хотя бы один)", chatId);
        } else {
            SendMessage message = new SendMessage(String.valueOf(chatId), "Выберете какой пароль хотите удалить:");
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            message.setReplyMarkup(replyKeyboardMarkup);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);

            //создание кнопок
            List<KeyboardRow> keyboardRows = new ArrayList<>();
            KeyboardRow row = new KeyboardRow();

            for (int i = 0; i < savePasses.size(); i++) {
                row.add(String.valueOf(i + 1));
            }

            KeyboardRow cancelRow = new KeyboardRow();
            cancelRow.add("Отмена");
            keyboardRows.add(row);
            keyboardRows.add(cancelRow);
            replyKeyboardMarkup.setKeyboard(keyboardRows);
            executer(message, "произошла ошибка в deleteKeyboard");
        }
    }

    private void saveMenuKeyboard(long chatId, String lending){
        SendMessage message=new SendMessage(String.valueOf(chatId), lending);


        //настройка клавиатуры
        ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup();
        message.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows=new ArrayList<>();
        KeyboardRow secondRow= new KeyboardRow();
        secondRow.add("новый пароль");
        KeyboardRow row= new KeyboardRow();
        row.add("удалить все пароли");
        row.add("удалить один пароль");
        keyboardRows.add(row);
        keyboardRows.add(secondRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        executer(message, "ошибка произошла в saveMenuKeyboard");
    }


    private void savePassword(long chatId){
        int maxSave=10;
        User user=userRepository.findByChatId(chatId);
        if(user.getCountPass()>=100){
            maxSave=15;
        }
       String message=userPass.get(chatId);
        int countPass=userServise.getUserSavePasswordCount(chatId);
       if(countPass<maxSave){try {
           userServise.setSavePassword(chatId, message);
           sendMessage("Пароль сохранен!" + checkMark, chatId);
       } catch (PasswordSaveException e) {

           log.error("Пользователь не найден или ошибка в сохранения пароля" + e.getMessage());
            }
       } else {

           SendMessage sendMessage=new SendMessage(String.valueOf(chatId), "Достигнуто максимальное количество сохраненных паролей("+maxSave+").");


           ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup();
           sendMessage.setReplyMarkup(replyKeyboardMarkup);
           replyKeyboardMarkup.setSelective(true);
           replyKeyboardMarkup.setResizeKeyboard(true);
           replyKeyboardMarkup.setOneTimeKeyboard(false);

           List<KeyboardRow> keyboardRows=new ArrayList<>();
           KeyboardRow secondRow= new KeyboardRow();

           KeyboardRow row= new KeyboardRow();
           row.add("перезаписать пароль");
           secondRow.add("назад");
           keyboardRows.add(row);
           keyboardRows.add(secondRow);
           replyKeyboardMarkup.setKeyboard(keyboardRows);
        executer(sendMessage, "ошибка произошла в savePassword");
        sendMessage("Хотети ли вы перезаписать последний пароль на "+userPass.get(chatId), chatId);
    }
}

    private void getSavePassword(long chatId){// выдает все сохраненные пароли

    List<SavePass> savePasses=userServise.getAllSavePass(chatId);
  if(savePasses.isEmpty() ){
       sendMessage("Нет сохраненных паролей", chatId);
    } else {
            int count=0;
        for(SavePass el : savePasses){
            count++;
            sendMessage(count+". "+el.toString()+"\n",chatId );
        }
    }
}
    private void registerUser(Message msg) {

        if(userRepository.findById(msg.getChatId()).isEmpty()){
            var chatId=msg.getChatId();
            var chat=msg.getChat();

            User user= new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setRegisterAt(new Timestamp(System.currentTimeMillis()));
            user.setCountPass(0);
            userRepository.save(user);
        }

    }
        private String reRollPassword(long chatId){
            String password= passCreator.creatPassword();
            userPass.put(chatId, password);
            scheduled.schedule(()->userPass.remove(chatId),1,TimeUnit.HOURS);
            return password;
        }

    private void selectionWindow(long chatId) {
        mainMenuKeyboard(chatId, reRollPassword(chatId));
    }
    private void mainMenuKeyboard (long chatId, String message){
        SendMessage sendMessage=new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(String.valueOf(chatId));


        ReplyKeyboardMarkup markup=new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);

        List<KeyboardRow> rows=new ArrayList<>();

        KeyboardRow firstRow= new KeyboardRow();
        KeyboardRow secondRow= new KeyboardRow();
        firstRow.add("сохранить");
        firstRow.add("новый пароль");
        secondRow.add("удаление паролей");
        secondRow.add("сохраненные пароли");
        rows.add(firstRow);
        rows.add(secondRow);
        markup.setKeyboard(rows);

        sendMessage.setReplyMarkup(markup);
        executer(sendMessage, "не отправилось сообщение в mainMenuKeyboard");

    }
    private void startCommandReceived(String name,  long chatId){
        SendMessage message=new SendMessage();
        String emojiFine= EmojiParser.parseToUnicode(":wink:");
            String answer="Алоха, "+ name+", давай придумаем надежный пароль ^^ \n\nЖми старт что бы начать! "+emojiFine;
            message.setChatId(String.valueOf(chatId));
            message.setText(answer);
            InlineKeyboardMarkup keyboardMarkup=new InlineKeyboardMarkup();

            List<List<InlineKeyboardButton>> rowInLine= new ArrayList<>();
            List<InlineKeyboardButton> row=new ArrayList<>();
            var startButton= new InlineKeyboardButton();
            startButton.setText("start!");
            startButton.setCallbackData("start_Button");
            row.add(startButton); rowInLine.add(row);
            keyboardMarkup.setKeyboard(rowInLine);
            message.setReplyMarkup(keyboardMarkup);
        User user= userRepository.findById(chatId).orElse(null);
        if (user != null) {
            user.incrementCountPass();
            userRepository.save(user);
        }
        executer(message,"не отправилось сообщение в startCommandReceived" );

    }
        private void executer(SendMessage message, String exceptionMessage){
            try {
                execute(message);

            } catch (TelegramApiException e) {
                log.error(exceptionMessage);
            }
        }

    private void sendMessage(String sendText, long chatId){
        SendMessage sendMessage= new SendMessage();
        sendMessage.setText(sendText);
        sendMessage.setChatId(String.valueOf(chatId));

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("не отправилось сообщение"+ e);
        }
    }

}
