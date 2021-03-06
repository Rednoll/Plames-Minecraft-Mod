package com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.JsonObject;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.network.plames.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class AllPlayersCountHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		OutputStream os = exchange.getResponseBody();
		DataOutputStream dos = new DataOutputStream(os);
		
		String rawData = HttpUtils.readString(exchange.getRequestBody());
		
		JsonObject data = (JsonObject) ReCraftAccessor.JSON_PARSER.parse(rawData.toString());
		
		if(!data.has("secret") || !data.get("secret").getAsString().equals(ReCraftAccessor.PROPERTIES.get("secret"))) {
			
			exchange.sendResponseHeaders(403, -1);
			return;
		}
		
		MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		int playersCount = minecraftServer.getPlayerList().getAvailablePlayerDat().length;

		exchange.sendResponseHeaders(200, 4);
		dos.writeInt(playersCount);
		os.flush();
		os.close();
		return;
	}
}
