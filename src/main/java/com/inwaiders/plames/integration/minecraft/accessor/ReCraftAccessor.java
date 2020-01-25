package com.inwaiders.plames.integration.minecraft.accessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.inwaiders.plames.integration.minecraft.accessor.chat.CommandProcedureStub;
import com.inwaiders.plames.integration.minecraft.accessor.commands.CommandHandlerRegistry;
import com.inwaiders.plames.integration.minecraft.accessor.commands.handlers.MarketCartCommandHandler;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.gui.ReCraftGuiHandler;
import com.inwaiders.plames.integration.minecraft.accessor.network.ReCraftNetworkWrapper;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.ReCraftHttpConnector;
import com.inwaiders.plames.integration.minecraft.stress.CommandStressBegin;
import com.inwaiders.plames.integration.minecraft.stress.CommandStressPrefix;
import com.inwaiders.plames.integration.minecraft.stress.CommandStressProfilesCount;
import com.inwaiders.plames.integration.minecraft.stress.CommandStressSpeed;
import com.inwaiders.plames.integration.minecraft.stress.CommandStressStop;

import net.minecraft.block.Block;
import net.minecraft.command.CommandHandler;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ReCraftAccessor.MODID, name = ReCraftAccessor.NAME, version = ReCraftAccessor.VERSION, acceptableRemoteVersions = "*")
public class ReCraftAccessor {

    public static final String MODID = "recraft_accessor";
    public static final String NAME = "ReCraft Accessor";
    public static final String VERSION = "1.0";

    public static Properties PROPERTIES = new Properties();
    
    private static File dataFile = new File("plames_accessor.data");
    public static JsonObject COMMON_DATA = null;
    
    public static JsonParser JSON_PARSER = new JsonParser();
    
    public static ExecutorService EXECUTOR_SERVICE = null;
    
    public static Map<String, Item> HASH_ITEM_MAP = new HashMap<>();
    
    @Mod.Instance(MODID)
    public static ReCraftAccessor instance;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    	ReCraftNetworkWrapper.init();
    	
    	if(event.getSide() == Side.SERVER) {
    		
    		serverPreInit();
    	}
    	
    	if(event.getSide() == Side.CLIENT) {
    		
    		clientPreInit();
    	}
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(ReCraftAccessor.instance, new ReCraftGuiHandler());
	}
    
    private void clientPreInit() {
    	
    	
    }
    
    private void serverPreInit() {

    	EXECUTOR_SERVICE = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("plames-integration-%d").build());
    	
    	File propertiesFile = new File("plames_accessor.properties");
 
		if(propertiesFile.exists()) {
    	
    		try {
    			
				PROPERTIES.load(Files.newInputStream(propertiesFile.toPath(), StandardOpenOption.READ));
			
				if(!PROPERTIES.containsKey("secret")) {
					
					System.out.println("Secret key not found in properties file, using default: 12345");
					PROPERTIES.setProperty("secret", "12345");
				}
				
				if(!PROPERTIES.containsKey("port")) {
					
					System.out.println("Port not found in properties file, using default: 7769");
					PROPERTIES.setProperty("port", "7769");
				}
				
				if(!PROPERTIES.containsKey("controller-protocol")) {
					
					System.out.println("Controller protocol not found in properties file, using default: http");
					PROPERTIES.setProperty("controller-protocol", "http");
				}
				
				if(!PROPERTIES.containsKey("controller-address")) {
					
					System.out.println("Controller address not found in properties file, using default: 0.0.0.0");
					PROPERTIES.setProperty("controller-address", "0.0.0.0");
				}
				
				if(!PROPERTIES.containsKey("controller-port")) {
					
					System.out.println("Controller port not found in properties file, using default: 80");
					PROPERTIES.setProperty("controller-port", "80");
				}
			
				if(!PROPERTIES.containsKey("server-id")) {
					
					System.out.println("Server id not found in properties file, will be requested from the server");
					//TODO: requesting...
				}
    		}
    		catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		else {
			
			System.out.println("Properties file not found! Creating default...");
			
			PROPERTIES.setProperty("secret", "12345");
			PROPERTIES.setProperty("port", "7769");
			PROPERTIES.setProperty("controller-protocol", "http");
			PROPERTIES.setProperty("controller-address", "0.0.0.0");
			PROPERTIES.setProperty("controller-port", "80");
		
			try {
				
				PROPERTIES.store(Files.newOutputStream(propertiesFile.toPath(), StandardOpenOption.CREATE_NEW), "");
			}
			catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		File dataFile = new File("plames_accessor.data");
		
		if(dataFile.exists()) {
		
			try {
				
				COMMON_DATA = (JsonObject) JSON_PARSER.parse(new String(Files.readAllBytes(dataFile.toPath())));
			}
			catch(JsonSyntaxException e) {
				
				e.printStackTrace();
			}
			catch(IOException e) {
				
				e.printStackTrace();
			}
		}
		else {
			
			COMMON_DATA = new JsonObject();
				COMMON_DATA.addProperty("market_items_hash", "");
			
			saveCommonData();
		}
    }
  
    @EventHandler
    public void init(FMLInitializationEvent event) {

    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	
    }
    
    @EventHandler
    public void serverInit(FMLServerStartingEvent event){
    	
    	ReCraftHttpConnector.init();
    	
    	CommandHandlerRegistry.register("market", new MarketCartCommandHandler());
    	
    	ReCraftHttpConnector.loadCommandsFromPlames((CommandHandler) event.getServer().getCommandManager());
    	
    	event.registerServerCommand(new CommandProcedureStub("m"));
    	
    	//stress
    	event.registerServerCommand(new CommandStressBegin());
    	event.registerServerCommand(new CommandStressPrefix());
    	event.registerServerCommand(new CommandStressProfilesCount());
    	event.registerServerCommand(new CommandStressSpeed());
    	event.registerServerCommand(new CommandStressStop());
    	
    	HASH_ITEM_MAP = createHashItemMap();
    	
    	marketInitialize();
    }
    
    private void marketInitialize() {
    	
		List<String> hashes = new ArrayList<>();
    	
    	List<Item> items = parseItems();
    	    
	    	for(Item item : items) {
	    		
	    		ItemStack is = new ItemStack(item);
	    		JsonObject itemMarketMetadata = MarketDataUtils.getMarketMetadata(is);
	    		
	    		String hash = DigestUtils.md5Hex(itemMarketMetadata.toString());
	    	
	    		hashes.add(hash);
	    	}
	    	
	    String totalHash = DigestUtils.sha256Hex(String.join("", hashes));
	    
	    System.out.println("Market Total Hash: "+totalHash);
	    
	    if(!COMMON_DATA.get("market_items_hash").getAsString().equals(totalHash)) {
	    	
	    	COMMON_DATA.addProperty("market_items_hash", totalHash);
	    	saveCommonData();
	    	
	    	ReCraftHttpConnector.sendItemsSyncRequest(totalHash, hashes);
	    }
    }
    
    public Map<String, Item> createHashItemMap() {
    	
    	Map<String, Item> result = new HashMap<>();
    	
    	List<Item> items = parseItems();
    	
    	for(Item item : items) {
    		
    		ItemStack is = new ItemStack(item);
    		JsonObject itemMarketMetadata = MarketDataUtils.getMarketMetadata(is);
    		
    		result.put(DigestUtils.md5Hex(itemMarketMetadata.toString()), item);
    	}
    	
    	return result;
    }
    
    public List<Item> parseItems() {
    	
    	List<Item> items = new ArrayList<>();
    	
    	try {
    		
    		Set<ResourceLocation> keys = Item.REGISTRY.getKeys();
    		
			for(ResourceLocation key : keys) {
				
				Item item = Item.REGISTRY.getObject(key);
				
				String unlocalizedName = item.getUnlocalizedName();
				
				if(unlocalizedName.startsWith("item")) {
					
					items.add(item);
				}
			}
			
			
			keys = Block.REGISTRY.getKeys();
			
			for(ResourceLocation key : keys) {
				
				Block block = Block.REGISTRY.getObject(key);
				
				String unlocalizedName = block.getUnlocalizedName();
				
				if(unlocalizedName.startsWith("tile")) {
		
					Item item = Item.getItemFromBlock(block);
					
					if(item != null && item != Items.AIR) {
						
						items.add(item);
					}
				}
			}
			
    	}
    	catch(SecurityException e) {
			
			e.printStackTrace();
		}
    	catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		}
    	
    	return items;
    }
    
    public static void saveCommonData() {
    	
    	if(dataFile.exists()) {
    		
    		dataFile.delete();
    	}
    	
    	try {
    		
			Files.write(dataFile.toPath(), COMMON_DATA.toString().getBytes());
		}
    	catch (IOException e) {
		
			e.printStackTrace();
		}
    }
}
