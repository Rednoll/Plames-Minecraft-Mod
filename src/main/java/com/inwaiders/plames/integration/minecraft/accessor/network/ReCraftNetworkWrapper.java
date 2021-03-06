package com.inwaiders.plames.integration.minecraft.accessor.network;

import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketBuyRequest;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketBuySearchRequest;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketBuySearchResponse;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketBuyViewOfferRequest;
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
		
		snw.registerMessage(MarketBuySearchRequest.Handler.class, 
				MarketBuySearchRequest.class, 3, Side.SERVER);
		
		snw.registerMessage(MarketBuySearchResponse.Handler.class, 
				MarketBuySearchResponse.class, 4, Side.CLIENT);
		
		snw.registerMessage(MarketBuyViewOfferRequest.Handler.class, 
				MarketBuyViewOfferRequest.class, 5, Side.SERVER);
	
		snw.registerMessage(MarketBuyRequest.Handler.class, 
				MarketBuyRequest.class, 6, Side.SERVER);
	}
	
	public static void sendToServer(IMessage message) {
		
		snw.sendToServer(message);
	}
	
	public static void sendTo(EntityPlayerMP player, IMessage message) {
		
		snw.sendTo(message, player);
	}
}
