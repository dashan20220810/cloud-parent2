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
//@NoArgsConstructor
//@Component
//public class MyTelegramLongPollingBot1 extends TelegramLongPollingBot {
////    @Value("${group.chatId}")
//    private String groupChatId = "-1001323106221";
//
////    @Autowired
////    private TelegramMessageHandler messageHandler;
//
//    private String username;
//    private String token;
//
//    public static MyTelegramLongPollingBot1 telegramInstance = new MyTelegramLongPollingBot1();
//
//    public MyTelegramLongPollingBot1(String username, String token) {
//        this.username=username;
//        this.token=token;
//    }
//    @Override
//    public String getBotUsername() {
//        return username;
//    }
//
//    @Override
//    public String getBotToken() {
//        return token;
//    }
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
//
//        if (update.hasCallbackQuery()) {
//            log.info("===============hasCallbackQuery===================");
//        }
//        if (update.hasChannelPost()) {
//            log.info("===============hasChannelPost===================");
//        }
//        if (update.hasChatMember()) {
//            log.info("===============hasChatMember===================");
//        }
//        if (update.hasPoll()) {
//            log.info("===============hasPoll===================");
//        }
//        if (update.hasChatJoinRequest()) {
//            log.info("===============hasChatJoinRequest===================");
//
//        }
//    }
//
//    /**
//     * 发送文字
//     *
//     * @param msg
//     */
//    public void sendMessage(String msg) {
//        SendMessage sm = new SendMessage();
//        sm.setChatId(groupChatId);
//        sm.setAllowSendingWithoutReply(true);
//        sm.setText(msg);
//        try {
//            execute(sm);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     *
//     * @param sm
//     */
//    public void SendMessage(SendMessage sm) {
//        try {
//            execute(sm);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 傳送 html
//     *
//     * @param msg
//     */
//    public void SendMessageHtml(String msg) {
//        SendMessage sm = new SendMessage();
//        sm.setChatId(groupChatId);
//        sm.setParseMode(ParseMode.HTML);
//        sm.setText(msg);
//        try {
//            execute(sm);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void SendMessageHtml(SendMessage sm) {
////		SendMessage sm = new SendMessage();
////		sm.setChatId(TelegramUtil.getStringResourceByKey("chat_id").trim());
////		sm.setParseMode(ParseMode.HTML);
////		sm.setText(msg);
//        try {
//            execute(sm);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 傳送照片
//     *
//     * @param file
//     */
//    public void SendPhoto(InputFile file) {
//        SendPhoto sp = new SendPhoto();
//        sp.setChatId(groupChatId);
//        sp.setPhoto(file);
//        try {
//            execute(sp);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void SendPhoto(SendPhoto sp) {
////		SendPhoto sp = new SendPhoto();
////		sp.setChatId(TelegramUtil.getStringResourceByKey("chat_id").trim());
////		sp.setPhoto(file);
//        try {
//            execute(sp);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 傳送影片
//     *
//     * @param file
//     */
//    public void SendAnimation(InputFile file) {
//        SendAnimation sa = new SendAnimation();
//        sa.setChatId(groupChatId);
//        sa.setAnimation(file);
//
//        try {
//            execute(sa);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void SendAnimation(SendAnimation sa) {
////		SendAnimation sa = new SendAnimation();
////		sa.setChatId(TelegramUtil.getStringResourceByKey("chat_id").trim());
////		sa.setAnimation(file);
//        try {
//            execute(sa);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//}
