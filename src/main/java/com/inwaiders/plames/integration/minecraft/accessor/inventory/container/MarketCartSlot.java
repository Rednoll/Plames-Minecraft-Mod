package com.inwaiders.plames.integration.minecraft.accessor.inventory.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class MarketCartSlot extends Slot {

	public MarketCartSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	
	}

    public boolean isItemValid(ItemStack stack){
    	
        return false;
    }
}
