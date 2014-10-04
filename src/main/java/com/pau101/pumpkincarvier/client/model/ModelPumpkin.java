package com.pau101.pumpkincarvier.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelPumpkin extends ModelBase {
	public ModelRenderer top;
	public ModelRenderer front;
	public ModelRenderer left;
	public ModelRenderer right;
	public ModelRenderer back;
	public ModelRenderer bottom;

	private ModelRenderer[] faces;

	public ModelPumpkin() {
		textureWidth = 16;
		textureHeight = 16;

		top = new ModelRenderer(this);
		top.addBox(0, 16, 0, 16, 0, 16);
		front = new ModelRenderer(this);
		front.rotateAngleZ = 180 * 0.0174532925f;
		front.offsetX = 1;
		front.offsetY = 1;
		front.addBox(0, 0, 0, 16, 16, 0);
		left = new ModelRenderer(this);
		left.rotateAngleX = 180 * 0.0174532925f;
		left.offsetZ = 1;
		left.offsetY = 1;
		left.addBox(0, 0, 0, 0, 16, 16);
		right = new ModelRenderer(this);
		right.rotateAngleX = 180 * 0.0174532925f;
		right.offsetZ = 1;
		right.offsetY = 1;
		right.addBox(16, 0, 0, 0, 16, 16);
		back = new ModelRenderer(this);
		back.rotateAngleZ = 180 * 0.0174532925f;
		back.offsetX = 1;
		back.offsetY = 1;
		back.addBox(0, 0, 16, 16, 16, 0);
		bottom = new ModelRenderer(this);
		bottom.addBox(0, 0, 0, 16, 0, 16);

		faces = new ModelRenderer[] { bottom, top, front, back, left, right };
	}

	public void renderSide(int side) {
		faces[side].render(0.0625f);
	}
}
