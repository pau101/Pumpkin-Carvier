package com.pau101.pumpkincarvier.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.pau101.pumpkincarvier.PumpkinCarvier;
import com.pau101.pumpkincarvier.tileentity.TileEntityPumpkin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPumpkin extends net.minecraft.block.BlockPumpkin implements ITileEntityProvider {
	public BlockPumpkin(boolean lit) {
		super(lit);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		if (world.getTileEntity(x, y, z) != null && !((TileEntityPumpkin) world.getTileEntity(x, y, z)).isReLighting()) {
			dropBlockAsItem(world, x, y, z, getItemForBlock(world, x, y, z, metadata));
		}
		super.breakBlock(world, x, y, z, block, metadata);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metaData) {
		return new TileEntityPumpkin();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	public ItemStack getItemForBlock(World world, int x, int y, int z, int metadata) {
		ItemStack returnItem = new ItemStack(Item.getItemFromBlock(this), 1, damageDropped(metadata));
		NBTTagCompound tagCompound = new NBTTagCompound();
		NBTTagCompound blockCompound = new NBTTagCompound();
		TileEntityPumpkin tileEntity = (TileEntityPumpkin) world.getTileEntity(x, y, z);
		tileEntity.rotate(tileEntity.getLastHitSide() + 1);
		if (tileEntity instanceof TileEntityPumpkin) {
			tileEntity.writeDetailToNBT(blockCompound);
		}
		tagCompound.setTag("block", blockCompound);
		returnItem.setTagCompound(tagCompound);
		return returnItem;
	}

	@Override
	public String getItemIconName() {
		return PumpkinCarvier.MODID + ":pumpkin";
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return getItemForBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float dx, float dy, float dz) {
		ItemStack heldItem;
		if (player.isSneaking()) {
			if (side > 1) {
				boolean clockwise = side == 2 ? dx < 0.5f : side == 3 ? dx > 0.5f : side == 4 ? dz > 0.5f : dz < 0.5f;
				if (!world.isRemote) {
					TileEntity tileEntity = world.getTileEntity(x, y, z);
					if (tileEntity instanceof TileEntityPumpkin) {
						TileEntityPumpkin tileEntityPumpkin = (TileEntityPumpkin) tileEntity;
						tileEntityPumpkin.rotate(clockwise ? 1 : -1);
					}
				}
				world.playSoundEffect(x + 0.5f, y + 0.5f, z + 0.5f, stepSound.func_150496_b(), (stepSound.getVolume() + 1) / 2f, stepSound.getPitch() * 0.8f);
				return true;
			}
		} else if ((heldItem = player.getHeldItem()) != null) {
			Item item = heldItem.getItem();
			if (item == Items.shears) {
				if (!world.isRemote) {
					int pixelX = MathHelper.floor_float((side < 4 ? dx : dy) * 16);
					int pixelY = MathHelper.floor_float((side != 2 && side != 3 ? dz : dy) * 16);
					if (side == 2 || side > 3) {
						pixelX = 15 - pixelX;
					}
					if (side != 4) {
						pixelY = 15 - pixelY;
					}
					if (side > 3) {
						int temp = pixelX;
						pixelX = pixelY;
						pixelY = temp;
					}
					TileEntity tileEntity = world.getTileEntity(x, y, z);
					if (tileEntity instanceof TileEntityPumpkin) {
						TileEntityPumpkin tileEntityPumpkin = (TileEntityPumpkin) tileEntity;
						tileEntityPumpkin.carve(side, pixelX, pixelY);
					}
				}
			} else if (item == Item.getItemFromBlock(Blocks.torch) && world.getBlock(x, y, z) == Blocks.pumpkin) {
				if (!world.isRemote) {
					NBTTagCompound tagCompound = new NBTTagCompound();
					TileEntityPumpkin tileEntityPumpkin = (TileEntityPumpkin) world.getTileEntity(x, y, z);
					tileEntityPumpkin.writeDetailToNBT(tagCompound);
					tileEntityPumpkin.setReLighting(true);
					world.setBlock(x, y, z, Blocks.lit_pumpkin);
					TileEntityPumpkin tileEntityLitPumpkin = (TileEntityPumpkin) world.getTileEntity(x, y, z);
					tileEntityLitPumpkin.readDetailFromNBT(tagCompound);
					heldItem.stackSize--;
				}
				world.playSoundEffect(x + 0.5f, y + 0.5f, z + 0.5f, "random.pop", 1, world.rand.nextFloat() * 0.2f + 0.9f);
				return true;
			}
		} else if (world.getBlock(x, y, z) == Blocks.lit_pumpkin) {
			if (!world.isRemote) {
				NBTTagCompound tagCompound = new NBTTagCompound();
				TileEntityPumpkin tileEntityLitPumpkin = (TileEntityPumpkin) world.getTileEntity(x, y, z);
				tileEntityLitPumpkin.writeDetailToNBT(tagCompound);
				tileEntityLitPumpkin.setReLighting(true);
				world.setBlock(x, y, z, Blocks.pumpkin);
				TileEntityPumpkin tileEntityPumpkin = (TileEntityPumpkin) world.getTileEntity(x, y, z);
				tileEntityPumpkin.readDetailFromNBT(tagCompound);
				dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.torch, 1));
			}
			world.playSoundEffect(x + 0.5f, y + 0.5f, z + 0.5f, "random.pop", 1, world.rand.nextFloat() * 0.2f + 0.5f);
		}
		return false;
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player) {
		if (!world.isRemote) {
			TileEntityPumpkin tileEntity = (TileEntityPumpkin) world.getTileEntity(x, y, z);
			if (tileEntity != null) {
				tileEntity.setLastHitSide(MathHelper.floor_double(player.rotationYaw * 4 / 360 + 2.5) & 3);
			}
		}
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(getTextureName());
	}
}
