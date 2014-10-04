package com.pau101.pumpkincarvier.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.pau101.pumpkincarvier.PumpkinCarvier;
import com.pau101.pumpkincarvier.tileentity.TileEntityPumpkin;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class ItemPumpkin extends ItemBlock {
	private static NBTTagList generageDefaultFaces(boolean lit) {
		NBTTagList tagList = new NBTTagList();
		for (int i = 0; i < 6; i++) {
			NBTTagCompound faceCompound = new NBTTagCompound();
			faceCompound.setBoolean("vertical", i < 2);
			faceCompound.setString("data", Base64.encode(new byte[256]));
			faceCompound.setInteger("hashCode", i < 2 ? lit ? -1064898048 : -1064898042 : lit ? -1064897862 : -1064897856);
			tagList.appendTag(faceCompound);
		}
		return tagList;
	}

	private static NBTTagCompound defaultTagCompound = new NBTTagCompound();
	private static NBTTagCompound litDefaultTagCompound = new NBTTagCompound();

	static {
		defaultTagCompound.setTag("faces", generageDefaultFaces(false));
		litDefaultTagCompound.setTag("faces", generageDefaultFaces(true));
	}

	public ItemPumpkin(Block block) {
		super(block);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List lines, boolean isHeld) {
		if (itemStack.hasTagCompound() && !(itemStack.getItem() == PumpkinCarvier.item_lit_pumpkin ? litDefaultTagCompound : defaultTagCompound).equals(itemStack.getTagCompound().getCompoundTag("block"))) {
			lines.add("Carved");
		} else {
			lines.add("Uncarved");
		}
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		Block block = world.getBlock(x, y, z);

		if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1) {
			side = 1;
		} else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z)) {
			if (side == 0) {
				--y;
			}

			if (side == 1) {
				++y;
			}

			if (side == 2) {
				--z;
			}

			if (side == 3) {
				++z;
			}

			if (side == 4) {
				--x;
			}

			if (side == 5) {
				++x;
			}
		}

		if (itemStack.stackSize == 0) {
			return false;
		} else if (!player.canPlayerEdit(x, y, z, side, itemStack)) {
			return false;
		} else if (y == 255 && field_150939_a.getMaterial().isSolid()) {
			return false;
		} else if (world.canPlaceEntityOnSide(field_150939_a, x, y, z, false, side, player, itemStack)) {
			int metadata = getMetadata(itemStack.getItemDamage());
			int placedMetadata = field_150939_a.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, metadata);

			if (placeBlockAt(itemStack, player, world, x, y, z, side, hitX, hitY, hitZ, placedMetadata)) {
				if (itemStack.hasTagCompound() && !world.isRemote) {
					NBTTagCompound tagCompound = itemStack.getTagCompound();
					if (tagCompound.hasKey("block", 10)) {
						TileEntityPumpkin pumpkin = (TileEntityPumpkin) world.getTileEntity(x, y, z);
						if (pumpkin != null) {
							NBTTagCompound blockCompound = tagCompound.getCompoundTag("block");
							pumpkin.readDetailFromNBT(blockCompound);
							pumpkin.rotate(3 - (MathHelper.floor_double(player.rotationYaw * 4 / 360 + 2.5) & 3));
							world.markBlockForUpdate(x, y, z);
						}
					}
				}
				world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, field_150939_a.stepSound.func_150496_b(), (field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, field_150939_a.stepSound.getPitch() * 0.8F);
				--itemStack.stackSize;
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int slot, boolean isHeld) {
		if (!entity.worldObj.isRemote) {
			NBTTagCompound tagCompound;
			if (!itemStack.hasTagCompound()) {
				tagCompound = new NBTTagCompound();
				itemStack.setTagCompound(tagCompound);
			}
			tagCompound = itemStack.getTagCompound();
			if (!tagCompound.hasKey("block", 10)) {
				tagCompound.setTag("block", (itemStack.getItem() == PumpkinCarvier.item_lit_pumpkin ? litDefaultTagCompound : defaultTagCompound).copy());
			}
		}
	}
}
