package com.inwaiders.plames.integration.minecraft.accessor;

import java.util.UUID;

import com.google.gson.JsonObject;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;

public class MarketDataUtilsTest {

	public static void test() {
		
		NBTTagCompound root = new NBTTagCompound();
			root.setByte("testByte", (byte) 0);
			root.setInteger("testInteger", (int) 1);
			root.setBoolean("testBoolean", true);
			root.setDouble("testDouble", 2D);
			root.setFloat("testFloat", 3F);
			root.setLong("testLong", 4L);
			root.setShort("testShort", (short) 5);
			root.setString("testString", "test");
			root.setUniqueId("testUUID", UUID.randomUUID());
			root.setByteArray("testByteArray", new byte[] {6, 7, 8});
			root.setIntArray("testIntArray", new int[] {9, 10, 11});
			
			NBTTagCompound testTag = new NBTTagCompound();
				testTag.setDouble("testTagDouble", 12D);
			
			root.setTag("testTag", testTag);
	
			NBTTagList testList = new NBTTagList();
				testList.set(0, new NBTTagInt(13));
				testList.set(1, new NBTTagInt(14));
				testList.set(2, new NBTTagInt(15));
				
			root.setTag("testList", testList);
			
		JsonObject object = MarketDataUtils.nbtToJson(root);
	
		System.out.println(object.toString());
	}
}
