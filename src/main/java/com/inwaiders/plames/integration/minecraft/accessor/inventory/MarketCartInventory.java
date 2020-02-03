package com.inwaiders.plames.integration.minecraft.accessor.inventory;

import java.util.ArrayList;
import java.util.List;

import com.inwaiders.plames.integration.minecraft.accessor.server.network.plames.ReCraftHttpConnector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MarketCartInventory extends InventoryBasic {

	private volatile EntityPlayer player = null;
	
	private volatile List<Long> itemStacksIds = null;
	
	private List<ItemStack> allItemStacks;
	
	private int currentBeginRowIndex = 0;
	
	private int allStacksCount = 0;

	public MarketCartInventory(EntityPlayer player) {
		super("", true, 6*9);
		
		this.player = player;

		allItemStacks = new ArrayList<>();
		
		itemStacksIds = new ArrayList<Long>();
	}
	
	public int getFilledStacksPart() {
		
		return allItemStacks.size();
	}
	
	public void scrollTo(int beginRowIndex) {
		
		for(int y = 0; y < 6 ; y++) {
		
			for(int x = 0; x < 9 ; x++) {
				
				if(allItemStacks.size() > (y+beginRowIndex)*9 + x) {
					
					super.setInventorySlotContents(y*9 + x, allItemStacks.get((y+beginRowIndex)*9 + x));
				}
				else {
					
					super.setInventorySlotContents(y*9 + x, ItemStack.EMPTY);
				}
			}
		}
		
		this.currentBeginRowIndex = beginRowIndex;
	}
	
	public ItemStack spreadStack(ItemStack input, long stackId) {

		for(ItemStack container : allItemStacks) {
			
			if(input.getCount() == 0) return input;
			
			if(container.isItemEqual(input) && ItemStack.areItemStackTagsEqual(container, input)) {
				
				addItemStack(container, input, stackId);
			}
		}
		
		int lastCount = 0;
		
		while(lastCount != input.getCount()) {
		
			lastCount = input.getCount();
			
			addItemStack(null, input, stackId);
		}
		
		return input;
	}
	
	public void addItemStack(ItemStack container, ItemStack is, long stackId) {
		
		if(is.isEmpty()) return;
		
		int stackLimit = getInventoryStackLimit();
		
		if(container == null || container.isEmpty()) {
			
			allItemStacks.add(is.splitStack(stackLimit));
			itemStacksIds.add(stackId);
		}
		else {
			
			if(container.isItemEqual(is) && ItemStack.areItemStackTagsEqual(container, is)) {
			
				if(container.getCount() + is.getCount() <= stackLimit) {
					
					container.setCount(container.getCount()+is.getCount());
					is.setCount(0);
				}
				else {
					
					int needToFull = stackLimit - container.getCount();
					container.setCount(stackLimit);
					is.setCount(is.getCount()-needToFull);
				}
			}
		}
	}
	
	public void setAllStacksCount(int i) {
		
		this.allStacksCount = i;
	}
	
	public int getAllStacksCount() {
	
		return this.allStacksCount;
	}
	
    @SideOnly(Side.SERVER)
	@Override
	public ItemStack decrStackSize(int index, int count) {
    
		ItemStack is = super.decrStackSize(index, count);
    
		int idIndex = (currentBeginRowIndex*9)+index;
		
		if(itemStacksIds.size() > idIndex && itemStacksIds.get(idIndex) != -1) {
			
			ReCraftHttpConnector.decrItemStackSizeFromMarketCart(player, itemStacksIds.get(idIndex), is.getCount());
			itemStacksIds.set(idIndex, -1L);
			allItemStacks.remove(idIndex);
		}

		return is;
	}
	
    @SideOnly(Side.SERVER)
	@Override
	public ItemStack removeStackFromSlot(int index) {
    	
		ItemStack is = super.removeStackFromSlot(index);
	    
		int idIndex = (currentBeginRowIndex*9)+index;
		
		if(itemStacksIds.size() > idIndex && itemStacksIds.get(idIndex) != -1) {
			
			ReCraftHttpConnector.decrItemStackSizeFromMarketCart(player, itemStacksIds.get(idIndex), is.getCount());
			itemStacksIds.set(idIndex, -1L);
			allItemStacks.remove(idIndex);
		}
		
		return is;
    }
	
    @SideOnly(Side.SERVER)
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		
    	int idIndex = (currentBeginRowIndex*9)+index;
		
		if(stack.getItem() == Items.AIR && itemStacksIds.size() > idIndex && itemStacksIds.get(idIndex) != -1) {
			
			ReCraftHttpConnector.decrItemStackSizeFromMarketCart(player, itemStacksIds.get(idIndex), getStackInSlot(index).getCount());
			allItemStacks.remove(idIndex);
		}
		
		super.setInventorySlotContents(index, stack);
		
		if(itemStacksIds.size() > idIndex && itemStacksIds.get(idIndex) != -1) {
			
			itemStacksIds.set(idIndex, -1L);
		}
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
