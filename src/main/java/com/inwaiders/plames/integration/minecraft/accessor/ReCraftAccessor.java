package com.inwaiders.plames.integration.minecraft.accessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonParser;
import com.inwaiders.plames.integration.minecraft.accessor.chat.CommandProcedureStub;
import com.inwaiders.plames.integration.minecraft.accessor.chat.CommandStub;
import com.inwaiders.plames.integration.minecraft.accessor.network.ReCraftHttpConnector;
import com.inwaiders.plames.integration.minecraft.stress.CommandStressBegin;
import com.inwaiders.plames.integration.minecraft.stress.CommandStressPrefix;
import com.inwaiders.plames.integration.minecraft.stress.CommandStressProfilesCount;
import com.inwaiders.plames.integration.minecraft.stress.CommandStressSpeed;
import com.inwaiders.plames.integration.minecraft.stress.CommandStressStop;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ReCraftAccessor.MODID, name = ReCraftAccessor.NAME, version = ReCraftAccessor.VERSION, acceptableRemoteVersions = "*")
public class ReCraftAccessor {

    public static final String MODID = "recraft_accessor";
    public static final String NAME = "ReCraft Accessor";
    public static final String VERSION = "1.0";

    public static Properties properties = new Properties();
    
   // private static Logger logger;
    
    public static JsonParser jsonParser = new JsonParser();
    
    public static ExecutorService executorService = null;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    	executorService = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("plames-integration-%d").build());
    	
    	//logger = event.getModLog();
    	
    	File propertiesFile = new File("recraft_accessor.properties");
 
		if(propertiesFile.exists()) {
    	
    		try {
    			
				properties.load(Files.newInputStream(propertiesFile.toPath(), StandardOpenOption.READ));
			
				if(!properties.containsKey("secret")) {
					
				//	logger.warn("Secret key not found in properties file, using default: 12345");
					System.out.println("Secret key not found in properties file, using default: 12345");
					properties.setProperty("secret", "12345");
				}
				
				if(!properties.containsKey("port")) {
					
				//	logger.warn("Port not found in properties file, using default: 7769");
					System.out.println("Port not found in properties file, using default: 7769");
					properties.setProperty("port", "7769");
				}
				
				if(!properties.containsKey("controller-protocol")) {
					
				//	logger.warn("Controller protocol not found in properties file, using default: http");
					System.out.println("Controller protocol not found in properties file, using default: http");
					properties.setProperty("controller-protocol", "http");
				}
				
				if(!properties.containsKey("controller-address")) {
					
				//	logger.warn("Controller address not found in properties file, using default: 0.0.0.0");
					System.out.println("Controller address not found in properties file, using default: 0.0.0.0");
					properties.setProperty("controller-address", "0.0.0.0");
				}
				
				if(!properties.containsKey("controller-port")) {
					
				//	logger.warn("Controller port not found in properties file, using default: 80");
					System.out.println("Controller port not found in properties file, using default: 80");
					properties.setProperty("controller-port", "80");
				}
			
				if(!properties.containsKey("server-id")) {
					
				//	logger.warn("Server id not found in properties file, will be requested from the server");
					System.out.println("Server id not found in properties file, will be requested from the server");
					//TODO: requesting...
				}
    		}
    		catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		else {
			
//			logger.warn("Properties file not found! Creating default...");
			System.out.println("Properties file not found! Creating default...");
			
			properties.setProperty("secret", "12345");
			properties.setProperty("port", "7769");
			properties.setProperty("controller-protocol", "http");
			properties.setProperty("controller-address", "0.0.0.0");
			properties.setProperty("controller-port", "80");
		
			try {
				
				properties.store(Files.newOutputStream(propertiesFile.toPath(), StandardOpenOption.CREATE_NEW), "");
			}
			catch (IOException e) {
				
				e.printStackTrace();
			}
		}
    }
  
    @EventHandler
    public void init(FMLInitializationEvent event) {

    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	
    	ReCraftHttpConnector.init();
    }
    
    @EventHandler
    public void serverInit(FMLServerStartingEvent event){
    	
    	List<String> commandsAliases = ReCraftHttpConnector.requestCommandsAliases();
    	
    	for(String commandAliase : commandsAliases) {
    		
    		event.registerServerCommand(new CommandStub(commandAliase));
    	}
    	
    	event.registerServerCommand(new CommandProcedureStub("m"));
    	
    	//stress
    	event.registerServerCommand(new CommandStressBegin());
    	event.registerServerCommand(new CommandStressPrefix());
    	event.registerServerCommand(new CommandStressProfilesCount());
    	event.registerServerCommand(new CommandStressSpeed());
    	event.registerServerCommand(new CommandStressStop());
    }
    
    @EventHandler
    public void parsingItems(FMLServerStartingEvent event) {
    	
    	try {
    		
    		Set<ResourceLocation> keys = Item.REGISTRY.getKeys();
    		
			for(ResourceLocation key : keys) {
				
				Item item = Item.REGISTRY.getObject(key);
				
				String unlocalizedName = item.getUnlocalizedName();
				
				if(unlocalizedName.startsWith("item")) {
				
					System.out.println("name: "+normalizeItemName(key.getResourcePath()));
					System.out.println(key.getResourceDomain()+":"+key.getResourcePath());
				}
			}
    	}
    	catch(SecurityException e) {
			
			e.printStackTrace();
		}
    	catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		}
    	
    	MarketDataUtilsTest.test();
    }
    
    public String normalizeItemName(String name) {
    	
    	String[] words = name.split("_");
    
    	for(int i = 0; i<words.length; i++) {
    		
    		if(words[i].toLowerCase().contains("item") || words[i].toLowerCase().contains("block")) continue;
    		
    		words[i] = (words[i].charAt(0)+"").toUpperCase()+words[i].substring(1);
    	}
    	
    	return String.join(" ", words);
    }
}
