package com.inwaiders.plames.integration.minecraft.stress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.inwaiders.plames.integration.minecraft.accessor.network.ReCraftHttpConnector;

public class AccessorStress {

	public static StressThread thread = null;
	
	public static String[] words = new String[] {
		
		"привет", "дороу", "хай", "ку", "O/",
		"ты", "он", "она", "они", "оно",
		"дота", "кс", "майн",
		"пошли", "погнали", "го", "мб",
		"в",
		"а",
		"как",
		"персик", "печень", "апельсин", "яблоко", "тандыр"
	};
	
	public static double speed = 10; // messages per second
	public static double profilesCount = 10; // emulated profiles count
	public static double minWords = 2;
	public static double maxWords = 8;
	public static MessagePrefix prefix = null;
	
	public static Map<String, MessagePrefix> prefixRegistry = new HashMap<>();
	
	public static List<EmulatedProfile> profiles = new ArrayList<EmulatedProfile>();
	
	private static Random random = new Random();
	private static StringBuilder stringBuilder = new StringBuilder();
	
	public static boolean profilesRegistered = false;
	
	static {
		
		prefixRegistry.put("send", ()-> "/send "+profiles.get(random.nextInt(profiles.size())).username+" mc ");
	}
	
	public static void beginStress() {
		
		thread = new StressThread();
	
		thread.start();
	}
	
	public static void stopStress() {
		
		thread.stop();
	}
	
	public static void updateSpeed(double speed) {

		AccessorStress.speed = speed;
		
		if(thread != null) {
			
			thread.delta = (int) Math.round(1000D/speed);
		}
	}
	
	public static void setPrefix(String name) {
		
		prefix = prefixRegistry.get(name);
	}
	
	public static void generateProfiles() {
		
		profiles.clear();
		profiles = new ArrayList<>();
		
		for(int i = 0;i<profilesCount;i++) {
			
			profiles.add(new EmulatedProfile(UUID.randomUUID(), Math.abs(random.nextInt())+"-test"));
		}
		
		profilesRegistered = false;
	}
	
	public static void registerProfiles() {
		
		if(profilesRegistered == false) {
		
			for(EmulatedProfile profile : profiles) {
				
				ReCraftHttpConnector.sendToMessengerServer(profile.uuid, profile.username, "reggg");
				
				try {
					
					Thread.sleep(250);
				}
				catch(InterruptedException e) {
	
					e.printStackTrace();
				}
				
				ReCraftHttpConnector.sendToMessengerServer(profile.uuid, profile.username, profile.username);
				
				try {
					
					Thread.sleep(250);
				}
				catch(InterruptedException e) {
	
					e.printStackTrace();
				}
				
				ReCraftHttpConnector.sendToMessengerServer(profile.uuid, profile.username, "да");
			
				try {
					
					Thread.sleep(250);
				}
				catch(InterruptedException e) {
	
					e.printStackTrace();
				}
			}
			
			profilesRegistered = true;
		}
	}
	
	public static String generateRandomText() {
		
		stringBuilder.setLength(0);
		
		int size = (int) (random.nextInt((int) (maxWords-minWords))+minWords);
		
		for(int i = 0; i<size;i++) {
			
			stringBuilder.append(words[random.nextInt(words.length)]+" ");
		}
		
		return stringBuilder.toString().trim();
	}
	
	private static class StressThread extends Thread{
		
		public int delta = (int) Math.round(1000D/speed);
		
		@Override
		public void run() {
			
			while(true) {
				
				EmulatedProfile profile = profiles.get(random.nextInt(profiles.size()));
				
				String text = generateRandomText();
				
				ReCraftHttpConnector.sendToMessengerServer(profile.uuid, profile.username, prefix.get()+text);
				
				try {
					
					Thread.sleep(delta);
				} 
				catch(InterruptedException e) {
					
					e.printStackTrace();
				}
			}
		}
	}
	
	public static class EmulatedProfile {
		
		public UUID uuid = null;
		public String username = null;
		
		public EmulatedProfile(UUID uuid, String username) {
			
			this.uuid = uuid;
			this.username = username;
		}
	}
	
	private static interface MessagePrefix { 
		
		public String get();
	}
}
