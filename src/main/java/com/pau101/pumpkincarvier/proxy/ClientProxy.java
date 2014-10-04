package com.pau101.pumpkincarvier.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import net.minecraftforge.client.MinecraftForgeClient;

import com.pau101.pumpkincarvier.PumpkinCarvier;
import com.pau101.pumpkincarvier.client.TickHandlerPumpkin;
import com.pau101.pumpkincarvier.client.render.ItemRendererPumpkin;
import com.pau101.pumpkincarvier.client.render.tileentity.TileEntityPumpkinRenderer;
import com.pau101.pumpkincarvier.tileentity.TileEntityPumpkin;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ClientProxy extends CommonProxy {
	public static Timer mcTimer;

	@Override
	public void initRenders() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPumpkin.class, new TileEntityPumpkinRenderer());

		ItemRendererPumpkin itemRendererPumpkin = new ItemRendererPumpkin();
		MinecraftForgeClient.registerItemRenderer(PumpkinCarvier.item_pumpkin, itemRendererPumpkin);
		MinecraftForgeClient.registerItemRenderer(PumpkinCarvier.item_lit_pumpkin, itemRendererPumpkin);

		mcTimer = (Timer) ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), PumpkinCarvier.F_TIMER);

		FMLCommonHandler.instance().bus().register(new TickHandlerPumpkin());
	}
}
