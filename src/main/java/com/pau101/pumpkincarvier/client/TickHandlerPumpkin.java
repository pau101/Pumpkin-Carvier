package com.pau101.pumpkincarvier.client;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;

import com.pau101.pumpkincarvier.PumpkinCarvier;
import com.pau101.pumpkincarvier.block.BlockPumpkin;
import com.pau101.pumpkincarvier.client.render.TexturePumpkinBlur;
import com.pau101.pumpkincarvier.proxy.ClientProxy;
import com.pau101.pumpkincarvier.util.Reflection;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TickHandlerPumpkin {
	private int pumpkinFaceHashCode = -1;
	private ResourceLocation pumpkinBlurTexPath = null;

	private Minecraft mc = Minecraft.getMinecraft();

	public boolean zoom = false;

	public boolean zoomComplete = true;
	public int zoomTimer;
	public double zoomValue = 1.0;
	public float defaultSens = 0.5f;
	public float maxZoom = 3;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			if (!zoomComplete) {
				zoomTimer++;
			}
			boolean prevZoom = zoom;
			zoom = mc.theWorld != null && mc.thePlayer != null && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK && mc.theWorld.getBlock(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ) instanceof BlockPumpkin && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() == Items.shears;
			if (zoom != prevZoom) {
				if (zoom && zoomComplete) {
					defaultSens = mc.gameSettings.mouseSensitivity;
				}
				zoomComplete = false;
				if (zoomTimer > 0) {
					zoomTimer = 10 - zoomTimer;
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onTick(TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			if (player != null) {
				ItemStack itemStack = player.inventory.armorInventory[3];
				if (itemStack != null && itemStack.getItem() == PumpkinCarvier.item_pumpkin) {
					if (itemStack.hasTagCompound()) {
						final NBTTagCompound faceCompound = itemStack.getTagCompound().getCompoundTag("block").getTagList("faces", 10).getCompoundTagAt(5);
						if (faceCompound.hasKey("hashCode", 3)) {
							final int pumpkinFaceHashCode = faceCompound.getInteger("hashCode");
							if (this.pumpkinFaceHashCode != pumpkinFaceHashCode) {
								this.pumpkinFaceHashCode = pumpkinFaceHashCode;
								try {
									pumpkinBlurTexPath = new ResourceLocation(PumpkinCarvier.MODID, "pumpkin_blur");
									Minecraft.getMinecraft().getTextureManager().loadTexture(pumpkinBlurTexPath, new TexturePumpkinBlur(Base64.decode(faceCompound.getString("data"))));
									Field field = Reflection.getField(GuiIngame.class, PumpkinCarvier.F_PUMPKIN_BLUR_TEX_PATH);
									Reflection.setModifier(field, Modifier.FINAL, false);
									Reflection.setField(field, null, pumpkinBlurTexPath);
								} catch (Base64DecodingException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			if (!zoomComplete) {
				if (zoom) {
					zoomValue = 1 + (maxZoom - 1) / 10d * zoomTimer + (maxZoom - 1) / 10d * ClientProxy.mcTimer.renderPartialTicks;
					float defSense = defaultSens;
					mc.gameSettings.mouseSensitivity = defSense - (float) ((zoomValue - 1) / (maxZoom - 1) * (0.84 * defSense));
					if (zoomValue > maxZoom) {
						zoomValue = maxZoom;
						zoomComplete = true;
						zoomTimer = 0;
					}
				} else {
					zoomValue = maxZoom - (maxZoom - 1) / 10d * zoomTimer - (maxZoom - 1) / 10d * ClientProxy.mcTimer.renderPartialTicks;
					float defSense = defaultSens;
					mc.gameSettings.mouseSensitivity = defSense - (float) ((zoomValue - 1) / (maxZoom - 1) * (0.84 * defSense));
					if (zoomValue < 1) {
						zoomValue = 1;
						zoomComplete = true;
						zoomTimer = 0;
						mc.gameSettings.mouseSensitivity = defaultSens;
					}
				}
				Reflection.setValue(EntityRenderer.class, mc.entityRenderer, zoomValue, PumpkinCarvier.F_CAMERA_ZOOM);
			}
		}
	}
}
