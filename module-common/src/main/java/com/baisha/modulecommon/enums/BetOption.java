package com.baisha.modulecommon.enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.StringUtils;

public enum BetOption {

	ZD(1, "庄对", new String[] {"ZD", "庄对"}),
	XD(2, "闲对", new String[] {"XD", "闲对"}),
	Z(3, "庄", new String[] {"Z", "庄"}),
	X(4, "闲", new String[] {"X", "闲"}),
	H(5, "和", new String[] {"H", "和", "和局"}),
	D(6, "对", new String[] {"D", "对", "对子"}),
	SS(7, "幸运六", new String[] {"SS", "超六", "幸运六", "超6", "幸运6"}),
	SB(8, "三宝", new String[] {"SB", "三宝", "3宝"})
	;

	private final int order;
	private final String display;
	private final Set<String> commands;

	private static final List<BetOption> list;
	static {
		list = Arrays.stream(values())
				.sorted(Comparator.comparingInt(BetOption::getOrder))
        		.collect(Collectors.toList());
	}
	
	public static List<BetOption> getList() {
		return list;
	}
	
	public static BetOption retrieve ( String val ) throws Exception {
		Optional<BetOption> opt = Arrays.stream(values()).filter( option -> StringUtils.equals(val, option.toString())).findAny();
		if ( opt.isPresent() ) {
			return opt.get();
		}
		
		throw new Exception (String.format(" error, wrong val: %s", val));
	}
	
	public static BetOption getBetOption(String command) {
		Optional<BetOption> bet = getList()
									.stream()
									.filter(betOption -> betOption.getCommands().contains(command)).findFirst();
		return bet.orElse(null);
	}
	
	BetOption(int order, String display, String[] commandsArray) {
		this.order = order;
		this.display = display;
		this.commands = new HashSet<>(Arrays.asList(commandsArray));
	}
	
	public int getOrder() {
		return order;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public Set<String> getCommands() {
		return commands;
	}
}
