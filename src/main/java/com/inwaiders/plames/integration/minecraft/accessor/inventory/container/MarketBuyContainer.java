package com.inwaiders.plames.integration.minecraft.accessor.inventory.container;

import com.inwaiders.plames.integration.minecraft.accessor.inventory.MarketBuyInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class MarketBuyContainer extends Container {

	private int numRows = 0;
	
	public MarketBuyContainer(IInventory buyInv, EntityPlayer player) {
		
		IInventory playerInv = player.inventory;
		
        this.numRows = (int) ((double) buyInv.getSizeInventory() / 9D);
        
        buyInv.openInventory(player);
        
        for(int y = 0; y < this.numRows; y++) {
        	
        	for(int x = 0; x < 9; x++) {
        		
        		this.addSlotToContainer(new MarketBuySlot(buyInv, x + y * 9, 112 + x * 18, 17 + y * 18));
        	}
        }
        
        for(int y = 0; y < 3; y++) {
        	
        	for(int x = 0; x < 9; x++) {
            	
            	this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 112 + x * 18, 174 + y * 18));
            }
        }

        for(int x = 0; x < 9; x++) {
        	
            this.addSlotToContainer(new Slot(playerInv, x, 112 + x * 18, 232));
        }
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		
		return true;
	}
}
