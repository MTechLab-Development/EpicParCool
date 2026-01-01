package com.yesman.epicparcool.mixin;

import com.alrex.parcool.common.attachment.client.Animation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Animation.class)
public interface ParCoolMixinAnimation {
	@Accessor
	public com.alrex.parcool.client.animation.Animator getAnimator();
}