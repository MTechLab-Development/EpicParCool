package com.yesman.epicparcool.mixin;

import com.alrex.parcool.common.action.impl.ChargeJump;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChargeJump.class)
public class ParCoolMixinChargeJump {
	@Redirect(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;isShiftKeyDown()Z"
			),
			method = "onClientTick",
			remap = false
	)
	private boolean epicparcool_isShiftKeyDown(LocalPlayer self) {
		return self.isShiftKeyDown();
	}
}