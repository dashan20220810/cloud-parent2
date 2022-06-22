//package com.baisha.bot;
//
//import com.baisha.handle.TelegramMessageHandler;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
//import org.telegram.telegrambots.meta.api.methods.ParseMode;
//import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
//import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
//import org.telegram.telegrambots.meta.api.objects.InputFile;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//@Slf4j
//public class MyTelegramLongPollingBot1 extends TelegramLongPollingBot {
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage()) {
//            // 消息处理
//            new TelegramMessageHandler().messageHandler(this,update);
//        } else if (update.hasCallbackQuery()) {
//            String text = null;
//            // 主体消息
//            CallbackQuery callbackQuery = update.getCallbackQuery();
//            // 消息
//            Message message = callbackQuery.getMessage();
//            // 代理name
////            String bucketName = TelegramUtil.getStringResourceByKey("BucketName").trim();
//            // 会员id
//            Long id = callbackQuery.getFrom().getId();
//            // TG群id
//            String chatId = message.getChatId().toString();
//            // 按钮名字
//            String data = callbackQuery.getData();
//            switch (data) {
//                case "查询余额":
////                    text = checkMemberBalance(bucketName, id, chatId);
//                    break;
//                default:
//                    break;
//            }
//
//            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
//            answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
//            answerCallbackQuery.setShowAlert(true);
//            answerCallbackQuery.setCacheTime(1);
//            answerCallbackQuery.setText(text);
//            // answerCallbackQuery.setUrl("https://telegram.me/pengrad_test_bot?game=pengrad_test_game");
//            try {
//                execute(answerCallbackQuery);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
