package com.inwaiders.plames.integration.minecraft.accessor.inventory.sync;

import com.inwaiders.plames.integration.minecraft.accessor.commands.handlers.MarketCartCommandHandler;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MarketCartSyncPacket implements IMessage {

	int allStacksCount = 0;
	
	public MarketCartSyncPacket() {}
	
	public MarketCartSyncPacket(int allStacksCount) {

		this.allStacksCount = allStacksCount;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {

		this.allStacksCount = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
	
		buf.writeInt(this.allStacksCount);
	}
	
	public static class Handler implements IMessageHandler<MarketCartSyncPacket, IMessage> {

		@Override
		public IMessage onMessage(MarketCartSyncPacket message, MessageContext ctx) {
			
			MarketCartCommandHandler.cartInventory.setAllStacksCount(message.allStacksCount);
			
			return null;
		}
	}
}
