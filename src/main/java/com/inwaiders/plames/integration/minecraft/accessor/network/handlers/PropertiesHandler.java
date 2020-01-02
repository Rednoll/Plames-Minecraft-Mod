package com.inwaiders.plames.integration.minecraft.accessor.network.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.network.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.netty.handler.codec.http.HttpUtil;

public class PropertiesHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		String rawData = HttpUtils.readString(exchange.getRequestBody());
		
		JsonObject data = (JsonObject) ReCraftAccessor.JSON_PARSER.parse(rawData.toString());
	
		if(!data.has("secret") || !data.get("secret").equals(ReCraftAccessor.PROPERTIES.get("secret"))) {
			
			exchange.sendResponseHeaders(403, -1);
			return;
		}
		
		if(data.has("server_id")) {
			
			ReCraftAccessor.PROPERTIES.setProperty("server-id", data.get("server_id").getAsString());
		}
		
		if(data.has("controller_protocol")) {
			
			ReCraftAccessor.PROPERTIES.setProperty("controller-protocol", data.get("controller_protocol").getAsString());
		}
		
		if(data.has("controller_address")) {
		
			ReCraftAccessor.PROPERTIES.setProperty("controller-address", data.get("controller_address").getAsString());
		}
		
		if(data.has("controller_port")) {
			
			ReCraftAccessor.PROPERTIES.setProperty("controller-port", data.get("controller_port").getAsString());
		}
	}
}
