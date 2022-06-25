package com.baisha.util;

import com.baisha.handle.TelegramMessageHandler;
import com.baisha.handle.TelegramMyChatMemberHandler;
import com.baisha.service.TgChatService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelegramBotUtil {

	public static String casinoWebDomain;
	public static String getCasinoWebDomain() {
		return casinoWebDomain;
	}

	public static TelegramMyChatMemberHandler telegramMyChatMemberHandler;
	public static TelegramMyChatMemberHandler getTelegramMyChatMemberHandler() {
		return telegramMyChatMemberHandler;
	}

	public static TelegramMessageHandler telegramMessageHandler;
	public static TelegramMessageHandler getTelegramMessageHandler() {
		return telegramMessageHandler;
	}

	public static TgChatService tgChatService;
	public static TgChatService getTgChatService() {
		return tgChatService;
	}
}
