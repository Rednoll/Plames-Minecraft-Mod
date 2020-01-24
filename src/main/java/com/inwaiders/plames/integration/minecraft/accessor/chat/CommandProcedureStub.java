package com.inwaiders.plames.integration.minecraft.accessor.chat;

import java.util.UUID;

import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.ReCraftHttpConnector;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandProcedureStub extends CommandBase {

	private String name = null;
	
	public CommandProcedureStub(String name) {
	
		this.name = name;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		Entity entity = sender.getCommandSenderEntity();
	
		if(entity instanceof EntityPlayer) {
			
			EntityPlayer player = (EntityPlayer) entity;
			
			UUID uuid = player.getGameProfile().getId();
			String playerName = player.getGameProfile().getName();
			String message = String.join(" ", args).trim();

			ReCraftAccessor.EXECUTOR_SERVICE.submit(()-> {
				
				boolean result = ReCraftHttpConnector.sendToMessengerServer(uuid, playerName, message);
				
				if(!result) {
					
					sender.sendMessage(new TextComponentString("Error sending command to ReCraft server!"));
				}
			});
		}
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
