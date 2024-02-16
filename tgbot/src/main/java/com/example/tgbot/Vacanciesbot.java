package com.example.tgbot;

import com.example.tgbot.dto.Vacancydto;
import com.example.tgbot.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.ArrayList;
import java.util.*;

@Component
public class Vacanciesbot extends TelegramLongPollingBot {
    //6643912031:AAFCWp8GtRhhpoKU0JGpHJQDvl-2UftHd5M
    @Autowired
    private VacancyService vacancyService;
    private final Map<Long, String> lastShownVacancyLevel = new HashMap<>();


    public Vacanciesbot() {
        super("6643912031:AAHDTg5g43xb2Gdz9ogtsga6XJrrAx1a0mw");
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.getMessage() != null) {
                handleStartCommand(update);
            }
            if (update.getCallbackQuery() != null) {
                String CallBackData = update.getCallbackQuery().getData();

                if ("showFirstYearTeacher".equals(CallBackData)) {
                    showJuniorVacancies(update);
                } else if ("showSecondYearTeacher".equals(CallBackData)) {
                    showMiddleVacancies(update);
                }
                else if ("showThirdYearTeacher".equals(CallBackData)) {
                    showSeniorVacancies(update);
                }
                else if (CallBackData.startsWith("vacancyId=")) {
                     String id = CallBackData.split("=") [1];
                    showVacancyDescription(id, update);

                } else if ("backToVacancies".equals(CallBackData)) {
                    handleBackToVacanciesCommand(update);

                } else if ("backToStartMenu".equals(CallBackData)) {
                    handleToStartCommand(update);

                }
            }

        } catch(Exception e) {
            throw new RuntimeException( "Can't send message to user: ", e);
        }
    }

    private void handleToStartCommand(Update update) throws TelegramApiException {
     SendMessage sendMessage = new SendMessage();
     sendMessage.setText("Choose title: ");
     sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
     sendMessage.setReplyMarkup(getStartMenu());
     execute(sendMessage);
    }
    private void  handleBackToVacanciesCommand(Update update) throws TelegramApiException {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String level = lastShownVacancyLevel.get(chatId);
        if ("junior".equals(level)) {
            showJuniorVacancies(update);
        } else if ("middle".equals(level)) {
            showMiddleVacancies(update);
        } else if ("senior".equals(level)) {
            showSeniorVacancies(update);
        }
    }

    private void showVacancyDescription (String id, Update update) throws TelegramApiException {
      SendMessage sendMessage = new SendMessage();
      sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
      Vacancydto vacancy = vacancyService.get(id);
      String description = vacancy.getShortdescription();
      sendMessage.setText(description);
      sendMessage.setReplyMarkup(getBackToVacanciesMenu());
      execute(sendMessage);
    }

private ReplyKeyboard getBackToVacanciesMenu () {
    List<InlineKeyboardButton> row = new ArrayList<>();
    InlineKeyboardButton backToVacanciesButton = new InlineKeyboardButton();
    backToVacanciesButton.setText("Back to vacancies");
    backToVacanciesButton.setCallbackData("backToVacancies");
    row.add(backToVacanciesButton);


    InlineKeyboardButton backToStartMenuButton= new InlineKeyboardButton();
    backToStartMenuButton.setText("Back to start menu");
    backToStartMenuButton.setCallbackData("backToStartMenu");
    row.add(backToStartMenuButton);

    return new InlineKeyboardMarkup(List.of(row));
}

    private void showJuniorVacancies(Update update) throws TelegramApiException {
     SendMessage sendMessage = new SendMessage();
     sendMessage.setText("Please choose teacher:");
     Long chatId = update.getCallbackQuery().getMessage().getChatId();
     sendMessage.setChatId(chatId);
     sendMessage.setReplyMarkup(getJuniorVacanciesMenu());

     lastShownVacancyLevel.put(chatId, "junior");

     execute(sendMessage);
    }
    private ReplyKeyboard getJuniorVacanciesMenu() {
        List<InlineKeyboardButton>row = new ArrayList<>();
        List<Vacancydto> vacancies = vacancyService.getJuniorVacancies();
        for(Vacancydto vacancy: vacancies) {
            InlineKeyboardButton vacancyButton = new InlineKeyboardButton();
            vacancyButton.setText(vacancy.getTitle());
            vacancyButton.setCallbackData("vacancyId=" + vacancy.getId());
            row.add(vacancyButton);
        }


        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));

        return keyboard;
    }
    private void showMiddleVacancies(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please choose teacher:");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getMiddleVacanciesMenu());
        execute(sendMessage);
        lastShownVacancyLevel.put(chatId, "middle");
    }
    private ReplyKeyboard getMiddleVacanciesMenu() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton maVacancy = new InlineKeyboardButton();
        maVacancy.setText("Middle Java developer at Amazon");
        maVacancy.setCallbackData("vacancy id = 3");
        row.add(maVacancy);

        InlineKeyboardButton GoogleVacancy = new InlineKeyboardButton();
        GoogleVacancy.setText("Middle Java developer at Global Logic");
        GoogleVacancy.setCallbackData("vacancy id = 4");
        row.add(GoogleVacancy);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));

        return keyboard;
    }
    private void showSeniorVacancies(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please choose vacancy:");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getSeniorVacanciesMenu());

        execute(sendMessage);
        lastShownVacancyLevel.put(chatId, "senior");
    }
    private ReplyKeyboard getSeniorVacanciesMenu() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton maVacancy = new InlineKeyboardButton();
        maVacancy.setText("Senior Java developer at Valve");
        maVacancy.setCallbackData("vacancy id = 5");
        row.add(maVacancy);

        InlineKeyboardButton GoogleVacancy = new InlineKeyboardButton();
        GoogleVacancy.setText("Senior Java developer at Work.ua");
        GoogleVacancy.setCallbackData("vacancy id = 6");
        row.add(GoogleVacancy);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));

        return keyboard;
    }



    private void handleStartCommand (Update update) throws RuntimeException, TelegramApiException {
    String text = update.getMessage().getText();
    System.out.println("Received text " + text);
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(update.getMessage().getChatId());
    sendMessage.setText("Welcome to bot that shows you a list of teachers. Please choose teacher that you need: " );
    sendMessage.setReplyMarkup(getStartMenu());


    try {
        execute(sendMessage);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }
    private ReplyKeyboard getStartMenu() {
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton firstYear = new InlineKeyboardButton();
        firstYear.setText("First year teacher");
        firstYear.setCallbackData("showFirstYearTeacher");
        row.add(firstYear);


        InlineKeyboardButton secondYear = new InlineKeyboardButton();
        secondYear.setText("Second year teacher");
        secondYear.setCallbackData("showSecondYearTeacher");
        row.add(secondYear);


        InlineKeyboardButton thirdYear = new InlineKeyboardButton();
        thirdYear.setText("Third year teacher");
        thirdYear.setCallbackData("showThirdYearTeacher");
        row.add(thirdYear);




        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));

        return keyboard;
    }

    @Override
    public String getBotUsername() {
        return "Vacancies Bot";
    }
}
