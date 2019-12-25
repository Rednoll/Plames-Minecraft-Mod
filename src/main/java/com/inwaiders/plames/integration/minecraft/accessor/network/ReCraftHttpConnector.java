package com.inwaiders.plames.integration.minecraft.accessor.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.network.handlers.AllPlayersCountHandler;
import com.inwaiders.plames.integration.minecraft.accessor.network.handlers.CheckOnlineHandler;
import com.inwaiders.plames.integration.minecraft.accessor.network.handlers.CollectCartHandler;
import com.inwaiders.plames.integration.minecraft.accessor.network.handlers.OnlinePlayersCountHandler;
import com.inwaiders.plames.integration.minecraft.accessor.network.handlers.PropertiesHandler;
import com.inwaiders.plames.integration.minecraft.accessor.network.handlers.SendMessageHandler;
import com.sun.net.httpserver.HttpServer;

public class ReCraftHttpConnector {
	
	private static HttpServer server = null;
	
	public static void init() {
		
		try {
			
			server = HttpServer.create();
				server.bind(new InetSocketAddress(Integer.valueOf(ReCraftAccessor.properties.getProperty("port"))), 0);
				server.createContext("/properties", new PropertiesHandler());
				server.createContext("/send_message", new SendMessageHandler());
				server.createContext("/check_online", new CheckOnlineHandler());
				server.createContext("/players_count", new AllPlayersCountHandler());
				server.createContext("/online_count", new OnlinePlayersCountHandler());
				server.createContext("/collect_cart", new CollectCartHandler());
				
			server.start();
			
			System.out.println("Embedded server started!");
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static boolean sendToMessengerServer(UUID playerUUID, String playerName, String text) {
		
		Properties properties = ReCraftAccessor.properties;
			
		JsonObject data = new JsonObject();
			data.addProperty("server_id", Long.valueOf((String) properties.get("server-id")));
			data.addProperty("secret", (String) properties.get("secret"));
			data.addProperty("type", "new_message");
			
			JsonObject dataObject = new JsonObject();
				dataObject.addProperty("player_name", playerName);
				dataObject.addProperty("player_uuid", playerUUID.toString());
				
				try {
					
					dataObject.addProperty("text", URLEncoder.encode(text, "UTF-8"));
				}
				catch(UnsupportedEncodingException e1) {
					
					e1.printStackTrace();
				}
				
			data.add("object", dataObject);
			
		System.out.println(data.toString());
			
		try {
	
			
			HttpPost post = new HttpPost(getMethodUrl("api/minecraft/callback"));
				post.setEntity(new StringEntity(data.toString()));
				post.setHeader("Content-type", "application/json;charset=UTF-8");
				
			HttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = (CloseableHttpResponse) client.execute(post);
			
			int statusCode = response.getStatusLine().getStatusCode();

			EntityUtils.consume(response.getEntity());
			
			if(statusCode != HttpStatus.SC_OK) {
				
				return false;
			}
			
			return true;
			
			/*
			URL url = new URL(getMethodUrl("api/minecraft/callback"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json; utf-8");
				con.setDoOutput(true);
				
			con.getOutputStream().write(data.toString().getBytes());
			
			int repsonseCode = con.getResponseCode();
			
			if(repsonseCode == 200) {
				
				return true;
			}
			
			return false;
			
			*/
		}
		catch(IOException e) {
	
			e.printStackTrace();
			
			return false;
		}
	}
	
	public static List<String> requestCommandsAliases() {
		
		List<String> result = new ArrayList<>();
		
    	CloseableHttpClient httpClient = (CloseableHttpClient) HttpClients.createDefault();
    	
    	HttpGet get = new HttpGet(getMethodUrl("api/minecraft/ajax/commands"));
	
    	try {
    		
			CloseableHttpResponse response = httpClient.execute(get);
		
	    		HttpEntity entity = response.getEntity();
	    		
	    		String rawData = EntityUtils.toString(entity);
	  
	    		EntityUtils.consume(entity);
	    		
	    	JsonArray array = new JsonParser().parse(rawData).getAsJsonArray();
	    	
	    	for(JsonElement element : array) {
	    		
	    		String commnadAliase = element.getAsString();
	    	
	    		result.add(commnadAliase);
	    	}
	    	
    	}
    	catch (ClientProtocolException e) {
			e.printStackTrace();
		}
    	catch (IOException e) {
			e.printStackTrace();
    	}
    	
    	return result;
	}
	
	private static String getMethodUrl(String methodName) {
		
		Properties properties = ReCraftAccessor.properties;
		
		StringBuilder builder = new StringBuilder();
			builder.append(properties.get("controller-protocol"));
			builder.append("://");
			builder.append(properties.get("controller-address"));
			builder.append(":"+properties.get("controller-port"));
			builder.append("/"+methodName);
			
		return builder.toString();
	}
}
