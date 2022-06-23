package com.baisha.modulecommon.enums;

import java.util.*;
import java.util.stream.Collectors;

public enum BetOption {

	ZD(1, "庄对", new String[] {"ZD", "庄对"}),
	XD(2, "闲对", new String[] {"XD", "闲对"}),
	Z(3, "庄", new String[] {"Z", "庄"}),
	X(4, "闲", new String[] {"X", "闲"}),
	H(5, "和", new String[] {"H", "和", "和局"}),
	D(6, "对", new String[] {"D", "对", "对子"}),
	SS(7, "幸运六", new String[] {"SS", "超六", "幸运六", "幸運六", "超6", "幸运6"}),
	SB(8, "三宝", new String[] {"SB", "三宝", "3宝"});

	private final int order;
	private final String display;
	private final Set<String> alias;

	private static final List<BetOption> list;
	static {
		list = Arrays.stream(values())
				.sorted(Comparator.comparingInt(BetOption::getOrder))
        		.collect(Collectors.toList());
	}
	public static List<BetOption> getList() {
		return list;
	}
	
	public static BetOption retrieveFromAlias (String alias) throws Exception {
		
		Optional<BetOption> bet = Arrays.stream(values())
				.filter(betOption -> betOption.getAlias().contains(alias)).findAny();
		
		if ( bet.isPresent() ) {
			return bet.get();
		} else {
			throw new Exception("alias不存在: " +alias);
		}
	}
//	public static String getCommand(String command) {
//		for (BetOption item : getList()) {
//			if (item.alias.equals(command)) {
//				return item.value;
//			}
//		}
//		return null;
//	}

	public static void main(String[] args) {
		String command = "Z1000";
		getList().forEach(betOption -> {

		});

	}
	
	BetOption(int order, String display, String[] aliasArray) {
		this.order = order;
		this.display = display;
		this.alias = new HashSet<>(Arrays.asList(aliasArray));
	}
	
	public int getOrder() {
		return order;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public Set<String> getAlias() {
		return alias;
	}
}
