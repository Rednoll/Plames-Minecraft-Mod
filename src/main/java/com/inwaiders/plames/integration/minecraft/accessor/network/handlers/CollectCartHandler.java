package com.inwaiders.plames.integration.minecraft.accessor.network.handlers;

import java.io.IOException;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inwaiders.plames.integration.minecraft.accessor.PlayerUtils;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.network.HttpUtils;
import com.mojang.authlib.GameProfile;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CollectCartHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		try {
			
			String rawData = HttpUtils.readString(exchange.getRequestBody());
			
			JsonObject data = (JsonObject) ReCraftAccessor.jsonParser.parse(rawData);

			if(!data.has("secret") || !data.get("secret").getAsString().equals(ReCraftAccessor.properties.get("secret"))) {
				
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
					
						if(ReCraftAccessor.properties.get("player-find-strategy").equals("uuid")) {
							
							if(!profile.getId().equals(playerUUID)) {
						
								continue;
							}
						}
						else if(ReCraftAccessor.properties.get("player-find-strategy").equals("name")) {
							
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
			

			JsonArray jsonItemStacks = data.get("item_stacks").getAsJsonArray();
			
				for(JsonElement element : jsonItemStacks) {
					
					JsonObject jsonItemStack = element.getAsJsonObject();
				
					int quantity = jsonItemStack.get("quantity").getAsInt();
		
					JsonObject jsonItem = jsonItemStack.get("item").getAsJsonObject();
					
					Item item = null;
					
						if(jsonItem.has("id")) {
							
							int itemId = jsonItem.get("id").getAsInt();
						
							item = Item.getItemById(itemId);
						
							if(item == null) {
								
								item = Item.getItemFromBlock(Block.getBlockById(itemId));
							}
						}
						else if(jsonItem.has("name")) {
							
							String itemName = jsonItem.get("name").getAsString();
							
							item = Item.getByNameOrId(itemName);
							
							if(item == null) {
								
								item = Item.getItemFromBlock(Block.getBlockFromName(itemName));
							}
						}
						
					ItemStack is = new ItemStack(item);
					
						int available = PlayerUtils.getAvailableSpaceForItem(player, is);
					
						if(quantity > available) {
							
							quantity = available;
						}
						
						is.setCount(quantity);
					
					player.addItemStackToInventory(is);
					
					JsonObject responseItemStack = new JsonObject();
						responseItemStack.addProperty("id", jsonItemStack.get("id").getAsLong());
						responseItemStack.addProperty("quantity", quantity);
				
					response.add(responseItemStack);
				}
		
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
