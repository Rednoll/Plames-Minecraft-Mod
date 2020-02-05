package com.inwaiders.plames.integration.minecraft.accessor.inventory;

import java.util.ArrayList;
import java.util.List;

import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.ReCraftHttpConnector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MarketBuyInventory extends InventoryBasic {

	private volatile EntityPlayer player = null;

	public MarketBuyInventory(EntityPlayer player, int size) {
		super("market.buy", true, size);
		
		this.player = player;
	}

	public int getFilledStacks() {
		
		int result = 0;
		
		for(int i = 0; i < this.getSizeInventory(); i++) {
			
			if(!getStackInSlot(i).isEmpty()) {
				
				result++;
			}
		}
		
		return result;
	}
	
	@Override
	public boolean hasCustomName() {
		
		return true;
	}

	@Override
	public ITextComponent getDisplayName() {
		
		return new TextComponentString("Market Buy");
	}
}
