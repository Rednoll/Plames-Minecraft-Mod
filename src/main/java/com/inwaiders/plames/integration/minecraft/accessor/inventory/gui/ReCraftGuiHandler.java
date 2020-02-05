package com.inwaiders.plames.integration.minecraft.accessor.inventory.gui;

import com.inwaiders.plames.integration.minecraft.accessor.commands.handlers.MarketBuyCommandHandler;
import com.inwaiders.plames.integration.minecraft.accessor.commands.handlers.MarketCartCommandHandler;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.MarketBuyInventory;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.MarketCartInventory;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.container.MarketBuyContainer;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.container.MarketCartContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ReCraftGuiHandler implements IGuiHandler {

	public static final int MARKET_CART = 0;
	
	public static final int MARKET_BUY = 1;
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		
		if(id == MARKET_CART) {
			
			return new MarketCartContainer(MarketCartCommandHandler.cartInventories.get(player), player);
		}
		
		else if(id == MARKET_BUY) {
			
			MarketBuyInventory inv = new MarketBuyInventory(player, 36);
			
			MarketBuyCommandHandler.buyInventories.put(player, inv);
			
			return new MarketBuyContainer(inv, player);
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if(id == MARKET_CART) {
			
			MarketCartInventory inv = new MarketCartInventory(player);
			
			MarketCartCommandHandler.cartInventory = inv;
			
			return new MarketCartGui(inv);
		}
		
		else if(id == MARKET_BUY) {
			
			MarketBuyInventory inv = new MarketBuyInventory(player, 36);
			
			MarketBuyCommandHandler.clientInventory = inv;
			
			return new MarketBuyGui(inv);
		}
		
		return null;
	}
}
