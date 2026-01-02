package com.yesman.epicparcool.client.event;

import com.alrex.parcool.client.animation.impl.*;
import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.action.impl.HangDown.BarAxis;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.VectorUtil;
import com.google.common.collect.Maps;
import com.yesman.epicparcool.EpicParCool;
import com.yesman.epicparcool.ParCoolUtils;
import com.yesman.epicparcool.ParCoolUtils.ClingType;
import com.yesman.epicparcool.ParcoolLivingMotions;
import com.yesman.epicparcool.animations.ParCoolAnimations;
import com.yesman.epicparcool.client.screen.EpicParCoolConfigurations;
import com.yesman.epicparcool.mixin.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.neoevent.UpdatePlayerMotionEvent;
import yesman.epicfight.api.neoevent.playerpatch.SkillCastEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.registry.entries.EpicFightSkillDataKeys;
import yesman.epicfight.registry.entries.EpicFightSkills;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = EpicParCool.MODID, value = Dist.CLIENT)
public class ParCoolClientEvents {
	@FunctionalInterface
	public interface LifecycleAnimationLinker {
		void accept(com.alrex.parcool.client.animation.Animator animator, Parkourability parkourability, UpdatePlayerMotionEvent.BaseLayer animationUpdateEvent);
	}
	
	private static final Map<Class<? extends com.alrex.parcool.client.animation.Animator>, LifecycleAnimationLinker> PARCOOL_ANIMATOR_MAPPING = Maps.newHashMap();
	private static final ByteBuffer DUMMY_BUFFER = ByteBuffer.allocate(128);
	private static final UUID EVENT_UUID = UUID.fromString("bc79276d-a0d1-4e58-867f-6bdd25d1ba23");

	@SubscribeEvent
	public static void onSetup(FMLClientSetupEvent event) {
		ModContainer container = ModLoadingContext.get().getActiveContainer();
		container.registerExtensionPoint(
				IConfigScreenFactory.class,
				(c, screen) -> new EpicParCoolConfigurations(screen)
		);

		PARCOOL_ANIMATOR_MAPPING.clear();
		
		PARCOOL_ANIMATOR_MAPPING.put(JumpChargingAnimator.class, (animator, parkourability, livingMotionUpdateEvent) -> {
			livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.CAT_LEAP_PREPARATION);
		});
		
		PARCOOL_ANIMATOR_MAPPING.put(ClingToCliffAnimator.class, (animator, parkourability, livingMotionUpdateEvent) -> {
			livingMotionUpdateEvent.getPlayerPatch().getAnimator().getVariables().getSharedVariable(ParCoolAnimations.CLING_TYPE).ifPresentOrElse((clingDirection) -> {
				if (clingDirection == ClingType.OUTER_CORNER) {
					livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.CLING_TO_CLIFF_OUTER_CORNER);
				} else if (clingDirection == ClingType.INNER_CORNER) {
					livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.CLING_TO_CLIFF_INNER_CORNER);
				} else {
					switch (parkourability.get(ClingToCliff.class).getFacingDirection()) {
					case ToWall -> {
						livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.CLING_TO_CLIFF);
					}
					case LeftAgainstWall -> {
						livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.CLING_TO_CLIFF_LEFT);			
					}
					case RightAgainstWall -> {
						livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.CLING_TO_CLIFF_RIGHT);
					}
					}
				}
			}, () -> {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.CLING_TO_CLIFF);
			});
		});
		
		PARCOOL_ANIMATOR_MAPPING.put(DiveAnimationHostAnimator.class, (animator, parkourability, livingMotionUpdateEvent) -> {
			if (livingMotionUpdateEvent.getPlayerPatch().getOriginal().isFallFlying()) {
				return;
			}
			
			if (((ParCoolMixinDiveAnimationHostAnimator)animator).getSkyDiveAnimator() != null) {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.SKY_DIVE);
			} else {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.DIVE);
			}
		});
		
		PARCOOL_ANIMATOR_MAPPING.put(WallSlideAnimator.class, (animator, parkourability, livingMotionUpdateEvent) -> {
			Vec3 wall = parkourability.get(WallSlide.class).getLeanedWallDirection();
			
			if (wall == null) {
				return;
			}
			
			Vec3 bodyVec = VectorUtil.fromYawDegree(livingMotionUpdateEvent.getPlayerPatch().getOriginal().yBodyRot);
			Vec3 vec = new Vec3(bodyVec.x, 0, bodyVec.z).normalize();
			Vec3 dividedVec = new Vec3(vec.x * wall.x + vec.z * wall.z, 0, -vec.x * wall.z + vec.z * wall.x).normalize();
			
			if (dividedVec.z < 0) {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.WALL_SLIDING_RIGHT);
			} else {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.WALL_SLIDING_LEFT);
			}
		});
		
		PARCOOL_ANIMATOR_MAPPING.put(HorizontalWallRunAnimator.class, (animator, parkourability, livingMotionUpdateEvent) -> {
			if (((ParCoolMixinHorizontalWallRunAnimator)animator).getWallIsRightSide()) {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.WALL_RUNNING_RIGHT);
			} else {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.WALL_RUNNING_LEFT);
			}
		});
		
		PARCOOL_ANIMATOR_MAPPING.put(FastRunningAnimator.class, (animator, parkourability, livingMotionUpdateEvent) -> {
			if (livingMotionUpdateEvent.getMotion() == LivingMotions.RUN) {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.FAST_RUN);
			}
		});
		
		PARCOOL_ANIMATOR_MAPPING.put(HangAnimator.class, (animator, parkourability, livingMotionUpdateEvent) -> {
			HangDown hangDown = parkourability.get(HangDown.class);
			
			if (hangDown.isOrthogonalToBar()) {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.HANG_DOWN_ORTHOGONAL);
			} else {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.HANG_DOWN);
			}
		});
		
		PARCOOL_ANIMATOR_MAPPING.put(SlidingAnimator.class, (animator, parkourability, livingMotionUpdateEvent) -> {
			livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.SLIDING);
		});
		
		PARCOOL_ANIMATOR_MAPPING.put(HideInBlockAnimator.class, (animator, parkourability, livingMotionUpdateEvent) -> {
			if (!((ParCoolMixinHideInBlockAnimator)animator).getStanding()) {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.HIDE_IN_BLOCK_HORIZONTAL);
			}
		});
		
		PARCOOL_ANIMATOR_MAPPING.put(RideZiplineAnimator.class, (animator, parkourability, livingMotionUpdateEvent) -> {
			LivingMotion oldMotion = livingMotionUpdateEvent.getPlayerPatch().getClientAnimator().currentMotion();
			
			if (oldMotion == ParcoolLivingMotions.RIDE_ZIPLINE_FORWARD || oldMotion == ParcoolLivingMotions.RIDE_ZIPLINE_SIDE) {
				livingMotionUpdateEvent.setMotion(oldMotion);
				return;
			}
			
			ParCoolMixinRideZiplineAccessor action = (ParCoolMixinRideZiplineAccessor)parkourability.get(RideZipline.class);
			Vec3 offset = action.getEndOffsetFromStart().normalize();
			Vec3 lookVec = VectorUtil.fromYawDegree(livingMotionUpdateEvent.getPlayerPatch().getOriginal().getYRot());
			double dot = offset.dot(lookVec);
			double yRot = VectorUtil.toYaw(offset);
			
			if (Math.abs(dot) > 0.5D) {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.RIDE_ZIPLINE_FORWARD);
				
				if (dot < 0.0D) {
					yRot += 180.0D;
				}
				
				livingMotionUpdateEvent.getPlayerPatch().setYRot((float)yRot);
			} else {
				livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.RIDE_ZIPLINE_SIDE);
			}
		});
		
		/**
		PARCOOL_ANIMATOR_MAPPING.put(CrawlAnimator.class, (animator, parkourability, livingMotionUpdateEvent) -> {
			livingMotionUpdateEvent.setMotion(ParcoolLivingMotions.CRAWL);
		});
		**/
	}
	
	@SubscribeEvent
	public static void onSkillCast(SkillCastEvent event) {
		if (event.getSkillContainer().getSkill() != EpicFightSkills.PHANTOM_ASCENT.get()) {
			return;
		}

		PlayerPatch<?> playerpatch = event.getPlayerPatch();

		Parkourability parkourability = Parkourability.get(playerpatch.getOriginal());

		if (parkourability.get(ClingToCliff.class).isDoing()) {
			event.setCanceled(true);
			return;
		}

		DUMMY_BUFFER.clear();

		if (parkourability.get(WallJump.class).canStart(playerpatch.getOriginal(), parkourability, DUMMY_BUFFER)) {
			DUMMY_BUFFER.flip();
			event.setCanceled(true);
			event.getSkillContainer().getDataManager().setData(EpicFightSkillDataKeys.JUMP_KEY_PRESSED_LAST_TICK, true);
			return;
		}

		DUMMY_BUFFER.clear();

		if (parkourability.get(VerticalWallRun.class).canStart(playerpatch.getOriginal(), parkourability, DUMMY_BUFFER)) {
			DUMMY_BUFFER.flip();
			event.setCanceled(true);
			event.getSkillContainer().getDataManager().setData(EpicFightSkillDataKeys.JUMP_KEY_PRESSED_LAST_TICK, true);
			return;
		}

		DUMMY_BUFFER.clear();

		if (parkourability.get(JumpFromBar.class).canStart(playerpatch.getOriginal(), parkourability, DUMMY_BUFFER)) {
			DUMMY_BUFFER.flip();
			event.setCanceled(true);
			event.getSkillContainer().getDataManager().setData(EpicFightSkillDataKeys.JUMP_KEY_PRESSED_LAST_TICK, true);
			return;
		}
	}
	
	@SubscribeEvent
	public static void onBaseLayerUpdateEvent(UpdatePlayerMotionEvent.BaseLayer event) {
		if (event.inaction()) {
			return;
		}
		Player player = event.getPlayerPatch().getOriginal();
		Animation animation = Animation.get(player);

		ParCoolMixinAnimation animationAccessor = (ParCoolMixinAnimation)animation;
		com.alrex.parcool.client.animation.Animator animator = animationAccessor.getAnimator();
		Parkourability parkourability = Parkourability.get(event.getPlayerPatch().getOriginal());

		if (parkourability != null && animator != null && PARCOOL_ANIMATOR_MAPPING.containsKey(animator.getClass())) {
			if (!animator.shouldRemoved(event.getPlayerPatch().getOriginal(), parkourability)) {
				PARCOOL_ANIMATOR_MAPPING.get(animator.getClass()).accept(animator, parkourability, event);
			}
		}
	}
	
	@SubscribeEvent
	public static void onMovementInputUpdateEvent(MovementInputUpdateEvent event) {
		LocalPlayerPatch playerpatch = EpicFightCapabilities.getEntityPatch(event.getEntity(), LocalPlayerPatch.class);
		
		if (playerpatch == null || !playerpatch.isEpicFightMode()) {
			return;
		}
		
		Parkourability parkourability = Parkourability.get(event.getEntity());
		HangDown hangDown;
		
		if ((hangDown = (HangDown)parkourability.get(HangDown.class)) != null && hangDown.isDoing()) {
			if (!playerpatch.getEntityState().inaction()) {
				float yRot = ParCoolUtils.idealYRotForHanging(hangDown, event.getEntity());
				BarAxis barAxis = hangDown.getHangingBarAxis();
				boolean axisMismatches = false;
				
				if (hangDown.isOrthogonalToBar()) {
					if (barAxis == ParCoolUtils.getLookBarAxis(yRot)) {
						axisMismatches = true;
					}
				} else {
					if (barAxis != ParCoolUtils.getLookBarAxis(yRot)) {
						axisMismatches = true;
					}
				}
				
				if (!axisMismatches) {
					playerpatch.setModelYRot(yRot, true);
					AssetAccessor<? extends ActionAnimation> animationAccessor = null;
					AssetAccessor<? extends ActionAnimation> endAnimationAccessor = null;
					
					if (hangDown.isOrthogonalToBar()) {
						if (event.getInput().left) {
							animationAccessor = ParCoolAnimations.BIPED_HANG_DOWN_MOVE_LEFT;
						} else if (event.getInput().right) {
							animationAccessor = ParCoolAnimations.BIPED_HANG_DOWN_MOVE_RIGHT;
						}
					} else {
						if (event.getInput().up) {
							animationAccessor = ParCoolAnimations.BIPED_HANG_DOWN_MOVE_FORWARD_START;
							endAnimationAccessor = ParCoolAnimations.BIPED_HANG_DOWN_MOVE_FORWARD_END1;
						} else if (event.getInput().down) {
							animationAccessor = ParCoolAnimations.BIPED_HANG_DOWN_MOVE_BACKWARD;
						}
					}
					
					if (animationAccessor != null) {
						Vec3 simulatedMove = animationAccessor.get().getExpectedMovement(playerpatch, animationAccessor.get().getTotalTime());
						
						if (endAnimationAccessor != null) {
							simulatedMove = simulatedMove.add(endAnimationAccessor.get().getExpectedMovement(playerpatch, endAnimationAccessor.get().getTotalTime()));
						}
						
						Vec3 hangDownDest = ParCoolUtils.getHangableBars(playerpatch.getOriginal(), simulatedMove);
						
						if (hangDownDest != null) {
							playerpatch.playAnimationSynchronized(animationAccessor, 0.0F);
						}
					}
				}
			}
			
			event.getInput().left = false;
			event.getInput().right = false;
			event.getInput().up = false;
			event.getInput().down = false;
			event.getInput().forwardImpulse = 0.0F;
			event.getInput().leftImpulse = 0.0F;
			event.getEntity().setDeltaMovement(0, 0, 0);
		} else if (parkourability.get(ClingToCliff.class).isDoing()) {
			if (!playerpatch.getEntityState().inaction()) {
				if (event.getInput().left) {
					ParCoolUtils.scanTerrainAndStartClingAction(playerpatch, ParCoolUtils.WallMoveType.MOVE_LEFT);
				} else if (event.getInput().right) {
					ParCoolUtils.scanTerrainAndStartClingAction(playerpatch, ParCoolUtils.WallMoveType.MOVE_RIGHT);
				}
			}
			
			event.getInput().left = false;
			event.getInput().right = false;
			event.getInput().up = false;
			event.getInput().down = false;
			event.getInput().forwardImpulse = 0.0F;
			event.getInput().leftImpulse = 0.0F;
		}
	}
}
