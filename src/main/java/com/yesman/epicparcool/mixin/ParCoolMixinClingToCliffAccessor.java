package com.yesman.epicparcool.mixin;

import com.alrex.parcool.common.action.impl.ClingToCliff;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClingToCliff.class)
public interface ParCoolMixinClingToCliffAccessor {
	@Accessor
	public Vec3 getClingWallDirection();
}