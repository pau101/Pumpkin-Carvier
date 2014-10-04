package com.pau101.pumpkincarvier.client.render.tileentity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.pau101.pumpkincarvier.PumpkinCarvier;
import com.pau101.pumpkincarvier.client.model.ModelPumpkin;
import com.pau101.pumpkincarvier.client.render.TexturePumpkin;
import com.pau101.pumpkincarvier.pumpkin.PumpkinFace;
import com.pau101.pumpkincarvier.tileentity.TileEntityPumpkin;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class TileEntityPumpkinRenderer extends TileEntitySpecialRenderer {
	public static void deleteTexture(ResourceLocation resourceLocation) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.deleteTexture(resourceLocation);
		Map<ResourceLocation, ITextureObject> mapTextureObjects = ReflectionHelper.getPrivateValue(TextureManager.class, textureManager, PumpkinCarvier.F_MAP_TEXTURE_OBJECTS);
		mapTextureObjects.remove(resourceLocation);
	}

	public static ResourceLocation getTexture(PumpkinFace data) {
		if (faceMap.containsKey(data)) {
			return faceMap.get(data);
		} else {
			Iterator<PumpkinFace> faceIterator = faceMap.keySet().iterator();
			while (faceIterator.hasNext()) {
				PumpkinFace face = faceIterator.next();
				if (face.host == null || face.host.isInvalid() || face.unused) {
					deleteTexture(faceMap.get(face));
					faceIterator.remove();
				}
			}
			ResourceLocation resourceLocation = new ResourceLocation(PumpkinCarvier.MODID, "pumpkin_block/" + String.valueOf(data.hashCode()));
			Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, new TexturePumpkin(data));
			faceMap.put(data, resourceLocation);
			return resourceLocation;
		}
	}

	public static Map<PumpkinFace, ResourceLocation> faceMap = new HashMap<PumpkinFace, ResourceLocation>();

	private ModelPumpkin model;

	public TileEntityPumpkinRenderer() {
		model = new ModelPumpkin();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		TileEntityPumpkin pumpkin = (TileEntityPumpkin) tileEntity;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		PumpkinFace[] faces = pumpkin.getFaces();
		for (int i = 0; i < faces.length; i++) {
			PumpkinFace face = faces[i];
			bindTexture(getTexture(face));
			model.renderSide(i);
		}
		GL11.glPopMatrix();
	}
}
