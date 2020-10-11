package org.studyproject.rezkaBot.bot;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.studyproject.rezkaBot.controller.ParsingController;
import org.studyproject.rezkaBot.services.ParsingService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.studyproject.rezkaBot.constants.Constants.HELLO_MESSAGE;

@Component
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.url}")
    private String rezkaUrl;
    ParsingController parsingController;
    ParsingService parsingService;

    @Autowired
    public Bot(ParsingController parsingController, ParsingService parsingService) {
        this.parsingController = parsingController;
        this.parsingService = parsingService;
    }


    List<String> serviceMessages = new ArrayList<String>();

    {
        serviceMessages.add("/start");
        serviceMessages.add("/help");
    }


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        String message = update.getMessage().getText();
        try {
            String answer = "";
            if (serviceMessages.contains(message)) {
                serviceAnswer(update, message);
            } else {
                ordinarySearch(update, message);
            }

        } catch (TelegramApiException telegramApiException) {
            telegramApiException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void serviceAnswer(Update update, String message) throws TelegramApiException {
        String answer = "";
        switch (message) {
            case "/start":
                answer = HELLO_MESSAGE;
            case "/help":
                answer = HELLO_MESSAGE;
        }
        execute(new SendMessage().setChatId(update.getMessage().getChatId())
                .setText(answer));
    }

    private void ordinarySearch(Update update, String message) throws TelegramApiException, IOException {
        execute(new SendMessage().setChatId(update.getMessage().getChatId())
                .setText(parsingController
                        .search(message)
                        .stream()
                        .collect(Collectors.joining("\n"))));
    }

}
