package com.baisha.util;

import com.baisha.handle.TelegramMessageHandler;
import com.baisha.handle.TelegramMyChatMemberHandler;
import com.baisha.service.TgChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

@Slf4j
public class TelegramBotUtil {


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

}
