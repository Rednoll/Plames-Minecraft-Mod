package com.inwaiders.plames.integration.minecraft.accessor.inventory.gui;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import com.inwaiders.plames.integration.minecraft.accessor.ReCraftAccessor;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.MarketCartInventory;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.container.MarketCartContainer;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketCartScrollPacket;
import com.inwaiders.plames.integration.minecraft.accessor.inventory.sync.MarketCartSyncRequestPacket;
import com.inwaiders.plames.integration.minecraft.accessor.network.ReCraftNetworkWrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MarketCartGui extends GuiContainer {
	
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ReCraftAccessor.MODID, "textures/gui/market_cart.png");
    private final MarketCartInventory marketInventory;
    private final int inventoryRows;

    /** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
    private float currentScroll;
    /** True if the scrollbar is being dragged */
    private boolean isScrolling;
    /** True if the left mouse button was held down last time drawScreen was called. */
    private boolean wasClicking;
    
	public MarketCartGui(MarketCartInventory marketInventory) {
		super(new MarketCartContainer(marketInventory, Minecraft.getMinecraft().player));
        
		this.marketInventory = marketInventory;
		this.allowUserInput = false;
        
		this.inventoryRows = 6;
		this.ySize = 114 + this.inventoryRows * 18;
		this.xSize = 193;
		
		ReCraftNetworkWrapper.sendToServer(new MarketCartSyncRequestPacket());
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    	
		this.fontRenderer.drawString(this.marketInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
    }

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
    	
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		
		int xBegin = (this.width - this.xSize) / 2;
        int yBegin = (this.height - this.ySize) / 2;
        
        this.drawTexturedModalRect(xBegin, yBegin, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(xBegin, yBegin + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    
        this.drawTexturedModalRect(xBegin+174, yBegin+18 + (91*this.currentScroll), 194, 0, 12, 15);
	}
	
	@Override
    public void handleMouseInput() throws IOException {
		
		super.handleMouseInput();
        int deltaZ = Mouse.getEventDWheel();
        
        if(deltaZ != 0 && this.needsScrollBars()) {
        	
        	double rowsToScroll = (double) this.marketInventory.getAllStacksCount()/9D - this.inventoryRows;

        	if(deltaZ > 0) {
        		
        		deltaZ = 1;
        	}

            if(deltaZ < 0) {
            	
            	deltaZ = -1;
            }

            this.currentScroll = (float)((double)this.currentScroll - (double)deltaZ / rowsToScroll);

            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            
            ReCraftNetworkWrapper.sendToServer(new MarketCartScrollPacket(this.currentScroll));
//          ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
        }
    }
	
	private boolean needsScrollBars() {
		
		return this.marketInventory.getAllStacksCount() > this.inventoryRows*9;
	}
}