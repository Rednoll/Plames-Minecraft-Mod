package com.inwaiders.plames.integration.minecraft.stress;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandStressProfilesCount extends CommandBase {
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		AccessorStress.profilesCount = Integer.valueOf(args[0]);
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		
		return "";
	}
	
	@Override
	public String getName() {
		
		return "stress_profiles";
	}
}
