package com.yesman.epicparcool.mixin;

import com.alrex.parcool.common.action.impl.RideZipline;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@OnlyIn(Dist.CLIENT)
@Mixin(RideZipline.class)
public interface ParCoolMixinRideZiplineAccessor {
	@Accessor("speed")
	double getSpeed();
}