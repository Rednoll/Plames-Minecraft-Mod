package com.inwaiders.plames.integration.minecraft.accessor.inventory.sync;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inwaiders.plames.integration.minecraft.accessor.MarketDataUtils;
import com.inwaiders.plames.integration.minecraft.accessor.commands.handlers.MarketBuyCommandHandler;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.MarketBuyInventory;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.ReCraftHttpConnector;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MarketBuyViewOfferRequest implements IMessage {

	private long id = 0;
	
	public MarketBuyViewOfferRequest() {}
	
	public MarketBuyViewOfferRequest(long id) {

		this.id = id;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
	
		this.id = buf.readLong();
	}

	@Override
	public void toBytes(ByteBuf buf) {

		buf.writeLong(id);
	}
	
	public static class Handler implements IMessageHandler<MarketBuyViewOfferRequest, IMessage> {

		@Override
		public IMessage onMessage(MarketBuyViewOfferRequest message, MessageContext ctx) {
			
			long id = message.id;
			
			EntityPlayerMP player = ctx.getServerHandler().player;
			
			JsonObject jsonOffer = ReCraftHttpConnector.getOffer(id);
			
			if(jsonOffer == null) return null;
			
			JsonArray jsonItemStacks = jsonOffer.get("item_stacks").getAsJsonArray();

			MarketBuyInventory inventory = MarketBuyCommandHandler.buyInventories.get(player);
			
				inventory.clear();
			
				for(JsonElement element : jsonItemStacks) {
					
					JsonObject jsonItemStack = element.getAsJsonObject();
					
					jsonItemStack.add("item", new Gson().fromJson((jsonItemStack.get("item").getAsJsonObject().get("metadata").getAsString()), JsonObject.class));
					
					ItemStack is = MarketDataUtils.fromMarketItemStack(jsonItemStack);
					
					inventory.spreadStack(is);
				}
				
			return null;
		}
	}
}
