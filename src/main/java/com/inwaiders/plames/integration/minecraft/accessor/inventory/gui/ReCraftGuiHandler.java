package com.inwaiders.plames.integration.minecraft.accessor.inventory.gui;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.inwaiders.plames.integration.minecraft.accessor.commands.handlers.MarketCartCommandHandler;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.MarketCartInventory;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.container.MarketCartContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ReCraftGuiHandler implements IGuiHandler {

	public static final int MARKET_CART = 0;
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		
		if(id == MARKET_CART) {
			
			return new MarketCartContainer(MarketCartCommandHandler.cartInventories.get(player), player);
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
		
		return null;
	}
}
