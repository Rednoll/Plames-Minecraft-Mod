package com.inwaiders.plames.integration.minecraft.accessor.inventory.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;
import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.commands.handlers.MarketBuyCommandHandler;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.container.MarketBuyContainer;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketBuyRequest;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketBuySearchRequest;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketBuyViewOfferRequest;
import com.inwaiders.plames.integration.minecraft.accessor.network.ReCraftNetworkWrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class MarketBuyGui extends GuiContainer {

	private static final ResourceLocation GUI_TEXTURE_INVENTORY = new ResourceLocation(ReCraftAccessor.MODID, "textures/gui/market_buy_inv.png");
	private static final ResourceLocation GUI_TEXTURE_SEARCH = new ResourceLocation(ReCraftAccessor.MODID, "textures/gui/market_buy_search.png");
	private static final ResourceLocation GUI_TEXTURE_MAIN = new ResourceLocation(ReCraftAccessor.MODID, "textures/gui/market_buy_main.png");
    
	private GuiTextField searchField = null;
	private GuiTextField quantityField= null;
	
	private int searchPageSize = 5;
	private int searchPageNumber = 0;
	
	private List<JsonObject> currentOffers = new ArrayList<>();
	private Map<GuiButton, Integer> offersButtons = new HashMap<>();
	
	private JsonObject currentOffer = null;
	
	private GuiButton buyButton = null;
	
	public MarketBuyGui(IInventory buyInventory) {
		super(new MarketBuyContainer(buyInventory, Minecraft.getMinecraft().player));
		
		this.xSize = 384;
		this.ySize = 256;
		
		this.allowUserInput = true;
	}

	public void updateScreen() {
		super.updateScreen();
		
		searchField.updateCursorCounter();
	}
	
	@Override
	public void initGui() {
		super.initGui();
	
		this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        
		searchField = new GuiTextField(0, this.fontRenderer, guiLeft - 25 + 8, guiTop + 8, 100, this.fontRenderer.FONT_HEIGHT + 4);
			searchField.setMaxStringLength(32);
			searchField.setFocused(true);
			searchField.setTextColor(Color.WHITE.getRGB());
			
		for(int i = 0; i < searchPageSize; i++) {
			
			GuiButton searchButton = new GuiButton(this.buttonList.size(), searchField.x, searchField.y + searchField.height + 8 + 20*i + 4*i, "---");
				searchButton.setWidth(100);
	
			this.addButton(searchButton);
			offersButtons.put(searchButton, i);
		}
		
		buyButton = new GuiButton(this.buttonList.size(), guiLeft + 111 + 162 - 45, 0, "Buy");
			buyButton.setWidth(45);
		
		this.addButton(buyButton);
		
		quantityField = new GuiTextField(1, this.fontRenderer, buyButton.x - 40 - 5, 0, 40, this.fontRenderer.FONT_HEIGHT + 4);
			quantityField.setMaxStringLength(4);
			quantityField.setTextColor(Color.WHITE.getRGB());
			quantityField.setText("1");
			quantityField.height = buyButton.height - 2;
	
		//
		ReCraftNetworkWrapper.sendToServer(new MarketBuySearchRequest("", 0, searchPageSize));
	}
	
	protected void actionPerformed(GuiButton button) throws IOException {
		
		if(offersButtons.containsKey(button)) {

			int index = offersButtons.get(button);
			
			if(currentOffers.size() > index) {
				
				JsonObject offer = currentOffers.get(index);
				
				viewOffer(offer);
			}
		}
		
		if(buyButton == button) {

			if(currentOffer != null) {
			
				ReCraftNetworkWrapper.sendToServer(new MarketBuyRequest(currentOffer.get("name").getAsString(), Integer.valueOf(quantityField.getText())));
			}
		}
	}
	
	private void viewOffer(JsonObject offer) {
	
		/*
		JsonArray jsonItemStacks = offer.get("item_stacks").getAsJsonArray();
		
		MarketBuyInventory inventory = MarketBuyCommandHandler.clientInventory;
		
		inventory.clear();
	
		for(JsonElement element : jsonItemStacks) {
			
			JsonObject jsonItemStack = new Gson().fromJson((element.getAsJsonObject().toString()), JsonObject.class);
			
			jsonItemStack.add("item", new Gson().fromJson((jsonItemStack.get("item").getAsJsonObject().get("metadata").getAsString()), JsonObject.class));
			
			ItemStack is = MarketDataUtils.fromMarketItemStack(jsonItemStack);
		
			while(is.getCount() > 0) {
			
				is = inventory.addItem(is);
			}
		}
		*/
		
		this.currentOffer = offer;
		
		ReCraftNetworkWrapper.sendToServer(new MarketBuyViewOfferRequest(offer.get("id").getAsLong()));
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		searchField.textboxKeyTyped(typedChar, keyCode);
		
		if("0123456789".contains(String.valueOf(typedChar))) { // Only arabic! (Character.isDigit - invalid)
			
			quantityField.textboxKeyTyped(typedChar, keyCode);
		}
		
		if(this.searchField.isFocused()) {
			
			String text = this.searchField.getText();
		
			if(text != null && !text.isEmpty()) {
				
				ReCraftNetworkWrapper.sendToServer(new MarketBuySearchRequest(text, searchPageNumber, searchPageSize));
			}
			else {
				
				ReCraftNetworkWrapper.sendToServer(new MarketBuySearchRequest("", 0, searchPageSize));
			}
		}
		
		if(!(keyCode == Keyboard.KEY_E && this.searchField.isFocused())) {
			super.keyTyped(typedChar, keyCode);
		}
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	
		this.drawDefaultBackground();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		super.mouseClicked(x, y, btn);
		
		searchField.mouseClicked(x, y, btn);
		quantityField.mouseClicked(x, y, btn);
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    	
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		
		int xBegin = (this.width - this.xSize) / 2;
        int yBegin = (this.height - this.ySize) / 2;
        
        //Inventory
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE_INVENTORY);
		
			this.drawTexturedModalRect(xBegin + 104, yBegin + 166, 0, 0, 176, 90);
        
        //Search
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE_SEARCH);
        	
			this.drawTexturedModalRect(xBegin - 25, yBegin, 0, 0, 116, 154);
       
        //Main
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE_MAIN);
		
	        this.drawTexturedModalRect(xBegin + 104, yBegin, 0, 0, 176, 154);
	        
	        int numRows = (int) Math.ceil((double)MarketBuyCommandHandler.clientInventory.getFilledStacks()/9D);
	        
	        for(int i = 0; i < numRows; i++) {
	        	
	        	this.drawTexturedModalRect(xBegin + 111, yBegin + 20 + i*18, 0, 154, 162, 18);
	        }
	        
	        buyButton.y = guiTop + 24 + numRows*18 + 5;
	        quantityField.y = buyButton.y + 1;
	        
		searchField.drawTextBox();
		quantityField.drawTextBox();
		
		if(currentOffer != null) {
			
			this.fontRenderer.drawString(currentOffer.get("name").getAsString(), xBegin + 111, yBegin + 8, 4210752);
		}
		
		for(Entry<GuiButton, Integer> entry : offersButtons.entrySet()) {
			
			GuiButton offerButton = entry.getKey();
			int index = entry.getValue();
			
			if(currentOffers.size() > index) {
			
				offerButton.displayString = currentOffers.get(index).get("name").getAsString();
			}
			else {
				
				offerButton.displayString = "---";
			}
		}
	}
	
	public List<JsonObject> getCurrentOffers() {
	
		return this.currentOffers;
	}
}
