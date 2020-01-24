package com.inwaiders.plames.integration.minecraft.accessor.inventory;

import java.util.ArrayList;
import java.util.List;

import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.ReCraftHttpConnector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MarketCartInventory extends InventoryBasic {

	private volatile EntityPlayer player = null;
	
	private volatile long[] itemStacksIds = new long[getSizeInventory()];
	
	public MarketCartInventory(EntityPlayer player) {
		super("", true, 6*9);
		
		this.player = player;
		
		for(int i = 0; i < itemStacksIds.length; i++) {
			
			itemStacksIds[i] = -1;
		}
	}

	public ItemStack spreadStack(ItemStack input, long stackId) {

		for(int i = 0;i<getSizeInventory();i++) {
			
			if(input.getCount() == 0) return input;
			
			if(getStackInSlot(i).isItemEqual(input) && ItemStack.areItemStackTagsEqual(getStackInSlot(i), input)) {
				
				addItemStack(i, input, stackId);
			}
		}

		for(int i = 0;i<getSizeInventory();i++) {
			
			if(input.getCount() == 0) return input;
			
			if(getStackInSlot(i).isEmpty()) {
				
				addItemStack(i, input, stackId);
			}
		}
		
		return input;
	}
	
	public int getEmptySlot() {
		
		for(int i = 0;i<getSizeInventory();i++) {
		
			if(getStackInSlot(i).isEmpty()) {
				
				return i;
			}
		}
		
		return -1;
	}
	
	public ItemStack addItemStack(int index, ItemStack is, long stackId) {
		
		int stackLimit = getInventoryStackLimit();
		
		if(getStackInSlot(index) == null || getStackInSlot(index).isEmpty()) {
			
			setInventorySlotContents(index, is.splitStack(stackLimit));
			itemStacksIds[index] = stackId;
		}
		else {
			
			if(getStackInSlot(index).isItemEqual(is) && ItemStack.areItemStackTagsEqual(getStackInSlot(index), is)) {
			
				if(getStackInSlot(index).getCount() + is.getCount() <= stackLimit) {
					
					getStackInSlot(index).setCount(getStackInSlot(index).getCount()+is.getCount());
					is.setCount(0);
					return is;
				}
				else {
					
					int needToFull = stackLimit - getStackInSlot(index).getCount();
					getStackInSlot(index).setCount(stackLimit);
					is.setCount(is.getCount()-needToFull);
				}
			}
			else {
				
				return is;
			}
		}
		
		return is;
	}
	
    @SideOnly(Side.SERVER)
	@Override
	public ItemStack decrStackSize(int index, int count) {
    
		ItemStack is = super.decrStackSize(index, count);
    
		if(itemStacksIds[index] != -1) {
			
			ReCraftHttpConnector.decrItemStackSizeFromMarketCart(player, itemStacksIds[index], is.getCount());
			this.itemStacksIds[index] = -1;
		}

		return is;
	}
	
    @SideOnly(Side.SERVER)
	@Override
	public ItemStack removeStackFromSlot(int index) {
    	
		ItemStack is = super.removeStackFromSlot(index);
	    
		if(itemStacksIds[index] != -1) {
			
			ReCraftHttpConnector.decrItemStackSizeFromMarketCart(player, itemStacksIds[index], is.getCount());
			this.itemStacksIds[index] = -1;
		}
		
		return is;
    }
	
    @SideOnly(Side.SERVER)
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		
		try {
			
			if(stack.getItem() == Items.AIR && itemStacksIds[index] != -1) {
				
				ReCraftHttpConnector.decrItemStackSizeFromMarketCart(player, itemStacksIds[index], getStackInSlot(index).getCount());
			}
		}
		catch(IndexOutOfBoundsException e) {
			
		}
		
		super.setInventorySlotContents(index, stack);
		this.itemStacksIds[index] = -1;
	}
	
	@Override
	public ItemStack getStackInSlot(int index) {
    	
		return index >= 0 && index < getSizeInventory() ? (ItemStack) super.getStackInSlot(index).copy() : ItemStack.EMPTY;
	}
	
	@Override
	public String getName() {
	
		return "market.cart";
	}

	@Override
	public boolean hasCustomName() {
		
		return true;
	}

	@Override
	public ITextComponent getDisplayName() {
		
		return new TextComponentString("Market Cart");
	}
}
