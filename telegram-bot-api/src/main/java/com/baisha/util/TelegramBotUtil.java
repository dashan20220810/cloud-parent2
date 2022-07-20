package com.baisha.util;

import com.baisha.handle.TelegramCallbackQueryHandler;
import com.baisha.handle.TelegramMessageHandler;
import com.baisha.handle.TelegramMyChatMemberHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Objects;

@Slf4j
public class TelegramBotUtil {

	public static String format(String str) {
		return String.format("%-" + str.length() + "s", str);
	}

	public static String base64Decode(String base64Str) throws UnsupportedEncodingException {
		byte[] base64Data = Base64.getDecoder().decode(base64Str);
		return new String(base64Data, "utf-8");
	}

	public static int getRandom(int min, int max) {
		return (int)(min + Math.random() * (max - min + 1));
	}

	public static Pageable setPageable(Integer pageCode, Integer pageSize, Sort sort) {
		if (Objects.isNull(sort)) {
			sort = Sort.unsorted();
		}

		if (pageSize == null || pageCode == null) {
			pageCode = 1;
			pageSize = 10;
		}

		if (pageCode < 1 || pageSize < 1) {
			pageCode = 1;
			pageSize = 10;
		}

		if (pageSize > 100) {
			pageSize = 100;
		}

		Pageable pageable = PageRequest.of(pageCode - 1, pageSize, sort);
		return pageable;
	}

	public static Pageable setPageable(Integer pageCode, Integer pageSize) {
		return setPageable(pageCode, pageSize, null);
	}

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

	public static TelegramCallbackQueryHandler telegramCallbackQueryHandler;
	public static TelegramCallbackQueryHandler getTelegramCallbackQueryHandler() {
		return telegramCallbackQueryHandler;
	}
}
