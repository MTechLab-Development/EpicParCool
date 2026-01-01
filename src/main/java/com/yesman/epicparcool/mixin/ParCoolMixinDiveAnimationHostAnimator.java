package com.yesman.epicparcool.mixin;

import com.alrex.parcool.client.animation.impl.DiveAnimationHostAnimator;
import com.alrex.parcool.client.animation.impl.DiveAnimationHostAnimator.SkyDiveAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DiveAnimationHostAnimator.class)
public interface ParCoolMixinDiveAnimationHostAnimator {
	@Accessor("skyDiveAnimator")
	public SkyDiveAnimator getSkyDiveAnimator();
}