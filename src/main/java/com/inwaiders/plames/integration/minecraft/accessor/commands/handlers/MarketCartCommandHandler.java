package com.inwaiders.plames.integration.minecraft.accessor.commands.handlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MarketCartCommandHandler implements CommandHandler {
	
	public static volatile Map<EntityPlayer, MarketCartInventory> cartInventories = new HashMap<EntityPlayer, MarketCartInventory>();
	
	@Override
	public boolean handle(EntityPlayer player, String[] args) {
	
		if(args.length == 2 && args[0].equals("/cart") && args[1].equals("gui")) {
			
			JsonObject data = new JsonObject();
				data.addProperty("server", Long.valueOf((String) ReCraftAccessor.PROPERTIES.get("server-id")));
				data.addProperty("secret", (String) ReCraftAccessor.PROPERTIES.get("secret"));
				data.addProperty("player_name", player.getGameProfile().getName());
				data.addProperty("player_uuid", player.getGameProfile().getId().toString());
				
			HttpPost post = new HttpPost(ReCraftHttpConnector.getMethodUrl("api/mc/ajax/server/player_cart"));
				
				try {
					
					post.setEntity(new StringEntity(data.toString()));
				}
				catch(UnsupportedEncodingException e) {
				
					e.printStackTrace();
				}
				
				post.setHeader("Content-type", "application/json;charset=UTF-8");
				
			CloseableHttpClient httpClient = HttpClients.createDefault();
			
	    	try {
				
	    		CloseableHttpResponse response = httpClient.execute(post);

	    		HttpEntity entity = response.getEntity();
	    		
	    			String rawData = EntityUtils.toString(entity);
	    			
	    		EntityUtils.consume(entity);

	    		JsonObject cart = new JsonParser().parse(rawData).getAsJsonObject();
	    		
	    			JsonArray jsonItemStacks = cart.get("item_stacks").getAsJsonArray();;

	    			MarketCartInventory cartInventory = new MarketCartInventory(player);

					for(JsonElement element : jsonItemStacks) {
						
						JsonObject jsonItemStack = element.getAsJsonObject();
						
						ItemStack is = MarketDataUtils.fromMarketItemStack(jsonItemStack);
						
						cartInventory.spreadStack(is, jsonItemStack.get("id").getAsLong());
					}

				cartInventories.put(player, cartInventory);
				
				player.openGui(ReCraftAccessor.instance, ReCraftGuiHandler.MARKET_CART, player.getEntityWorld(), -1, -1, -1);
	    	}
			catch(Exception e) {
				
				e.printStackTrace();
			}
	    	
	    	return true;
		}
		
		return false;
	}
}
