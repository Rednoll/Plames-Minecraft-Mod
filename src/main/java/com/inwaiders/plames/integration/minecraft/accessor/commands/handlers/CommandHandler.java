package com.inwaiders.plames.integration.minecraft.accessor.commands.handlers;

import net.minecraft.entity.player.EntityPlayer;

public interface CommandHandler {

	public boolean handle(EntityPlayer ep, String[] args);
}
