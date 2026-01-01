package com.yesman.epicparcool.mixin;

import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.yesman.epicparcool.animations.ParCoolAnimations;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = ClingToCliff.class)
public class ParCoolMixinClingToCliff {
	@Inject(at = @At(value = "HEAD"), method = "onRenderTick", cancellable = true, remap = false)
	public void epicfight_onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability, CallbackInfo ci) {
		PlayerPatch<?> playerpatch = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
		
		if (playerpatch != null && playerpatch.isEpicFightMode()) {
			ci.cancel();
		}
	}
	
	@Inject(at = @At(value = "HEAD"), method = "canContinue", cancellable = true, remap = false)
	public void epicfight_canContinue(Player player, Parkourability parkourability, CallbackInfoReturnable<Boolean> cir) {
		PlayerPatch<?> playerpatch = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
		
		if (playerpatch != null && playerpatch.isEpicFightMode()) {
			AssetAccessor<? extends StaticAnimation> nowPlaying = playerpatch.getAnimator().getPlayerFor(null).getAnimation().get().getRealAnimation();
			
			if (nowPlaying == ParCoolAnimations.BIPED_CLING_START ||
				nowPlaying == ParCoolAnimations.BIPED_CLING_MOVE_LEFT ||
				nowPlaying == ParCoolAnimations.BIPED_CLING_MOVE_RIGHT ||
				nowPlaying == ParCoolAnimations.BIPED_CLING_MOVE_RIGHT_OUTER_CORNER2 ||
				nowPlaying == ParCoolAnimations.BIPED_CLING_MOVE_LEFT_INNER_CORNER1 ||
				nowPlaying == ParCoolAnimations.BIPED_CLING_MOVE_LEFT_INNER_CORNER2 ||
				nowPlaying == ParCoolAnimations.BIPED_CLING_MOVE_RIGHT_INNER_CORNER1 ||
				nowPlaying == ParCoolAnimations.BIPED_CLING_MOVE_RIGHT_INNER_CORNER2 ||
				nowPlaying == ParCoolAnimations.BIPED_CLING_MOVE_LEFT_OUTER_CORNER1 ||
				nowPlaying == ParCoolAnimations.BIPED_CLING_MOVE_LEFT_OUTER_CORNER2 ||
				nowPlaying == ParCoolAnimations.BIPED_CLING_MOVE_RIGHT_OUTER_CORNER1 ||
				nowPlaying == ParCoolAnimations.BIPED_CLING_MOVE_RIGHT_OUTER_CORNER2
			) {
				cir.setReturnValue(true);
				cir.cancel();
			}
		}
	}
	
	@Inject(at = @At(value = "TAIL"), method = "onWorkingTickInLocalClient", remap = false)
	public void epicfight_onWorkingTickInLocalClient(Player player, Parkourability parkourability, CallbackInfo ci) {
		PlayerPatch<?> playerpatch = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
		
		if (playerpatch != null && playerpatch.isEpicFightMode()) {
			player.setDeltaMovement(0, 0, 0);
		}
	}
}