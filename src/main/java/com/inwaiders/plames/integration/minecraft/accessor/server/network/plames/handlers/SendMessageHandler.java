package com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.handlers;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.network.plames.HttpUtils;
import com.inwaiders.plames.integration.minecraft.stress.AccessorStress;
import com.inwaiders.plames.integration.minecraft.stress.AccessorStress.EmulatedProfile;
import com.mojang.authlib.GameProfile;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SendMessageHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		String rawData = HttpUtils.readString(exchange.getRequestBody());
		
		JsonObject data = (JsonObject) ReCraftAccessor.JSON_PARSER.parse(rawData.toString());
	
		if(!data.has("text") || !data.has("player_name") || !data.has("player_uuid")) {
			
			exchange.sendResponseHeaders(400, -1);
			return;
		}
		
		if(!data.has("secret") || !data.get("secret").getAsString().equals(ReCraftAccessor.PROPERTIES.get("secret"))) {
			
			exchange.sendResponseHeaders(403, -1);
			return;
		}
		
		String text = URLDecoder.decode(data.get("text").getAsString(), "UTF-8");
		String playerName = data.get("player_name").getAsString();
		UUID playerUUID = UUID.fromString(data.get("player_uuid").getAsString());
	
		// *** STRESS TEST ***
		for(EmulatedProfile profile : AccessorStress.profiles) {
			
			if(profile.username.equals(playerName)) {
				
				exchange.sendResponseHeaders(200, -1);
				return;
			}
		}
		// *** STRESS TEST ***
		
		MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		for(World world : minecraftServer.worlds) {
			
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
				
				ep.sendMessage(new TextComponentString(text));
				exchange.sendResponseHeaders(200, -1);
				return;
			}
		}
	}
}
