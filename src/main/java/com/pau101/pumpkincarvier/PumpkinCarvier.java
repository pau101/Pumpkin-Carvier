package com.pau101.pumpkincarvier;

import com.pau101.pumpkincarvier.block.BlockPumpkin;
import com.pau101.pumpkincarvier.item.ItemPumpkin;
import com.pau101.pumpkincarvier.proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = PumpkinCarvier.MODID, name = PumpkinCarvier.NAME, version = PumpkinCarvier.VERSION)
public class PumpkinCarvier {
	public static final String MODID = "pumpkincarvier";
	public static final String NAME = "Pumpkin Carvier";
	public static final String VERSION = "0.1";

	@Instance
	public static PumpkinCarvier instance;

	@SidedProxy(clientSide = "com.pau101.pumpkincarvier.proxy.ClientProxy", serverSide = "com.pau101.pumpkincarvier.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static BlockPumpkin block_pumpkin;
	public static BlockPumpkin block_lit_pumpkin;

	public static ItemPumpkin item_pumpkin;
	public static ItemPumpkin item_lit_pumpkin;

	public static final String[] F_TIMER = { "field_71428_T", "Q", "timer" };
	public static final String[] F_PUMPKIN_BLUR_TEX_PATH = { "field_110328_d", "h", "pumpkinBlurTexPath" };
	public static final String[] F_CAMERA_ZOOM = { "field_78503_V", "Y", "cameraZoom" };
	public static final String[] F_STEM_PLANT = { "field_149877_a", "a" };
	public static final String[] F_MAP_TEXTURE_OBJECTS = { "field_110585_a", "b", "mapTextureObjects" };

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.initBlocks();
		proxy.initCrafting();
		proxy.initEntities();
		proxy.initRenders();
	}
}
