package com.inwaiders.plames.integration.minecraft.accessor.inventory.sync;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.gui.MarketBuyGui;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.ReCraftHttpConnector;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MarketBuySearchResponse implements IMessage {
	
	private JsonArray offers = null;
	
	public MarketBuySearchResponse() {}
	
	public MarketBuySearchResponse(JsonArray offers) {

		this.offers = offers;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {

		String rawOffers = ByteBufUtils.readUTF8String(buf);
		
		this.offers = new Gson().fromJson(rawOffers, JsonArray.class);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		ByteBufUtils.writeUTF8String(buf, offers.toString());
	}
	
	public static class Handler implements IMessageHandler<MarketBuySearchResponse, IMessage> {

		@Override
		public IMessage onMessage(MarketBuySearchResponse message, MessageContext ctx) {
			
			JsonArray offers = message.offers;
			
			GuiScreen screen = Minecraft.getMinecraft().currentScreen;
			
			if(screen instanceof MarketBuyGui) {
				
				MarketBuyGui gui = (MarketBuyGui) screen;
				
				List<JsonObject> guiOffers = gui.getCurrentOffers();
				
				guiOffers.clear();
				
				for(JsonElement element : offers) {
					
					guiOffers.add((JsonObject) element);
				}
			}
			
			return null;
		}
	}
}
