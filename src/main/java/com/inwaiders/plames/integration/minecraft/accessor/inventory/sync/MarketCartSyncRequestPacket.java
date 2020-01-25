package com.inwaiders.plames.integration.minecraft.accessor.inventory.sync;

import com.inwaiders.plames.integration.minecraft.accessor.commands.handlers.MarketCartCommandHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MarketCartSyncRequestPacket implements IMessage {

	public MarketCartSyncRequestPacket() {}
	
	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {
	
	}
	
	public static class Handler implements IMessageHandler<MarketCartSyncRequestPacket, IMessage> {

		@Override
		public IMessage onMessage(MarketCartSyncRequestPacket message, MessageContext ctx) {

			EntityPlayerMP player = ctx.getServerHandler().player;
			
			int allStacksCount = MarketCartCommandHandler.cartInventories.get(player).getFilledStacksPart();
		
			return new MarketCartSyncPacket(allStacksCount);
		}
	}
}
