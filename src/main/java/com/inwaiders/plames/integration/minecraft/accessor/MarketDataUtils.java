package com.inwaiders.plames.integration.minecraft.accessor;

import java.lang.reflect.Field;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import scala.util.parsing.json.JSON;

public class MarketDataUtils {

	private static Gson gson = new GsonBuilder().create();
	
	public static JsonObject getMarketItem(ItemStack is) {
		
		return getMarketItem(is, getMarketMetadata(is));
	}
	
	public static JsonObject getMarketItem(ItemStack is, JsonObject metadata) {
	
		JsonObject root = new JsonObject(); 
			root.addProperty("name", normalizeItemName(is.getItem().getRegistryName().getResourcePath()));
			root.add("metadata", metadata);
		
		return root;
	}
	
	public static JsonObject getMarketMetadata(ItemStack is) {
		
		Item item = is.getItem();
		
		JsonObject root = new JsonObject();
			root.addProperty("id", Item.getIdFromItem(item));
			
			ResourceLocation rl = item.getRegistryName();
			root.addProperty("name", rl.getResourceDomain()+":"+rl.getResourcePath());
			
			root.addProperty("domain", rl.getResourceDomain());
			root.addProperty("path", rl.getResourcePath());
			
			NBTTagCompound nbt = is.serializeNBT();
			
				JsonObject jsonNbt = nbtToJson(nbt);
			
			root.add("nbt", jsonNbt);
			
		return root;
	}
	
	public static ItemStack fromMarketItemStack(JsonObject data) {
		
		int quantity = data.get("quantity").getAsInt();
		
		JsonObject jsonItem = data.get("item").getAsJsonObject();
		
		Item item = null;
		
			if(jsonItem.has("id")) {
				
				int itemId = jsonItem.get("id").getAsInt();
			
				item = Item.getItemById(itemId);
			
				if(item == null) {
					
					item = Item.getItemFromBlock(Block.getBlockById(itemId));
				}
			}
			else if(jsonItem.has("name")) {
				
				String itemName = jsonItem.get("name").getAsString();
				
				item = Item.getByNameOrId(itemName);
				
				if(item == null) {
					
					item = Item.getItemFromBlock(Block.getBlockFromName(itemName));
				}
			}
			
			if(jsonItem.has("nbt")) {
				
				jsonItem.get("nbt");
				
				//TODO
			}
			
		return new ItemStack(item, quantity);
	}
	
	public static JsonObject nbtToJson(NBTTagCompound compound) {
	
		Set<String> keys = compound.getKeySet();
		
		JsonObject root = new JsonObject();
	
			for(String key : keys) {
				
				NBTBase base = compound.getTag(key);
			
				writeTag(key, base, root);
			}
		
		return root;
	}
	
	public static void writeTag(String key, NBTBase base, JsonObject jsonObject) {
		
		if(base instanceof NBTTagCompound) {
			
			jsonObject.add(key, nbtToJson((NBTTagCompound) base));
		}
		else if(base instanceof NBTTagByte) {
			
			jsonObject.addProperty(key, ((NBTTagByte) base).getByte());
		}
		else if(base instanceof NBTTagByteArray) {
			
			byte[] bytes = ((NBTTagByteArray) base).getByteArray();
			
			JsonArray array = new JsonArray();
				
				for(byte b : bytes) {
					
					array.add(b);
				}
			
			jsonObject.add(key, array);
		}
		else if(base instanceof NBTTagDouble) {
			
			jsonObject.addProperty(key, ((NBTTagDouble) base).getDouble());
		}
		else if(base instanceof NBTTagFloat) {
			
			jsonObject.addProperty(key, ((NBTTagFloat) base).getFloat());
		}
		else if(base instanceof NBTTagInt) {
			
			jsonObject.addProperty(key, ((NBTTagInt) base).getInt());
		}
		else if(base instanceof NBTTagIntArray) {
			
			int[] integers = ((NBTTagIntArray) base).getIntArray();
			
			JsonArray array = new JsonArray();
				
				for(int i : integers) {
					
					array.add(i);
				}
			
			jsonObject.add(key, array);
		}
		else if(base instanceof NBTTagList) {
			
			NBTTagList data = (NBTTagList) base;
			
			JsonArray array = new JsonArray();
			
				for(NBTBase nbt : data) {
					
					writeTagToArray(nbt, array);
				}
				
			jsonObject.add(key, array);
		}
		else if(base instanceof NBTTagLong) {
		
			jsonObject.addProperty(key, ((NBTTagLong) base).getLong());
		}
		else if(base instanceof NBTTagLongArray) {
			
			Field[] fields = NBTTagLongArray.class.getDeclaredFields();
		
			c2: for(Field field : fields) {
				
				if(field.getType() == long[].class) {
					
					try {
						
						long[] longs = (long[]) field.get((NBTTagLongArray) base);
					
						JsonArray array = new JsonArray();
							
							for(long l : longs) {
								
								array.add(l);
							}
						
						jsonObject.add(key, array);
						
						break c2;
					}
					catch(IllegalArgumentException e) {
						
						e.printStackTrace();
					}
					catch(IllegalAccessException e) {
						
						e.printStackTrace();
					}
				}
			}
		}
		else if(base instanceof NBTTagShort) {
			
			jsonObject.addProperty(key, ((NBTTagShort) base).getShort());
		}
		else if(base instanceof NBTTagString) {
			
			jsonObject.addProperty(key, ((NBTTagString) base).getString());
		}
	}
	
	public static void writeTagToArray(NBTBase base, JsonArray jsonArray) {
		
		if(base instanceof NBTTagCompound) {
			
			jsonArray.add(nbtToJson((NBTTagCompound) base));
		}
		else if(base instanceof NBTTagByte) {
			
			jsonArray.add(((NBTTagByte) base).getByte());
		}
		else if(base instanceof NBTTagByteArray) {
			
			byte[] bytes = ((NBTTagByteArray) base).getByteArray();
			
			JsonArray array = new JsonArray();
				
				for(byte b : bytes) {
					
					array.add(b);
				}
			
			jsonArray.add(array);
		}
		else if(base instanceof NBTTagDouble) {
			
			jsonArray.add(((NBTTagDouble) base).getDouble());
		}
		else if(base instanceof NBTTagFloat) {
			
			jsonArray.add(((NBTTagFloat) base).getFloat());
		}
		else if(base instanceof NBTTagInt) {
			
			jsonArray.add(((NBTTagInt) base).getInt());
		}
		else if(base instanceof NBTTagIntArray) {
			
			int[] integers = ((NBTTagIntArray) base).getIntArray();
			
			JsonArray array = new JsonArray();
				
				for(int i : integers) {
					
					array.add(i);
				}
			
			jsonArray.add(array);
		}
		else if(base instanceof NBTTagList) {
			
			NBTTagList data = (NBTTagList) base;
			
			JsonArray array = new JsonArray();
			
				for(NBTBase nbt : data) {
				
					writeTagToArray(nbt, array);
				}
				
			jsonArray.add(array);
		}	
		else if(base instanceof NBTTagLong) {
	
			jsonArray.add(((NBTTagLong) base).getLong());
		}
		else if(base instanceof NBTTagLongArray) {
			
			Field[] fields = NBTTagLongArray.class.getDeclaredFields();
		
			c2: for(Field field : fields) {
				
				if(field.getType() == long[].class) {
					
					try {
						
						long[] longs = (long[]) field.get((NBTTagLongArray) base);
					
						JsonArray array = new JsonArray();
							
							for(long l : longs) {
								
								array.add(l);
							}
						
						jsonArray.add(array);
						
						break c2;
					}
					catch(IllegalArgumentException e) {
						
						e.printStackTrace();
					}
					catch(IllegalAccessException e) {
						
						e.printStackTrace();
					}
				}
			}
		}
		else if(base instanceof NBTTagShort) {
			
			jsonArray.add(((NBTTagShort) base).getShort());
		}
		else if(base instanceof NBTTagString) {
			
			jsonArray.add(((NBTTagString) base).getString());
		}
	}
	
    public static String normalizeItemName(String name) {
    	
    	String[] words = name.split("_");
    
    	for(int i = 0; i<words.length; i++) {
    		
    		if(words[i].toLowerCase().contains("item") || words[i].toLowerCase().contains("block")) continue;
    		
    		words[i] = (words[i].charAt(0)+"").toUpperCase()+words[i].substring(1);
    	}
    	
    	return String.join(" ", words);
    }
}
