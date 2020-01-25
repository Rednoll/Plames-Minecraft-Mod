package com.inwaiders.plames.integration.minecraft.accessor.inventory.sync;

import com.inwaiders.plames.integration.minecraft.accessor.commands.handlers.MarketCartCommandHandler;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.MarketCartInventory;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MarketCartScrollPacket implements IMessage {

	double targetScroll = 0;
	
	public MarketCartScrollPacket() {}
	
	public MarketCartScrollPacket(double targetScroll) {

		this.targetScroll = targetScroll;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {

		this.targetScroll = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
	
		buf.writeDouble(this.targetScroll);
	}
	
	public static class Handler implements IMessageHandler<MarketCartScrollPacket, IMessage> {

		@Override
		public IMessage onMessage(MarketCartScrollPacket message, MessageContext ctx) {
			
			MarketCartInventory cartInventory = MarketCartCommandHandler.cartInventories.get(ctx.getServerHandler().player);
			
			double rowsCount = Math.ceil((double) cartInventory.getFilledStacksPart() / 9D);
			
			rowsCount -= cartInventory.getSizeInventory()/9;
			
			System.out.println("rowsCount: "+rowsCount);
			System.out.println("targetScroll: "+message.targetScroll);
			System.out.println("(int) (rowsCount*message.targetScroll): "+((int) (rowsCount*message.targetScroll)));
			
			cartInventory.scrollTo((int) (rowsCount*message.targetScroll));
			
			return null;
		}
	}
}
