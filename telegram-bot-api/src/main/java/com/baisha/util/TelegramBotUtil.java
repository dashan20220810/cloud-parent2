package com.baisha.util;

import com.baisha.handle.TelegramMyChatMemberHandler;
import com.baisha.repository.TgChatRepository;
import com.baisha.service.TgChatService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelegramBotUtil {

	public static String casinoWebDomain;

	public static TelegramMyChatMemberHandler telegramMyChatMemberHandler;

	public static TgChatService tgChatService;

	public static String getCasinoWebDomain() {
		return casinoWebDomain;
	}

	public static TelegramMyChatMemberHandler getTelegramMyChatMemberHandler() {
		return telegramMyChatMemberHandler;
	}

	public static TgChatService getTgChatService() {
		return tgChatService;
	}
}
