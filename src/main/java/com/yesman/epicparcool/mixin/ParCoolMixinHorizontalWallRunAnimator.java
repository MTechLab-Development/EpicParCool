package com.yesman.epicparcool.mixin;

import com.alrex.parcool.client.animation.impl.HorizontalWallRunAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = HorizontalWallRunAnimator.class)
public interface ParCoolMixinHorizontalWallRunAnimator {
	@Accessor
	public boolean getWallIsRightSide();
}