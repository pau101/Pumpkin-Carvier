package com.pau101.pumpkincarvier.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;

import com.pau101.pumpkincarvier.PumpkinCarvier;
import com.pau101.pumpkincarvier.block.BlockPumpkin;
import com.pau101.pumpkincarvier.item.ItemPumpkin;
import com.pau101.pumpkincarvier.tileentity.TileEntityPumpkin;
import com.pau101.pumpkincarvier.util.Constants;
import com.pau101.pumpkincarvier.util.Reflection;

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	public static boolean replaceBlock(Block block, Block newBlock, ItemBlock newItemBlock, boolean replaceField) {
		try {
			FMLControlledNamespacedRegistry<Block> registry = GameData.getBlockRegistry();
			String registryName = registry.getNameForObject(block);
			int id = Block.getIdFromBlock(block);
			ItemBlock itemBlock = (ItemBlock) Item.getItemFromBlock(block);

			Reflection.invoke(Constants.METHOD_REGISTRY_ADDOBJECTRAW, registry, new Object[] { id, registryName, newBlock });

			if (newItemBlock != null) {
				replaceItem(itemBlock, newItemBlock, false);
			} else if (itemBlock != null) {
				Reflection.setField(Constants.FIELD_ItemBlock_block, itemBlock, newBlock);
			}

			if (replaceField) {
				for (Field field : Blocks.class.getDeclaredFields()) {
					if (!Block.class.isAssignableFrom(field.getType())) {
						continue;
					}

					Block block1 = (Block) field.get(null);
					if (block1 != block) {
						continue;
					}

					Reflection.setModifier(field, Modifier.FINAL, false);
					field.set(null, newBlock);
				}
			}

			boolean flag = true;
			if (registry.getObject(registryName) != newBlock) {
				flag = false;
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean replaceItem(Item item, Item newItem, boolean replaceField) {
		try {
			FMLControlledNamespacedRegistry<Item> registry = GameData.getItemRegistry();
			String registryName = registry.getNameForObject(item);
			int id = Item.getIdFromItem(item);

			Reflection.invoke(Constants.METHOD_REGISTRY_ADDOBJECTRAW, registry, new Object[] { id, registryName, newItem });

			StatCrafting stat = (StatCrafting) StatList.objectBreakStats[id];
			if (stat != null) {
				Reflection.setField(Constants.FIELD_StatCrafting_item, stat, newItem);
			}
			stat = (StatCrafting) StatList.objectCraftStats[id];
			if (stat != null) {
				Reflection.setField(Constants.FIELD_StatCrafting_item, stat, newItem);
			}
			stat = (StatCrafting) StatList.objectUseStats[id];
			if (stat != null) {
				Reflection.setField(Constants.FIELD_StatCrafting_item, stat, newItem);
			}

			if (replaceField) {
				for (Field field : Items.class.getDeclaredFields()) {
					if (!Item.class.isAssignableFrom(field.getType())) {
						continue;
					}

					Item item1 = (Item) field.get(null);
					if (item1 != item) {
						continue;
					}

					Reflection.setModifier(field, Modifier.FINAL, false);
					field.set(null, newItem);
				}
			}

			boolean flag = true;
			if (registry.getObject(registryName) != newItem) {
				flag = false;
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void initBlocks() {
		PumpkinCarvier.block_pumpkin = (BlockPumpkin) new BlockPumpkin(false).setHardness(1.0F).setStepSound(Block.soundTypeWood).setBlockName("pumpkin").setBlockTextureName(PumpkinCarvier.MODID + ":pumpkin_transparent");
		PumpkinCarvier.block_lit_pumpkin = (BlockPumpkin) new BlockPumpkin(true).setHardness(1.0F).setStepSound(Block.soundTypeWood).setLightLevel(1.0F).setBlockName("litpumpkin").setBlockTextureName(PumpkinCarvier.MODID + ":pumpkin_transparent");

		PumpkinCarvier.item_pumpkin = new ItemPumpkin(PumpkinCarvier.block_pumpkin);
		PumpkinCarvier.item_lit_pumpkin = new ItemPumpkin(PumpkinCarvier.block_lit_pumpkin);

		replaceBlock(Blocks.pumpkin, PumpkinCarvier.block_pumpkin, PumpkinCarvier.item_pumpkin, true);
		replaceBlock(Blocks.lit_pumpkin, PumpkinCarvier.block_lit_pumpkin, PumpkinCarvier.item_lit_pumpkin, true);

		Reflection.setValue(BlockStem.class, (BlockStem) Blocks.pumpkin_stem, Blocks.pumpkin, PumpkinCarvier.F_STEM_PLANT);
	}

	public void initCrafting() {
		CraftingManager.getInstance().addRecipe(new ItemStack(Items.pumpkin_seeds, 4), new Object[] { "M", 'M', Blocks.pumpkin });
		CraftingManager.getInstance().addShapelessRecipe(new ItemStack(Items.pumpkin_pie), new Object[] { Blocks.pumpkin, Items.sugar, Items.egg });
	}

	public void initEntities() {
		GameRegistry.registerTileEntity(TileEntityPumpkin.class, "pumpkin");
	}

	public void initRenders() {
	}
}
