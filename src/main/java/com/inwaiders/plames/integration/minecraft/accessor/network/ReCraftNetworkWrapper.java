package com.inwaiders.plames.integration.minecraft.accessor.network;

import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketCartScrollPacket;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketCartSyncPacket;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketCartSyncRequestPacket;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ReCraftNetworkWrapper {

	public static final SimpleNetworkWrapper snw = NetworkRegistry.INSTANCE.newSimpleChannel(ReCraftAccessor.MODID);
	
	public static void init() {
		
		snw.registerMessage(MarketCartSyncPacket.Handler.class, 
				MarketCartSyncPacket.class, 0, Side.CLIENT);
		
		snw.registerMessage(MarketCartSyncRequestPacket.Handler.class, 
				MarketCartSyncRequestPacket.class, 1, Side.SERVER);
		
		snw.registerMessage(MarketCartScrollPacket.Handler.class, 
				MarketCartScrollPacket.class, 2, Side.SERVER);
	}
	
	public static void sendToServer(IMessage message) {
		
		snw.sendToServer(message);
	}
	
	public static void sendTo(EntityPlayerMP player, IMessage message) {
		
		snw.sendTo(message, player);
	}
}
