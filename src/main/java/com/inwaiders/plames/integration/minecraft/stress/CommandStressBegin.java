package com.inwaiders.plames.integration.minecraft.stress;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandStressBegin extends CommandBase {
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		if(!AccessorStress.profilesRegistered) {
			
			AccessorStress.generateProfiles();
			AccessorStress.registerProfiles();
		}
		
		AccessorStress.beginStress();
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		
		return "";
	}
	
	@Override
	public String getName() {
		
		return "stress";
	}
}
