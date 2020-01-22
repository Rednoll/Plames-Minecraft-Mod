package com.inwaiders.plames.integration.minecraft.accessor.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class MarketCartInventory implements IInventory {

	private ItemStack[] itemStacks = new ItemStack[getSizeInventory()];
	
	public MarketCartInventory() {
		
		for(int i = 0;i<itemStacks.length;i++) {
			
			itemStacks[i] = ItemStack.EMPTY;
		}
	}
	
	public ItemStack spreadStack(ItemStack input) {

		for(int i = 0;i<itemStacks.length;i++) {
			
			if(input.getCount() == 0) return input;
			
			if(itemStacks[i].isItemEqual(input) && ItemStack.areItemStackTagsEqual(itemStacks[i], input)) {
				
				addItemStack(i, input);
			}
		}

		for(int i = 0;i<itemStacks.length;i++) {
			
			if(input.getCount() == 0) return input;
			
			if(itemStacks[i] == null || itemStacks[i].isEmpty()) {
				
				addItemStack(i, input);
			}
		}
		
		return input;
	}
	
	public int getEmptySlot() {
		
		for(int i = 0;i<itemStacks.length;i++) {
		
			if(itemStacks[i] == null || itemStacks[i].isEmpty()) {
				
				return i;
			}
		}
		
		return -1;
	}
	
	public ItemStack addItemStack(int index, ItemStack is) {
		
		int stackLimit = getInventoryStackLimit();
		
		if(itemStacks[index] == null || itemStacks[index].isEmpty()) {
			
			itemStacks[index] = is.splitStack(stackLimit);
		}
		else {
			
			if(itemStacks[index].isItemEqual(is) && ItemStack.areItemStackTagsEqual(itemStacks[index], is)) {
			
				if(itemStacks[index].getCount() + is.getCount() <= stackLimit) {
					
					is.setCount(0);
					return is;
				}
				else {
					
					int needToFull = stackLimit - itemStacks[index].getCount();
					itemStacks[index].setCount(stackLimit);
					is.setCount(is.getCount()-needToFull);
				}
			}
			else {
				
				return is;
			}
		}
		
		return is;
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

	@Override
	public int getSizeInventory() {
		
		return 9*6;
	}

	@Override
	public boolean isEmpty() {
		
		for(ItemStack is : itemStacks) {
			
			if(is != null && !is.isEmpty()) {
				
				return false;
			}
		}
		
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {

		return itemStacks[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
	
		ItemStack result = itemStacks[index].splitStack(count);
		
		this.markDirty();
		
		return result;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		
		ItemStack is = itemStacks[index];
		itemStacks[index] = ItemStack.EMPTY;
		
		this.markDirty();
		
		return is;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		
		itemStacks[index] = stack;
	}

	@Override
	public int getInventoryStackLimit() {
		
		return 64;
	}

	@Override
	public void markDirty() {
		
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		
		
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		
		return false;
	}

	@Override
	public int getField(int id) {
		
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
		
	}

	@Override
	public int getFieldCount() {
		
		return 0;
	}

	@Override
	public void clear() {
		
		for(int i = 0;i<itemStacks.length;i++) {
			
			itemStacks[i] = ItemStack.EMPTY;
		}
	}
}
