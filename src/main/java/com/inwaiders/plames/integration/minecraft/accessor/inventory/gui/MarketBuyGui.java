package com.inwaiders.plames.integration.minecraft.accessor.inventory.gui;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.container.MarketBuyContainer;

import net.minecraft.client.Minecraft;
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
        
		searchField = new GuiTextField(0, this.fontRenderer, guiLeft + 40 - 65 + 8, guiTop + 8, 100, this.fontRenderer.FONT_HEIGHT + 4);
			searchField.setMaxStringLength(32);
			searchField.setCanLoseFocus(false);
			searchField.setFocused(true);
			searchField.setTextColor(Color.WHITE.getRGB());
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		searchField.textboxKeyTyped(typedChar, keyCode);
		
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
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    	
//		this.fontRenderer.drawString(this.marketInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
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
        this.drawTexturedModalRect(xBegin + 40 - 65, yBegin, 0, 0, 116, 154);
       
        //Main
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE_MAIN);
        this.drawTexturedModalRect(xBegin + 169 - 65, yBegin, 0, 0, 176, 154);
        
		searchField.drawTextBox();
	}
}
