package com.inwaiders.plames.integration.minecraft.accessor.network.handlers;

import java.io.IOException;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inwaiders.plames.integration.minecraft.accessor.MarketDataUtils;
import com.inwaiders.plames.integration.minecraft.accessor.PlayerUtils;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.network.HttpUtils;
import com.mojang.authlib.GameProfile;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import scala.tools.nsc.doc.model.diagram.ObjectNode;

public class CollectCartHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		try {
			
			String rawData = HttpUtils.readString(exchange.getRequestBody());
			
			JsonObject data = (JsonObject) ReCraftAccessor.JSON_PARSER.parse(rawData);

			if(!data.has("secret") || !data.get("secret").getAsString().equals(ReCraftAccessor.PROPERTIES.get("secret"))) {
				
				exchange.sendResponseHeaders(403, -1);
				return;
			}
			
			String playerName = data.get("player_name").getAsString();
			UUID playerUUID = UUID.fromString(data.get("player_uuid").getAsString());
			
			EntityPlayer player = null;
			
				MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
				
				c2 : for(World world : minecraftServer.worlds) {
					
					for(EntityPlayer ep : world.playerEntities) {
						
						GameProfile profile = ep.getGameProfile();
					
						if(ReCraftAccessor.PROPERTIES.get("player-find-strategy").equals("uuid")) {
							
							if(!profile.getId().equals(playerUUID)) {
						
								continue;
							}
						}
						else if(ReCraftAccessor.PROPERTIES.get("player-find-strategy").equals("name")) {
							
							if(!profile.getName().equals(playerName)) {
								
								continue;
							}
						}
					
						player = ep;
						break c2;
					}
				}
			
			if(player == null) {
				
				exchange.sendResponseHeaders(404, -1);
				return;
			}
			
			JsonArray response = new JsonArray();

			JsonArray jsonItemStacks = ((JsonObject) data.get("cart")).get("item_stacks").getAsJsonArray();
//			MarketCartInventory cartInventory = new MarketCartInventory();

				for(JsonElement element : jsonItemStacks) {
					
					JsonObject jsonItemStack = element.getAsJsonObject();
					
					ItemStack is = MarketDataUtils.fromMarketItemStack(jsonItemStack);
					
//					cartInventory.spreadStack(is);
					
						int available = PlayerUtils.getAvailableSpaceForItem(player, is);
					
						if(is.getCount() > available) {
							
							is.setCount(available);
						}

					JsonObject responseItemStack = new JsonObject();
						responseItemStack.addProperty("id", jsonItemStack.get("id").getAsLong());
						responseItemStack.addProperty("quantity", is.getCount());
				
					response.add(responseItemStack);
					
					player.addItemStackToInventory(is);		
					 
				}
	
//			player.displayGUIChest(cartInventory);
			
			byte[] responseByteArray = response.toString().getBytes();
				
			exchange.sendResponseHeaders(200, responseByteArray.length);
			exchange.getResponseBody().write(responseByteArray);
			return;
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
			
		exchange.sendResponseHeaders(500, -1);
		return;
	}
}
