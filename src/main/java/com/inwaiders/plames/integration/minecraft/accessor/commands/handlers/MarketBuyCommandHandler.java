package com.inwaiders.plames.integration.minecraft.accessor.commands.handlers;

import java.util.HashMap;
import java.util.Map;

import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.MarketBuyInventory;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.gui.ReCraftGuiHandler;

import net.minecraft.entity.player.EntityPlayer;

public class MarketBuyCommandHandler implements CommandHandler {
	
	public static volatile Map<EntityPlayer, MarketBuyInventory> buyInventories = new HashMap<EntityPlayer, MarketBuyInventory>();
	
	public static volatile MarketBuyInventory clientInventory = null;
	
	@Override
	public boolean handle(EntityPlayer player, String[] args) {
	
		if(!(args.length == 2 && args[0].equals("/buy") && args[1].equals("gui"))) return false;
		
		player.openGui(ReCraftAccessor.instance, ReCraftGuiHandler.MARKET_BUY, player.getEntityWorld(), -1, -1, -1);

		return true;
	}
}