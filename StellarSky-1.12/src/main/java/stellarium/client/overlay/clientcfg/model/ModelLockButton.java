package stellarium.client.overlay.clientcfg.model;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.basic.ModelSimpleTextured;
import stellarium.StellarSkyResources;

public class ModelLockButton implements IRenderModel {
	
	private static final ModelLockButton instance = new ModelLockButton();
	
	public static ModelLockButton getInstance() {
		return instance;
	}
	
	private ModelSimpleRect selectModel = ModelSimpleRect.getInstance();
	private ModelSimpleTextured clickedModel;
	private ModelSimpleTextured lockTextureModel, unlockTextureModel;
	
	public ModelLockButton() {
		this.clickedModel = new ModelSimpleTextured(StellarSkyResources.clicked);
		this.lockTextureModel = new ModelSimpleTextured(StellarSkyResources.lock);
		this.unlockTextureModel = new ModelSimpleTextured(StellarSkyResources.unlock);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			BufferBuilder worldRenderer, TextureManager textureManager, float[] color) {
		GlStateManager.pushMatrix();
		if(info.equals("select")) {
			GlStateManager.scale(0.9f, 0.9f, 0.9f);
			color[3] *= 0.2f;
			selectModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("locked")) {
			color[1] *= 0.9f;
			color[2] *= 0.9f;
			lockTextureModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else {
			color[1] *= 0.9f;
			color[2] *= 0.9f;
			unlockTextureModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		}
		GlStateManager.popMatrix();
	}

}
