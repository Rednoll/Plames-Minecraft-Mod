package com.inwaiders.plames.integration.minecraft.accessor.inventory.sync;

import com.google.gson.JsonArray;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.network.ReCraftNetworkWrapper;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.ReCraftHttpConnector;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MarketBuySearchRequest implements IMessage {

	private String searchText = null;
	private int pageNumber = 0;
	private int pageSize = 0;
	
	public MarketBuySearchRequest() {}
	
	public MarketBuySearchRequest(String i, int pageNumber, int pageSize) {

		this.searchText = i;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
	
		this.searchText = ByteBufUtils.readUTF8String(buf);
		this.pageNumber = buf.readInt();
		this.pageSize = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeUTF8String(buf, searchText);
		buf.writeInt(pageNumber);
		buf.writeInt(pageSize);
	}
	
	public static class Handler implements IMessageHandler<MarketBuySearchRequest, IMessage> {

		@Override
		public IMessage onMessage(MarketBuySearchRequest message, MessageContext ctx) {
			
			String searchText = message.searchText;
			int pageNumber = message.pageNumber;
			int pageSize = message.pageSize;
			
			EntityPlayerMP player = ctx.getServerHandler().player;
			
			ReCraftAccessor.EXECUTOR_SERVICE.submit(()-> {
				
				JsonArray offers = ReCraftHttpConnector.searchOffers(searchText, pageNumber, pageSize);
				
				ReCraftNetworkWrapper.sendTo(player, new MarketBuySearchResponse(offers));
			});
			
			return null;
		}
	}
}
