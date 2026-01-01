package com.yesman.epicparcool;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;


import com.mojang.logging.LogUtils;
import com.yesman.epicparcool.client.event.ParCoolClientEvents;

import com.yesman.epicparcool.event.ParCoolEvents;

import yesman.epicfight.api.animation.LivingMotion;



@Mod(EpicParCool.MODID)
public class EpicParCool {
	public static final String MODID = "epicparcool";
	public static final String LEAST_PARCOOL_VERSION = "3.4.0.6";

	public EpicParCool(ModContainer modContainer) {
		IEventBus modEventbus = modContainer.getEventBus();

		modEventbus.addListener(ParCoolEvents::onSetup);
		modEventbus.addListener(this::constructMod);

		if (FMLEnvironment.dist == Dist.CLIENT) {
			modEventbus.addListener(ParCoolClientEvents::onSetup);
		}
	}


	public void constructMod(FMLConstructModEvent event) {
		LivingMotion.ENUM_MANAGER.registerEnumCls(EpicParCool.MODID, ParcoolLivingMotions.class);
	}
}

