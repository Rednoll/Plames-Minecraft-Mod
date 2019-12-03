package com.inwaiders.plames.integration.minecraft.accessor;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PlayerUtils {

	public static int getAvailableSpaceForItem(EntityPlayer ep, ItemStack is) {
		
		List<Slot> slots = ep.inventoryContainer.inventorySlots;
		
		int available = 0;
			
		for(Slot slot : slots) {
			
			// *** CRAFT AND SECOND HAND ***
			if(slot.slotNumber == 1) continue;
			if(slot.slotNumber == 2) continue;
			if(slot.slotNumber == 3) continue;
			if(slot.slotNumber == 4) continue;
			if(slot.slotNumber == 45) continue;
			// *** CRAFT AND SECOND HAND ***
			
			if(!slot.getHasStack() && slot.isItemValid(is)) {
				
				available += slot.getSlotStackLimit();
			}
			else if(slot.getStack().getItem() == is.getItem() && slot.isItemValid(is)){
				
				available += slot.getSlotStackLimit() - slot.getStack().getCount();
			}
		}
		
		return available;
	}
}
