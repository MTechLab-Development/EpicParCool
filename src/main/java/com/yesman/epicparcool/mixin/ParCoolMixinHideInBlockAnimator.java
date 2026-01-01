package com.yesman.epicparcool.mixin;

import com.alrex.parcool.client.animation.impl.HideInBlockAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = HideInBlockAnimator.class)
public interface ParCoolMixinHideInBlockAnimator {
	@Accessor()
	boolean getStanding();
}
