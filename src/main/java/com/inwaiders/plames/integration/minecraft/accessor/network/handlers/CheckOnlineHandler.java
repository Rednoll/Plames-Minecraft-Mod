package com.inwaiders.plames.integration.minecraft.accessor.network.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.network.HttpUtils;
import com.inwaiders.plames.integration.minecraft.stress.AccessorStress;
import com.inwaiders.plames.integration.minecraft.stress.AccessorStress.EmulatedProfile;
import com.mojang.authlib.GameProfile;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CheckOnlineHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		OutputStream os = exchange.getResponseBody();
		
		String rawData = HttpUtils.readString(exchange.getRequestBody());
		
		JsonObject data = (JsonObject) ReCraftAccessor.jsonParser.parse(rawData.toString());
		
		if(!data.has("secret") || !data.get("secret").getAsString().equals(ReCraftAccessor.properties.get("secret"))) {
			
			exchange.sendResponseHeaders(403, -1);
			return;
		}
		
		String playerName = data.get("player_name").getAsString();
		UUID playerUUID = UUID.fromString(data.get("player_uuid").getAsString());
	
		for(EmulatedProfile profile : AccessorStress.profiles) {
			
			if(profile.username.equals(playerName)) {
				
				exchange.sendResponseHeaders(200, 1);
				os.write(1);
				os.flush();
				os.close();
				return;
			}
		}
		
		MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		for(World world : minecraftServer.worlds) {
			
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
				
				exchange.sendResponseHeaders(200, 1);
				os.write(1);
				os.flush();
				os.close();
				return;
			}
		}

		exchange.sendResponseHeaders(200, 1);
		os.write(0);
		os.flush();
		os.close();
		return;
	}
}
