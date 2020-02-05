package com.inwaiders.plames.integration.minecraft.accessor.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class MarketBuySlot extends Slot {

	public MarketBuySlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	
	}
	
	@Override
	public void onSlotChanged() {
		// Nothing
	}
	
	@Override
	public boolean isEnabled() {
		
		return this.getHasStack();
	}

    public boolean canTakeStack(EntityPlayer playerIn) {
    	
        return false;
    }
    
    public boolean isItemValid(ItemStack stack){
    	
        return false;
    }
}
