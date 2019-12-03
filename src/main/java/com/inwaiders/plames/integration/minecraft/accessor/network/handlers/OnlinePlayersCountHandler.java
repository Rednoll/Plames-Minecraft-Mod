package com.inwaiders.plames.integration.minecraft.accessor.network.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.JsonObject;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.network.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class OnlinePlayersCountHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		OutputStream os = exchange.getResponseBody();
		DataOutputStream dos = new DataOutputStream(os);
		
		String rawData = HttpUtils.readString(exchange.getRequestBody());
		
		JsonObject data = (JsonObject) ReCraftAccessor.jsonParser.parse(rawData.toString());
		
		if(!data.has("secret") || !data.get("secret").getAsString().equals(ReCraftAccessor.properties.get("secret"))) {
			
			exchange.sendResponseHeaders(403, -1);
			return;
		}
		
		MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		int onlineCount = minecraftServer.getPlayerList().getOnlinePlayerNames().length;

		exchange.sendResponseHeaders(200, 4);
		dos.writeInt(onlineCount);
		os.flush();
		os.close();
		return;
	}
}