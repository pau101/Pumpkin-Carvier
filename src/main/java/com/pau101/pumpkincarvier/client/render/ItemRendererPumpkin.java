package com.pau101.pumpkincarvier.client.render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.pau101.pumpkincarvier.PumpkinCarvier;
import com.pau101.pumpkincarvier.client.model.ModelPumpkin;
import com.pau101.pumpkincarvier.client.render.tileentity.TileEntityPumpkinRenderer;
import com.pau101.pumpkincarvier.proxy.ClientProxy;
import com.pau101.pumpkincarvier.pumpkin.PumpkinFace;

public class ItemRendererPumpkin implements IItemRenderer {
	private static class TimedResourceEntry {
		public long time;
		public ResourceLocation resourceLocation;

		public TimedResourceEntry(long time, ResourceLocation resourceLocation) {
			this.time = time;
			this.resourceLocation = resourceLocation;
		}
	}

	private static ResourceLocation getTexture(boolean lit, NBTTagCompound tagCompound) {
		if (tagCompound.hasKey("hashCode", 3)) {
			Integer hashCode = Integer.valueOf(tagCompound.getInteger("hashCode"));
			if (faceMap.containsKey(hashCode)) {
				TimedResourceEntry timedResourceEntry = faceMap.get(hashCode);
				timedResourceEntry.time = System.currentTimeMillis();
				return timedResourceEntry.resourceLocation;
			} else {
				Iterator<Integer> faceIterator = faceMap.keySet().iterator();
				long currentTime = System.currentTimeMillis();
				while (faceIterator.hasNext()) {
					Integer face = faceIterator.next();
					TimedResourceEntry timedResourceEntry = faceMap.get(face);
					if (currentTime - timedResourceEntry.time > 1000) {
						TileEntityPumpkinRenderer.deleteTexture(timedResourceEntry.resourceLocation);
						faceIterator.remove();
					}
				}
				PumpkinFace pumpkinFace = new PumpkinFace();
				pumpkinFace.readFromNBT(tagCompound);
				pumpkinFace.setLit(lit);
				ResourceLocation resourceLocation = new ResourceLocation(PumpkinCarvier.MODID, "pumpkin_item/" + String.valueOf(pumpkinFace.hashCode()));
				Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, new TexturePumpkin(pumpkinFace));
				faceMap.put(hashCode, new TimedResourceEntry(System.currentTimeMillis(), resourceLocation));
				return resourceLocation;
			}
		}
		return null;
	}

	public static Map<Integer, TimedResourceEntry> faceMap = new HashMap<Integer, TimedResourceEntry>();

	private ModelPumpkin model;

	public ItemRendererPumpkin() {
		model = new ModelPumpkin();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type != ItemRenderType.INVENTORY;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		boolean lit = item.getItem() == Item.getItemFromBlock(Blocks.lit_pumpkin);
		TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
		GL11.glPushMatrix();
		if (type == ItemRenderType.ENTITY) {
			EntityItem entityItem = (EntityItem) data[1];
			float heightOscillation = 0;
			float rotation = 0;
			if (!RenderItem.renderInFrame) {
				heightOscillation = MathHelper.sin((entityItem.age + ClientProxy.mcTimer.renderPartialTicks) / 10 + entityItem.hoverStart) * 0.4f - 0.1f;
				rotation = ((entityItem.age + ClientProxy.mcTimer.renderPartialTicks) / 20 + entityItem.hoverStart) * (180 / (float) Math.PI);
			}
			GL11.glRotatef(rotation, 0, 1, 0);
			GL11.glTranslatef(-0.5f, heightOscillation, -0.5f);
		} else {
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		if (item.hasTagCompound()) {
			NBTTagCompound tagCompound = item.getTagCompound();
			if (tagCompound.hasKey("block", 10)) {
				NBTTagList tagList = tagCompound.getCompoundTag("block").getTagList("faces", 10);
				for (int i = 0; i < tagList.tagCount(); i++) {
					NBTTagCompound faceCompound = tagList.getCompoundTagAt(i);
					ResourceLocation resourceLocation = getTexture(lit, faceCompound);
					textureManager.bindTexture(resourceLocation);
					model.renderSide(i);
				}
			}
		}
		GL11.glPopMatrix();
		if (type != ItemRenderType.ENTITY) {
			GL11.glDisable(GL11.GL_CULL_FACE);
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return helper == ItemRendererHelper.EQUIPPED_BLOCK;
	}
}
