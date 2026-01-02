package com.yesman.epicparcool;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import yesman.epicfight.api.animation.LivingMotion;

/**
 *  ***************************************************************
 *  Changes
 *  ***************************************************************
 *  
 *  20.10.1.3 -> 20.10.1.4
 *  
 *  Added Breakfall animation played when players succeed in canceling fall damage
 *  Added Vertical wall run animation
 *  Added Sky dive animation
 *  Fixed players sometimes unable to move while clinging to walls and moving
 *  Fixed players able to guard while running walls
 *  Fixed a crash caused by grabbing walls in the mining mode then move in the battle mode
 *  Now Epic Fight models are visible when filter animation is off in the mining mode
 *  Now players can't cancel attacks by flip
 *  
 *  ****************************************************************
 *  
 *  20.10.1.4 -> 20.10.1.5
 *  
 *  Fixed the chain movement animations being played after getting away from the chain blocks
 *  Fixed the chain animations clipping when hanging on the wall blocks
 *  Fixed the chain animations to play sounds depending on the hanging block
 *  Fixed phantom ascent and jumping from bar action triggering at the same time
 *  Added the same movement sound when moving corner clinging to the walls
 *  Added side-looking wall cling animations
 *  Locked climbing-up action while moving by clinging to the walls
 *  
 *  ****************************************************************
 *  
 *  20.10.1.5 -> 20.10.1.6
 *  
 *  Fixed an issue where all players are sharing the same animation
 *  
 *  ****************************************************************
 *  
 *  20.10.1.6 -> 20.10.2.1
 *  
 *  Fixed the never-ending parcool animation issue in first person
 *  Fixed the players could pull the bow while doing the Fast Run
 *  Fixed the cat leap jump power being weakened when learn Demolition Leap
 *  Mod dependency changed: parccol-3.3.1.0 -> parccol-3.4.0.1
 *  
 *  ****************************************************************
 *  
 *  20.10.2.1 -> 20.11.0.1
 *  
 *  Added animations for hide-in-block and zipline riding
 *  Fixed the first person animations not ending
 *  Fixed the first animations so that they stick to the grabbing walls & blocks
 *  
 *  ****************************************************************
 *  
 *  @author yesman
 */
@Mod(EpicParCool.MODID)
public class EpicParCool {
	public static final String MODID = "epicparcool";
	
	public EpicParCool(IEventBus modEventBus, ModContainer modContainer) {


	}

	@SubscribeEvent
	public void constructMod(FMLConstructModEvent event) {
		LivingMotion.ENUM_MANAGER.registerEnumCls(EpicParCool.MODID, ParcoolLivingMotions.class);
	}
}
