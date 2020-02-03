package com.inwaiders.plames.integration.minecraft.accessor.inventory.gui;

import java.io.IOException;

import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.MarketCartInventory;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.container.MarketCartContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class MarketBuyGui extends GuiScreen {

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ReCraftAccessor.MODID, "textures/gui/market_buy.png");
    
	private GuiTextField searchField = null;
	
	private int xSize = 0;
	private int ySize = 0;
	
	public MarketBuyGui() {
		
        
		this.xSize = 100;
		this.ySize = 100;
		
		this.allowUserInput = true;
	}

	public void updateScreen() {
		super.updateScreen();
		
		searchField.updateCursorCounter();
	}
	
	@Override
	public void initGui() {
		super.initGui();
	
		searchField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 68, this.height/2-46, 137, 20);
			searchField.setMaxStringLength(23);
			searchField.setText("Sample Tag");
			searchField.setFocused(true);
	
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		
		searchField.textboxKeyTyped(typedChar, keyCode);
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	
		this.drawDefaultBackground();
		
			searchField.drawTextBox();
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		super.mouseClicked(x, y, btn);
		
		searchField.mouseClicked(x, y, btn);
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    	
//		this.fontRenderer.drawString(this.marketInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
    }

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
    	
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		
		int xBegin = (this.width - this.xSize) / 2;
        int yBegin = (this.height - this.ySize) / 2;
        
        this.drawTexturedModalRect(xBegin, yBegin, 0, 0, this.xSize, this.ySize);
	}
}
