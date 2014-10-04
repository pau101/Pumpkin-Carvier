package com.pau101.pumpkincarvier.pumpkin;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.nbt.NBTTagCompound;

import com.pau101.pumpkincarvier.tileentity.TileEntityPumpkin;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class PumpkinFace {
	public TileEntityPumpkin host;

	private byte[] data;
	private boolean vertical;
	private boolean lit;

	public boolean unused;

	public PumpkinFace() {
	}

	public PumpkinFace(PumpkinFace face) {
		host = face.host;
		data = ArrayUtils.clone(face.data);
		vertical = face.vertical;
		lit = face.lit;
		unused = false;
	}

	public PumpkinFace(TileEntityPumpkin host) {
		this.host = host;
		data = new byte[256];
		vertical = false;
		lit = host.isLit();
		unused = false;
	}

	public PumpkinFace carve(int x, int y) {
		unused = true;
		PumpkinFace newFace = new PumpkinFace(this);
		newFace.data[x + y * 16] = 1;
		return newFace;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PumpkinFace other = (PumpkinFace) obj;
		if (!Arrays.equals(data, other.data)) {
			return false;
		}
		if (vertical != other.vertical) {
			return false;
		}
		if (lit != other.lit) {
			return false;
		}
		return true;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + (vertical ? 1231 : 1237);
		result = prime * result + (lit ? 1231 : 1237);
		return result;
	}

	public boolean isLit() {
		return lit;
	}

	public boolean isVertical() {
		return vertical;
	}

	public void readFromNBT(NBTTagCompound tagCompound) {
		try {
			data = Base64.decode(tagCompound.getString("data"));
		} catch (Base64DecodingException e) {
			e.printStackTrace();
		}
		vertical = tagCompound.getBoolean("vertical");
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void setLit(boolean lit) {
		this.lit = lit;
	}

	public void setVertical(boolean verticle) {
		vertical = verticle;
	}

	public void writeToNBT(NBTTagCompound tagCompound) {
		tagCompound.setString("data", Base64.encode(data));
		tagCompound.setBoolean("vertical", vertical);
		tagCompound.setInteger("hashCode", hashCode());
	}
}
