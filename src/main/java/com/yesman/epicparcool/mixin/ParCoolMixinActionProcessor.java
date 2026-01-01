package com.yesman.epicparcool.mixin;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.Dodge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = ActionProcessor.class)
public class ParCoolMixinActionProcessor {
	@Redirect(at = @At( value = "INVOKE",
			target = "Lcom/alrex/parcool/common/action/Action;getStaminaConsumeTiming()Lcom/alrex/parcool/common/action/StaminaConsumeTiming;"),
			method = "onTick"
	)
	public StaminaConsumeTiming epicfight_getStaminaConsumeTimingInTick(Action action, PlayerTickEvent.Pre event) {
		PlayerPatch<?> playerpatch = EpicFightCapabilities.getEntityPatch(event.getEntity(), PlayerPatch.class);
		
		if (playerpatch != null && playerpatch.isEpicFightMode()) {
			if (action.getClass() == Dodge.class) {
				return null;
			}
		}
		
		return action.getStaminaConsumeTiming();
	}
}