package com.yesman.epicparcool.mixin;

import com.alrex.parcool.common.action.impl.RideZipline;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RideZipline.class)
public interface ParCoolMixinRideZiplineAccessor {
	@Accessor()
	Vec3 getEndOffsetFromStart();
	
	@Accessor()
	double getSpeed();
}
