package com.inwaiders.plames.integration.minecraft.accessor.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.inwaiders.plames.integration.minecraft.accessor.chat.CommandStub;
import com.inwaiders.plames.integration.minecraft.accessor.commands.handlers.CommandHandler;

public class CommandHandlerRegistry {

	private static Map<String, Set<CommandHandler>> map = new HashMap<>();
	
	public static void register(String name, CommandHandler handler) {
		
		Set<CommandHandler> handlers = map.get(name);
	
		if(handlers == null) {
			
			handlers = new HashSet<>();
			map.put(name, handlers);
		}
		
		handlers.add(handler);
	}
	
	public static Set<CommandHandler> getCommandHandlers(String name) {
		
		Set<CommandHandler> handlers = map.get(name);
		
		if(handlers == null) {
			
			handlers = Collections.EMPTY_SET;
		}
		
		return handlers;
	}
}
