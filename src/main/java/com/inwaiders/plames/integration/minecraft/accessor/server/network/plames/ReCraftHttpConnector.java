package com.inwaiders.plames.integration.minecraft.accessor.server.network.plames;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.chat.CommandStub;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.handlers.AllPlayersCountHandler;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.handlers.CheckOnlineHandler;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.handlers.CollectCartHandler;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.handlers.ItemsHandler;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.handlers.OnlinePlayersCountHandler;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.handlers.PropertiesHandler;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.handlers.SendMessageHandler;
import com.sun.net.httpserver.HttpServer;

import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayer;

public class ReCraftHttpConnector {
	
	private static HttpServer server = null;
	
	private static Thread requestProcessor = null;
	private static BlockingDeque<Task> requests = new LinkedBlockingDeque<>();
	
	public static void init() {
		
		try {
			
			server = HttpServer.create();
				server.bind(new InetSocketAddress(Integer.valueOf(ReCraftAccessor.PROPERTIES.getProperty("port"))), 0);
				server.createContext("/properties", new PropertiesHandler());
				server.createContext("/send_message", new SendMessageHandler());
				server.createContext("/check_online", new CheckOnlineHandler());
				server.createContext("/players_count", new AllPlayersCountHandler());
				server.createContext("/online_count", new OnlinePlayersCountHandler());
				server.createContext("/collect_cart", new CollectCartHandler());
				server.createContext("/items", new ItemsHandler());
				
			server.start();
			
			System.out.println("Embedded server started!");
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
		
		requestProcessor = new Thread(()-> {
			
			while(true) {
				
				Task task = null;
				
				try {
					
					task = requests.take();
				}
				catch(InterruptedException e2) {
					
					e2.printStackTrace();
				}
				
				try {

					task.run();
				}
				catch(IOException e) {
					
					if(e instanceof ClientProtocolException) {
						
						System.out.println("Can't connect to Plames System!");
						
						requests.addFirst(task);
						
						try {
							
							Thread.sleep(5000);
						}
						catch(InterruptedException e1) {
						
							e1.printStackTrace();
						}
					}
				}
			}
			
		});

		requestProcessor.start();
	}
	
	public static void addTask(Task task) {
		
		requests.add(task);
	}
	
	public static JsonObject getOffer(long id) {
		
		try {

			HttpGet get = new HttpGet(getMethodUrl("api/market/rest/offers/"+id));
	
	    	CloseableHttpClient httpClient = HttpClients.createDefault();
	    	
			CloseableHttpResponse response = httpClient.execute(get);
		
	    		HttpEntity entity = response.getEntity();
	    		
	    		String rawData = EntityUtils.toString(entity);
	  
	    		EntityUtils.consume(entity);
	    		
	    		if(rawData == null || rawData.isEmpty()) {
	    			
	    			return null;
	    		}
	    		   		
	    	JsonObject jsonOffer = new JsonParser().parse(rawData).getAsJsonObject();
	    	
	    	return jsonOffer;
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static JsonArray searchOffers(String name, int pageNumber, int pageSize) {
		
		try {

			HttpGet get = new HttpGet(getMethodUrl("/web/controller/ajax/market/offer?page="+pageNumber+"&pageSize="+pageSize+"&name="+name));
	
	    	CloseableHttpClient httpClient = HttpClients.createDefault();
	    	
			CloseableHttpResponse response = httpClient.execute(get);
		
	    		HttpEntity entity = response.getEntity();
	    		
	    		String rawData = EntityUtils.toString(entity);
	  
	    		EntityUtils.consume(entity);
	    		
	    	JsonArray array = new JsonParser().parse(rawData).getAsJsonArray();
	    	
	    	return array;
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
		
		return new JsonArray();
	}
	
	public static void decrItemStackSizeFromMarketCart(EntityPlayer ep, long itemStackId, int quantity) {
		
		Properties properties = ReCraftAccessor.PROPERTIES;
		
		JsonObject data = new JsonObject();
			data.addProperty("server", Long.valueOf((String) properties.get("server-id")));
			data.addProperty("secret", (String) properties.get("secret"));
			data.addProperty("item_stack_id", itemStackId);
			data.addProperty("quantity", quantity);
			data.addProperty("player_name", ep.getGameProfile().getName());
			data.addProperty("player_uuid", ep.getGameProfile().getId().toString());
			
		try {
	
			HttpPost post = new HttpPost(getMethodUrl("api/mc/ajax/server/player_cart/decr"));
				post.setEntity(new StringEntity(data.toString()));
				post.setHeader("Content-type", "application/json;charset=UTF-8");
			
			HttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = (CloseableHttpResponse) client.execute(post);
			
			int statusCode = response.getStatusLine().getStatusCode();

			EntityUtils.consume(response.getEntity());
		}
		catch(IOException e) {
	
			e.printStackTrace();
		}
	}
	
	public static void sendItemsSyncRequest(String totalHash, List<String> hashes) {
		
		System.out.println("Sending items sync request");
		
		Properties properties = ReCraftAccessor.PROPERTIES;
		
		JsonObject data = new JsonObject();
			data.addProperty("server_id", Long.valueOf((String) properties.get("server-id")));
			data.addProperty("secret", (String) properties.get("secret"));
			data.add("hashes", new Gson().toJsonTree(hashes));
			data.addProperty("total_hash", totalHash);
			
		try {
	
			HttpPost post = new HttpPost(getMethodUrl("api/minecraft/ajax/item/sync"));
				post.setEntity(new StringEntity(data.toString()));
				post.setHeader("Content-type", "application/json;charset=UTF-8");
			
			HttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = (CloseableHttpResponse) client.execute(post);
			
			int statusCode = response.getStatusLine().getStatusCode();

			EntityUtils.consume(response.getEntity());
		}
		catch(IOException e) {
	
			e.printStackTrace();
		}
	}
	
	public static boolean sendToMessengerServer(UUID playerUUID, String playerName, String text) {
		
		Properties properties = ReCraftAccessor.PROPERTIES;
			
		JsonObject root = new JsonObject();
			root.addProperty("server_id", Long.valueOf((String) properties.get("server-id")));
			root.addProperty("secret", (String) properties.get("secret"));
			root.addProperty("type", "new_message");
			
			JsonObject dataObject = new JsonObject();
				dataObject.addProperty("player_name", playerName);
				dataObject.addProperty("player_uuid", playerUUID.toString());
				
				try {
					
					dataObject.addProperty("text", URLEncoder.encode(text, "UTF-8"));
				}
				catch(UnsupportedEncodingException e1) {
					
					e1.printStackTrace();
				}
				
			root.add("object", dataObject);
			
		try {
	
			
			HttpPost post = new HttpPost(getMethodUrl("api/minecraft/callback"));
				post.setEntity(new StringEntity(root.toString()));
				post.setHeader("Content-type", "application/json;charset=UTF-8");
				
			HttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = (CloseableHttpResponse) client.execute(post);
			
			int statusCode = response.getStatusLine().getStatusCode();

			EntityUtils.consume(response.getEntity());
			
			if(statusCode != HttpStatus.SC_OK) {
				
				return false;
			}
			
			return true;
		}
		catch(IOException e) {
	
			e.printStackTrace();
			
			return false;
		}
	}
	
	public static void loadCommandsFromPlames(CommandHandler commandHandler) {

		requests.add(()-> {
		
			List<String> commandsAliases = requestCommandsAliases();
	    	
	    	for(String commandAliase : commandsAliases) {
	    		
	    		commandHandler.registerCommand(new CommandStub(commandAliase));
	    		System.out.println("registered command/aliase: "+commandAliase);
	    	}
		});
	}
	
	public static List<String> requestCommandsAliases() throws ClientProtocolException, IOException {
		
		List<String> result = new ArrayList<>();
		
    	HttpGet get = new HttpGet(getMethodUrl("api/minecraft/ajax/commands"));

    	CloseableHttpClient httpClient = HttpClients.createDefault();
    	
		CloseableHttpResponse response = httpClient.execute(get);
	
    		HttpEntity entity = response.getEntity();
    		
    		String rawData = EntityUtils.toString(entity);
  
    		EntityUtils.consume(entity);
    		
    	JsonArray array = new JsonParser().parse(rawData).getAsJsonArray();
    	
    	for(JsonElement element : array) {
    		
    		String commnadAliase = element.getAsString();
    	
    		result.add(commnadAliase);
    	}
    	
    	return result;
	}
	
	public static String getMethodUrl(String methodName) {
		
		Properties properties = ReCraftAccessor.PROPERTIES;
		
		StringBuilder builder = new StringBuilder();
			builder.append(properties.get("controller-protocol"));
			builder.append("://");
			builder.append(properties.get("controller-address"));
			builder.append(":"+properties.get("controller-port"));
			builder.append("/"+methodName);
			
		return builder.toString();
	}
	
	private static interface Task {
		
		public void run() throws ClientProtocolException, IOException;
	}
}
