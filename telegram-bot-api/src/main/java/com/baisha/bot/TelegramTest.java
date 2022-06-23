package com.baisha.bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramTest {
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        botsApi.registerBot(new MyTelegramLongPollingBot("tg_bet_neiwang_bot","5529214590:AAFMmnzsEWgE548-x8A4T4rS5aS89b0rzD4"));
//        botsApi.registerBot(new MyTelegramLongPollingBot());
//        botsApi.registerBot(new MyTelegramWebhookBot(),new SetWebhook());
    }


}
