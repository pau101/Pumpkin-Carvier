package com.pau101.pumpkincarvier.client.render;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import com.pau101.pumpkincarvier.PumpkinCarvier;
import com.pau101.pumpkincarvier.pumpkin.PumpkinFace;

public class TexturePumpkin extends AbstractTexture {
	private PumpkinFace face;

	private static final ResourceLocation horizontal = new ResourceLocation(PumpkinCarvier.MODID, "textures/entity/pumpkin/horizontal.png");
	private static final ResourceLocation vertical = new ResourceLocation(PumpkinCarvier.MODID, "textures/entity/pumpkin/vertical.png");

	private static final int BACK = 0x441300;
	private static final int WALL_SHADOW = 0x2D0003;
	private static final int LIT_WALL_SHADOW = 0x5D3A14;
	private static final int DISCOLORATION = 0xA05A0B;
	private static final int INTERNAL_LIGHT = 0xEDEA47;
	private static final int INTERNAL_LIGHT_FADE = 0xB7AD43;
	private static final int EXTERNAL_LIGHT = 0xF9FF3A;

	public TexturePumpkin(PumpkinFace face) {
		this.face = face;
	}

	private int blend(int i1, int i2, float t) {
		t = Math.min(t, 1);
		int a1 = i1 >> 24 & 0xff;
		int r1 = (i1 & 0xff0000) >> 16;
		int g1 = (i1 & 0xff00) >> 8;
		int b1 = i1 & 0xff;

		int a2 = i2 >> 24 & 0xff;
		int r2 = (i2 & 0xff0000) >> 16;
		int g2 = (i2 & 0xff00) >> 8;
		int b2 = i2 & 0xff;

		int a = (int) (a1 + (a2 - a1) * t);
		int r = (int) (r1 + (r2 - r1) * t);
		int g = (int) (g1 + (g2 - g1) * t);
		int b = (int) (b1 + (b2 - b1) * t);

		return a << 24 | r << 16 | g << 8 | b;
	}

	private BufferedImage getImage(IResourceManager resourceManger, ResourceLocation resourceLocation) throws IOException {
		InputStream inputstream = resourceManger.getResource(resourceLocation).getInputStream();
		BufferedImage image = ImageIO.read(inputstream);
		BufferedImage direct = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		direct.setData(image.getData());
		return direct;
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		deleteGlTexture();
		BufferedImage bufferedImage;

		bufferedImage = getImage(resourceManager, face.isVertical() ? vertical : horizontal);
		int[] pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

		byte[] data = face.getData();
		boolean lit = face.isLit();
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				if (data[x + y * 16] != 0) {
					if (x > 0 && y > 0 && data[x - 1 + (y - 1) * 16] != 0) {
						pixels[x + y * 16] = lit ? blend(INTERNAL_LIGHT, INTERNAL_LIGHT_FADE, MathHelper.sqrt_float((x - 8) * (x - 8) + (y - 8) * (y - 8)) / 8) : BACK;
					} else {
						pixels[x + y * 16] = lit ? LIT_WALL_SHADOW : WALL_SHADOW;
					}
				} else {
					if (x < 15 && data[x + 1 + y * 16] != 0 || x > 0 && data[x - 1 + y * 16] != 0) {
						pixels[x + y * 16] = DISCOLORATION;
					}
					if (lit) {
						float exposure = ((x > 0 ? data[x - 1 + y * 16] : 0) + (y > 0 ? data[x + (y - 1) * 16] : 0) + (x < 15 ? data[x + 1 + y * 16] : 0) + (y < 15 ? data[x + (y + 1) * 16] : 0)) / 4f;
						if (exposure > 0) {
							pixels[x + y * 16] = blend(pixels[x + y * 16], EXTERNAL_LIGHT, exposure * 0.5f + 0.25f);
						}
					}
				}
			}
		}

		TextureUtil.uploadTextureImage(getGlTextureId(), bufferedImage);
	}
}
