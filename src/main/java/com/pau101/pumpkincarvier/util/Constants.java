package com.pau101.pumpkincarvier.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatCrafting;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;

public class Constants {
	public static final int COLOR_RED = 0xFF0000;
	public static final int COLOR_YELLOW = 0xFFDF00;
	public static final int COLOR_GREEN = 0x00DF00;
	public static final int COLOR_BLUE = 0x007FFF;
	public static final int COLOR_LIGHT_BLUE = 0x00DFFF;

	public static final Field FIELD_ItemStack_item = Reflection.getField(ItemStack.class, 3);
	public static final Field FIELD_ItemBlock_block = Reflection.getField(ItemBlock.class, 0);
	public static final Field FIELD_StatCrafting_item = Reflection.getField(StatCrafting.class, 0);
	public static final Field FIELD_ItemArmor_armorType = Reflection.getField(ItemArmor.class, 4);
	public static final Field FIELD_ItemArmor_damageReduction = Reflection.getField(ItemArmor.class, 5);
	public static final Field FIELD_EntityList_classToIDMapping = Reflection.getField(EntityList.class, 4);
	public static final Field FIELD_EntityList_stringToIDMapping = Reflection.getField(EntityList.class, 5);
	public static final Field FIELD_CreativeTabs_tabIndex = Reflection.getField(CreativeTabs.class, 13);

	public static final Method METHOD_REGISTRY_ADDOBJECTRAW = Reflection.getMethod(FMLControlledNamespacedRegistry.class, "addObjectRaw", new Class[] { int.class, String.class, Object.class });
}
