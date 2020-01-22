package com.inwaiders.plames.integration.minecraft.accessor.chat;

import java.util.Set;
import java.util.UUID;

import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.commands.CommandHandlerRegistry;
import com.inwaiders.plames.integration.minecraft.accessor.commands.handlers.CommandHandler;
import com.inwaiders.plames.integration.minecraft.accessor.network.ReCraftHttpConnector;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandStub extends CommandBase {

	private String name = null;
	private Set<CommandHandler> handlers = null;
	
	public CommandStub(String name) {
	
		this.name = name;
		this.handlers = CommandHandlerRegistry.getCommandHandlers(this.name);
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		Entity entity = sender.getCommandSenderEntity();
	
		if(entity instanceof EntityPlayer) {
			
			EntityPlayer player = (EntityPlayer) entity;
			
			UUID uuid = player.getGameProfile().getId();
			String playerName = player.getGameProfile().getName();
			String message = ("/"+name+" "+String.join(" ", args)).trim();
			
			ReCraftAccessor.EXECUTOR_SERVICE.submit(()-> {
				
				boolean cancel = processHandlers(player, args);
				
				if(cancel) {
					
					return;
				}
				
				boolean result = ReCraftHttpConnector.sendToMessengerServer(uuid, playerName, message);
				
				if(!result) {
					
					sender.sendMessage(new TextComponentString("Error sending command to ReCraft server!"));
				}
			});
		}
	}
	
	public boolean processHandlers(EntityPlayer ep, String[] args) {
		
		for(CommandHandler handler : handlers) {
			
			if(handler.handle(ep, args)) {
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		
		return "";
	}
	
	@Override
	public String getName() {
		
		return name;
	}
}
