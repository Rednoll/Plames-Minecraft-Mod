package com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.JsonObject;
import com.inwaiders.plames.integration.minecraft.accessor.MarketDataUtils;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.network.plames.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemsHandler implements HttpHandler{

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
		
		if(data.has("item_hash")) {
			
			String hash = data.get("item_hash").getAsString();
			
			Item item = ReCraftAccessor.HASH_ITEM_MAP.get(hash);
			
			JsonObject jsonItem = MarketDataUtils.getMarketItem(new ItemStack(item));
		
			byte[] responseData = jsonItem.toString().getBytes();
			
			exchange.sendResponseHeaders(200, responseData.length);
			dos.write(responseData);
			dos.flush();
			dos.close();
			return;
		}
	}
}
