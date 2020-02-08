package com.inwaiders.plames.integration.minecraft.accessor.inventory.sync;

import com.google.gson.JsonArray;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.network.ReCraftNetworkWrapper;
import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.ReCraftHttpConnector;
import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MarketBuyRequest implements IMessage {

	private int quantity = 0;
	private String offerName = null;
	
	public MarketBuyRequest() {}
	
	public MarketBuyRequest(String offerName, int quantity) {

		this.offerName = offerName;
		this.quantity = quantity;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
	
		this.offerName = ByteBufUtils.readUTF8String(buf);
		this.quantity = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeUTF8String(buf, offerName);
		buf.writeInt(this.quantity);
	}
	
	public static class Handler implements IMessageHandler<MarketBuyRequest, IMessage> {

		@Override
		public IMessage onMessage(MarketBuyRequest message, MessageContext ctx) {

			String offerName = message.offerName;
			int qunatity = message.quantity;
			
			EntityPlayerMP player = ctx.getServerHandler().player;
			GameProfile profile = player.getGameProfile();
			
			ReCraftHttpConnector.sendToMessengerServer(profile.getId(), profile.getName(), "/market /buy "+offerName+" "+qunatity);
			
			return null;
		}
	}
}
