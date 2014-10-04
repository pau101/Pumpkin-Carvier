package com.pau101.pumpkincarvier.client.render;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.util.Arrays;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

public class TexturePumpkinBlur extends AbstractTexture {
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	private static final int RESOLUTION = 256;
	private static final int BLUR_RADIUS = (int) (RESOLUTION * 0.203125f);
	private static final int OFFSET = (BLUR_RADIUS - 1) / 2;
	private static final float[] BLUR_WEIGHTS;
	private static final ConvolveOp convolve;
	static {
		int weightCount = BLUR_RADIUS * BLUR_RADIUS;
		BLUR_WEIGHTS = new float[weightCount];
		float weight = 1f / weightCount;
		Arrays.fill(BLUR_WEIGHTS, weight);
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		convolve = new ConvolveOp(new Kernel(BLUR_RADIUS, BLUR_RADIUS, BLUR_WEIGHTS), ConvolveOp.EDGE_NO_OP, hints);
	}

	private byte[] data;

	public TexturePumpkinBlur(byte[] data) {
		this.data = data;
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		deleteGlTexture();
		BufferedImage image = new BufferedImage(RESOLUTION + BLUR_RADIUS, RESOLUTION + BLUR_RADIUS, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		g.setColor(TRANSPARENT);
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				if (data[x + y * 16] == 1) {
					g.fillRect((15 - x) * (RESOLUTION / 16) + OFFSET, y * (RESOLUTION / 16) + OFFSET, RESOLUTION / 16, RESOLUTION / 16);
				}
			}
		}

		image = convolve.filter(image, null);

		BufferedImage finalImage = new BufferedImage(RESOLUTION, RESOLUTION, BufferedImage.TYPE_INT_ARGB);
		g = finalImage.createGraphics();
		AffineTransform transform = new AffineTransform();
		transform.translate(-OFFSET - 1, -OFFSET - 1);
		g.drawRenderedImage(image, transform);

		TextureUtil.uploadTextureImage(getGlTextureId(), finalImage);
	}
}
