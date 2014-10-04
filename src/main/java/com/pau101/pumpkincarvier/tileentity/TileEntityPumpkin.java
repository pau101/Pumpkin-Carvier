package com.pau101.pumpkincarvier.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import com.pau101.pumpkincarvier.pumpkin.PumpkinFace;

public class TileEntityPumpkin extends TileEntity {
	private PumpkinFace[] faces;

	private boolean lit;

	private boolean reLighting = false;

	private int lastHitSide;

	private boolean prevLit = false;

	public TileEntityPumpkin() {
		faces = new PumpkinFace[6];
		for (int i = 0; i < faces.length; i++) {
			faces[i] = new PumpkinFace(this);
			faces[i].setVertical(i < 2);
		}
	}

	public void carve(int side, int pixelX, int pixelY) {
		faces[side] = faces[side].carve(pixelX, pixelY);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		writeToNBT(tagCompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, tagCompound);
	}

	public PumpkinFace[] getFaces() {
		return faces;
	}

	public int getLastHitSide() {
		return lastHitSide;
	}

	public boolean isLit() {
		return lit;
	}

	public boolean isReLighting() {
		return reLighting;
	}

	private int mod(int a, int b) {
		return (a % b + b) % b;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
	}

	public void readDetailFromNBT(NBTTagCompound tagCompound) {
		NBTTagList tagList = tagCompound.getTagList("faces", 10);
		for (int i = 0; i < tagList.tagCount(); i++) {
			faces[i].unused = true;
			faces[i] = new PumpkinFace(faces[i]);
			faces[i].readFromNBT(tagList.getCompoundTagAt(i));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		readDetailFromNBT(tagCompound);
	}

	public void rotate(int direction) {
		PumpkinFace[] facesClockwise = { faces[2], faces[4], faces[3], faces[5] };
		int[] dirClockwise = { 2, 4, 3, 5 };
		for (int i = 0; i < facesClockwise.length; i++) {
			faces[dirClockwise[mod(i + direction, dirClockwise.length)]] = facesClockwise[i];
		}
		faces[1] = rotate(faces[1], direction > 0 ? direction % 4 : 4 - -direction % 4);
		faces[0] = rotate(faces[0], direction > 0 ? direction % 4 : 4 - -direction % 4);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	private PumpkinFace rotate(PumpkinFace face, int times) {
		face.unused = true;
		PumpkinFace rotatedFace = new PumpkinFace(face);
		byte[] data = face.getData();
		byte[] rotatedData = new byte[256];
		for (int i = 0; i < times; i++) {
			for (int y = 0; y < 8; y++) {
				for (int x = y; x < 15 - y; x++) {
					rotatedData[y + x * 16] = data[x + (15 - y) * 16];
					rotatedData[x + (15 - y) * 16] = data[15 - y + (15 - x) * 16];
					rotatedData[15 - y + (15 - x) * 16] = data[15 - x + y * 16];
					rotatedData[15 - x + y * 16] = data[y + x * 16];
				}
			}
			System.arraycopy(rotatedData, 0, data, 0, 256);
		}
		rotatedFace.setData(rotatedData);
		return rotatedFace;
	}

	public void setLastHitSide(int lastHitSide) {
		this.lastHitSide = lastHitSide;
	}

	public void setReLighting(boolean reLighting) {
		this.reLighting = reLighting;
	}

	@Override
	public void updateEntity() {
		lit = getBlockType() == Blocks.lit_pumpkin;
		if (prevLit != lit) {
			for (PumpkinFace face : faces) {
				face.setLit(lit);
			}
			prevLit = lit;
		}
	}

	public void writeDetailToNBT(NBTTagCompound tagCompound) {
		NBTTagList tagList = new NBTTagList();
		for (PumpkinFace face : faces) {
			NBTTagCompound faceCompound = new NBTTagCompound();
			face.writeToNBT(faceCompound);
			tagList.appendTag(faceCompound);
		}
		tagCompound.setTag("faces", tagList);
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		writeDetailToNBT(tagCompound);
	}
}
