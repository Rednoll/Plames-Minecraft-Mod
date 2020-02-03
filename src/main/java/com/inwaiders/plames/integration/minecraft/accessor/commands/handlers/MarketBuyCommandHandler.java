package com.inwaiders.plames.integration.minecraft.accessor.commands.handlers;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inwaiders.plames.integration.minecraft.accessor.MarketDataUtils;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.MarketCartInventory;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.gui.ReCraftGuiHandler;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.ReCraftHttpConnector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class MarketBuyCommandHandler implements CommandHandler {
	
	@Override
	public boolean handle(EntityPlayer player, String[] args) {
	
		if(!(args.length == 2 && args[0].equals("/buy") && args[1].equals("gui"))) return false;

		player.openGui(ReCraftAccessor.instance, ReCraftGuiHandler.MARKET_BUY, player.getEntityWorld(), -1, -1, -1);

		return true;
	}
}