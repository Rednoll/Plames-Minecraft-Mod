package com.inwaiders.plames.integration.minecraft.accessor.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class MarketBuyContainer extends Container {

	private int numRows = 0;
	
	public MarketBuyContainer(IInventory marketInv, EntityPlayer player) {
		
		IInventory playerInv = player.inventory;
		
        this.numRows = 1;
        marketInv.openInventory(player);

        /*
        for(int y = 0; y < this.numRows; y++) {
        	
        	for(int x = 0; x < 9; x++) {
            	
            	this.addSlotToContainer(new MarketCartSlot(marketInv, x + y * 9, 8 + x * 18, 18 + y * 18));
	        }
        }
        */
        
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
			
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if(slot != null && slot.getHasStack()) {
			
        	ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if(index < this.numRows * 9) {
            	
                if(!this.mergeItemStack(itemStack1, this.numRows * 9, this.inventorySlots.size(), true)) {
                	
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.mergeItemStack(itemStack1, 0, this.numRows * 9, false)) {
            	
                return ItemStack.EMPTY;
            }

            if(itemStack1.isEmpty()) {
            	
                slot.putStack(ItemStack.EMPTY);
            }
            else {
            	
                slot.onSlotChanged();
            }
        }

        return itemStack;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		
		return true;
	}
}
