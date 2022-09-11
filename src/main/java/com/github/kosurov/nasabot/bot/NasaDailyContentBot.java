package com.github.kosurov.nasabot.bot;

import com.github.kosurov.nasabot.models.NasaResponse;
import com.github.kosurov.nasabot.models.YandexResponse;
import com.github.kosurov.nasabot.services.NasaResponseService;
import com.github.kosurov.nasabot.services.TranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class NasaDailyContentBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    private final NasaResponseService nasaResponseService;
    private final TranslateService translateService;

    @Autowired
    public NasaDailyContentBot(NasaResponseService nasaResponseService, TranslateService translateService) {
        this.nasaResponseService = nasaResponseService;
        this.translateService = translateService;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            String chatId = update.getMessage().getChatId().toString();

            if (message.equals("Получить NASA контент")) {
                sendNASAMessage(chatId);
            } else {
                sendHelloMessage(chatId);
            }
        } else if (update.hasCallbackQuery()) {
            String text = update.getCallbackQuery().getData();
            if (text.equals("GetNASARequest")) {
                sendNASAMessage(update.getCallbackQuery().getMessage().getChatId().toString());
            } else if (text.equals("Translate")) {
                sendTranslateMessage(update.getCallbackQuery().getMessage().getChatId().toString());
            }
        }
    }

    public synchronized void setInline(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Да, пришли, плиз!");
        inlineKeyboardButton.setCallbackData("GetNASARequest");

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(inlineKeyboardButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    public synchronized void setInlineForNasaCaption(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Перевести?");
        inlineKeyboardButton.setCallbackData("Translate");

        List<InlineKeyboardButton> keyboardButtonsFirstRow = new ArrayList<>();
        keyboardButtonsFirstRow.add(inlineKeyboardButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsFirstRow);

        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    public synchronized void sendHelloMessage(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Привет! Я могу прислать тебе файл с сайта NASA.");
        setInline(sendMessage);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendNASAMessage(String chatId) {
        NasaResponse nasaResponse = nasaResponseService.nasaRequest();
        if ("video".equals(nasaResponse.getMediaType())) {
            SendMessage sendTextMessage = new SendMessage();
            sendTextMessage.setChatId(chatId);
            String text = nasaResponse.getUrl() + "\n" + nasaResponse.getTitle();
            sendTextMessage.setText(text);

            SendMessage sendCaptionMessage = new SendMessage();
            sendCaptionMessage.setChatId(chatId);
            sendCaptionMessage.setText(nasaResponse.getExplanation());
            setInlineForNasaCaption(sendCaptionMessage);
            try {
                execute(sendTextMessage);
                execute(sendCaptionMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            InputFile photo = new InputFile().setMedia(nasaResponse.getUrl());
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(photo);
            sendPhoto.setCaption(nasaResponse.getTitle());
            SendMessage sendCaptionMessage = new SendMessage();
            sendCaptionMessage.setChatId(chatId);
            sendCaptionMessage.setText(nasaResponse.getExplanation());
            setInlineForNasaCaption(sendCaptionMessage);
            try {
                execute(sendPhoto);
                execute(sendCaptionMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void sendTranslateMessage(String chatId) {
        NasaResponse nasaResponse = nasaResponseService.nasaRequest();
        YandexResponse translateResponse = translateService.Translate(nasaResponse.getExplanation());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String text = translateResponse.getTranslations().get(0).getText() +
                "\nПереведено сервисом \"Яндекс.Переводчик\" http://translate.yandex.ru";
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
