package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.client.animation.AnimationChannel
import net.minecraft.client.animation.AnimationChannel.Interpolations.*
import net.minecraft.client.animation.AnimationDefinition
import net.minecraft.client.animation.Keyframe
import net.minecraft.client.animation.KeyframeAnimations.*

object RaccoonAnimation {
    val WALK: AnimationDefinition = AnimationDefinition.Builder.withLength(2f).looping()
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, -2.5f), CATMULLROM),
                Keyframe(0.25f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(0.5f, degreeVec(0f, 0f, 2.5f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(1f, degreeVec(0f, 0f, -2.5f), CATMULLROM),
                Keyframe(1.25f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, 0f, 2.5f), CATMULLROM),
                Keyframe(1.75f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(2f, degreeVec(0f, 0f, -2.5f), CATMULLROM)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 2.5f), CATMULLROM),
                Keyframe(0.25f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(0.5f, degreeVec(0f, 0f, -2.5f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(1f, degreeVec(0f, 0f, 2.5f), CATMULLROM),
                Keyframe(1.25f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, 0f, -2.5f), CATMULLROM),
                Keyframe(1.75f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(2f, degreeVec(0f, 0f, 2.5f), CATMULLROM)
            )
        )
        .addAnimation(
            "leg_front_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.25f, degreeVec(10f, 0f, 0f), CATMULLROM),
                Keyframe(0.75f, degreeVec(-10f, 0f, 0f), CATMULLROM),
                Keyframe(1f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(1.25f, degreeVec(10f, 0f, 0f), CATMULLROM),
                Keyframe(1.75f, degreeVec(-10f, 0f, 0f), CATMULLROM),
                Keyframe(2f, degreeVec(0f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_right", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, -0.12f, 0f), LINEAR),
                Keyframe(0.25f, posVec(0f, 0f, 1f), LINEAR),
                Keyframe(0.5f, posVec(0f, 1f, -0.6f), CATMULLROM),
                Keyframe(0.75f, posVec(0f, 0f, -1f), CATMULLROM),
                Keyframe(1.25f, posVec(0f, 0f, 1f), CATMULLROM),
                Keyframe(1.5f, posVec(0f, 1f, -0.6f), CATMULLROM),
                Keyframe(1.75f, posVec(0f, 0f, -1f), LINEAR),
                Keyframe(2f, posVec(0f, -0.12f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(0.25f, degreeVec(-10f, 0f, 0f), CATMULLROM),
                Keyframe(0.75f, degreeVec(10f, 0f, 0f), CATMULLROM),
                Keyframe(1.25f, degreeVec(-10f, 0f, 0f), CATMULLROM),
                Keyframe(1.75f, degreeVec(10f, 0f, 0f), CATMULLROM),
                Keyframe(2f, degreeVec(0f, 0f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "leg_front_left", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 1f, -0.6f), CATMULLROM),
                Keyframe(0.25f, posVec(0f, 0f, -1f), CATMULLROM),
                Keyframe(0.75f, posVec(0f, 0f, 1f), CATMULLROM),
                Keyframe(1f, posVec(0f, 1f, -0.6f), CATMULLROM),
                Keyframe(1.25f, posVec(0f, 0f, -1f), CATMULLROM),
                Keyframe(1.75f, posVec(0f, 0f, 1f), CATMULLROM),
                Keyframe(2f, posVec(0f, 1f, -0.6f), CATMULLROM)
            )
        )
        .addAnimation(
            "leg_back_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(10f, 0f, 0f), CATMULLROM),
                Keyframe(0.5f, degreeVec(-10f, 0f, 0f), CATMULLROM),
                Keyframe(1f, degreeVec(10f, 0f, 0f), CATMULLROM),
                Keyframe(1.5f, degreeVec(-10f, 0f, 0f), CATMULLROM),
                Keyframe(2f, degreeVec(10f, 0f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "leg_back_right", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0f, 1f), CATMULLROM),
                Keyframe(0.25f, posVec(0f, 1f, -0.6f), CATMULLROM),
                Keyframe(0.5f, posVec(0f, 0f, -1f), CATMULLROM),
                Keyframe(1f, posVec(0f, 0f, 1f), CATMULLROM),
                Keyframe(1.25f, posVec(0f, 1f, -0.6f), CATMULLROM),
                Keyframe(1.5f, posVec(0f, 0f, -1f), CATMULLROM),
                Keyframe(2f, posVec(0f, 0f, 1f), CATMULLROM)
            )
        )
        .addAnimation(
            "leg_back_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(-10f, 0f, 0f), CATMULLROM),
                Keyframe(0.5f, degreeVec(10f, 0f, 0f), CATMULLROM),
                Keyframe(1f, degreeVec(-10f, 0f, 0f), CATMULLROM),
                Keyframe(1.5f, degreeVec(10f, 0f, 0f), CATMULLROM),
                Keyframe(2f, degreeVec(-10f, 0f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "leg_back_left", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0f, -1f), CATMULLROM),
                Keyframe(0.5f, posVec(0f, 0f, 1f), CATMULLROM),
                Keyframe(0.75f, posVec(0f, 1f, -0.6f), CATMULLROM),
                Keyframe(1f, posVec(0f, 0f, -1f), CATMULLROM),
                Keyframe(1.5f, posVec(0f, 0f, 1f), CATMULLROM),
                Keyframe(1.75f, posVec(0f, 1f, -0.6f), CATMULLROM),
                Keyframe(2f, posVec(0f, 0f, -1f), CATMULLROM)
            )
        )
        .build()

    val HEAD_SHAKE: AnimationDefinition = AnimationDefinition.Builder.withLength(0.1667f)
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(0.0417f, degreeVec(20f, 40f, 40f), CATMULLROM),
                Keyframe(0.125f, degreeVec(20f, -40f, -40f), CATMULLROM),
                Keyframe(0.1667f, degreeVec(0f, 0f, 0f), CATMULLROM)
            )
        )
        .build()

    val TAIL_SWISH: AnimationDefinition = AnimationDefinition.Builder.withLength(1f)
        .addAnimation(
            "tail", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.25f, degreeVec(0f, -20f, 0f), LINEAR),
                Keyframe(0.75f, degreeVec(0f, 20f, 0f), LINEAR),
                Keyframe(1f, degreeVec(0f, 0f, 0f), LINEAR)
            )
        )
        .build()

    val RUMMAGE: AnimationDefinition = AnimationDefinition.Builder.withLength(0.25f).looping()
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(-12.5f, -2.5f, -10f), LINEAR),
                Keyframe(0.125f, degreeVec(-12.3184f, 2.159f, 9.7649f), LINEAR),
                Keyframe(0.25f, degreeVec(-12.5f, -2.5f, -10f), LINEAR)
            )
        )
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0f, 0.5f), LINEAR)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(48f, 15f, 5f), LINEAR),
                Keyframe(0.125f, degreeVec(47f, -15f, -5f), LINEAR),
                Keyframe(0.25f, degreeVec(48f, 15f, 5f), LINEAR)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0.5f, 0.75f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(-22.5f, 0f, -22.5f), LINEAR),
                Keyframe(0.125f, degreeVec(50f, -40f, -20f), LINEAR),
                Keyframe(0.25f, degreeVec(-22.5f, 0f, -22.5f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_right", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.125f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.1667f, posVec(0f, 1.5f, 0f), CATMULLROM),
                Keyframe(0.25f, posVec(0f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(50f, 40f, 20f), LINEAR),
                Keyframe(0.125f, degreeVec(-22.5f, 0f, 22.5f), LINEAR),
                Keyframe(0.25f, degreeVec(50f, 40f, 20f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_left", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.0417f, posVec(0f, 1.5f, 0f), CATMULLROM),
                Keyframe(0.125f, posVec(0f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "front_legs", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(-12.5f, -2.5f, -10f), LINEAR),
                Keyframe(0.125f, degreeVec(-12.3184f, 2.159f, 9.7649f), LINEAR),
                Keyframe(0.25f, degreeVec(-12.5f, -2.5f, -10f), LINEAR)
            )
        )
        .addAnimation(
            "front_legs", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0f, 0.5f), LINEAR)
            )
        )
        .build()

    val DIG: AnimationDefinition = AnimationDefinition.Builder.withLength(8f)
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.5f, degreeVec(1.5f, 0f, 0f), LINEAR),
                Keyframe(1.3333f, degreeVec(-5f, 0f, 0f), LINEAR),
                Keyframe(1.5f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(2f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(2.5f, degreeVec(2.5f, 0f, 0f), LINEAR),
                Keyframe(3f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(3.5f, degreeVec(2.5f, 0f, 0f), LINEAR),
                Keyframe(4f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(4.5f, degreeVec(2.5f, 0f, 0f), LINEAR),
                Keyframe(5.6667f, degreeVec(5f, 0f, 0f), LINEAR),
                Keyframe(5.8333f, degreeVec(-2.5f, 0f, 0f), LINEAR),
                Keyframe(6f, degreeVec(0f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(1.3333f, posVec(0f, 1f, 0f), LINEAR),
                Keyframe(1.5f, posVec(0f, -3f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(1.5f, scaleVec(1.0, 1.0, 1.0), LINEAR),
                Keyframe(1.5417f, scaleVec(1.04, 0.98, 1.02), LINEAR),
                Keyframe(1.5833f, scaleVec(1.0, 1.0, 1.0), LINEAR)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(1.1667f, degreeVec(10f, 0f, 0f), CATMULLROM),
                Keyframe(1.4167f, degreeVec(-10f, 0f, 0f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(1.5833f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(1.875f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(2.0833f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(2.5f, degreeVec(47.5f, 0f, 0f), CATMULLROM),
                Keyframe(2.6667f, degreeVec(38.44f, 0f, 0f), CATMULLROM),
                Keyframe(2.875f, degreeVec(11f, -13.6f, -14.9f), CATMULLROM),
                Keyframe(3.2083f, degreeVec(47.5f, 0f, 0f), CATMULLROM),
                Keyframe(3.5833f, degreeVec(55f, 0f, 0f), CATMULLROM),
                Keyframe(3.7917f, degreeVec(4f, 16f, 11f), CATMULLROM),
                Keyframe(4.125f, degreeVec(47.5f, 0f, 0f), CATMULLROM),
                Keyframe(4.4167f, degreeVec(55f, -8f, -6f), CATMULLROM),
                Keyframe(4.5f, degreeVec(56f, 7f, 5f), CATMULLROM),
                Keyframe(4.5833f, degreeVec(55f, -8f, -6f), CATMULLROM),
                Keyframe(4.6667f, degreeVec(56f, 7f, 5f), CATMULLROM),
                Keyframe(4.75f, degreeVec(55f, -8f, -6f), CATMULLROM),
                Keyframe(4.8333f, degreeVec(56f, 7f, 5f), CATMULLROM),
                Keyframe(5f, degreeVec(65f, 0f, 0f), CATMULLROM),
                Keyframe(5.75f, degreeVec(65f, 0f, 0f), CATMULLROM),
                Keyframe(5.9167f, degreeVec(-33f, 0f, 0f), CATMULLROM),
                Keyframe(6.25f, degreeVec(0f, 0f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.625f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(1.3333f, posVec(0f, 0.1f, 0f), LINEAR),
                Keyframe(1.875f, posVec(0f, 0.1f, 0f), LINEAR),
                Keyframe(2.0833f, posVec(0f, 0.3f, 0f), LINEAR),
                Keyframe(2.2917f, posVec(0f, 0.6f, 0f), LINEAR),
                Keyframe(2.6667f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(3.2083f, posVec(0f, 0.4f, 0f), LINEAR),
                Keyframe(3.5833f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(4.125f, posVec(0f, 0.4f, 0f), LINEAR),
                Keyframe(5f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(5.75f, posVec(0f, 0.1f, 0f), LINEAR),
                Keyframe(6f, posVec(0f, 0.15f, 0f), LINEAR),
                Keyframe(6.25f, posVec(0f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(1.2083f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(1.375f, degreeVec(0f, 0f, 90f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_right", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(1.2083f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(1.2917f, posVec(-1f, 1.25f, 0f), LINEAR),
                Keyframe(1.375f, posVec(-1f, 0.5f, 0f), LINEAR),
                Keyframe(1.4583f, posVec(-1f, -2.1f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(1.2083f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(1.375f, degreeVec(0f, 0f, -90f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_left", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(1.2083f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(1.2917f, posVec(1f, 1.25f, 0f), LINEAR),
                Keyframe(1.375f, posVec(1f, 0.5f, 0f), LINEAR),
                Keyframe(1.4583f, posVec(1f, -2.1f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "leg_back_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(1.3333f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(1.5f, degreeVec(0f, 0f, 90f), LINEAR)
            )
        )
        .addAnimation(
            "leg_back_right", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(1.25f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(1.3333f, posVec(-1f, 1.25f, 0f), LINEAR),
                Keyframe(1.4167f, posVec(-1f, 0.5f, 0f), LINEAR),
                Keyframe(1.5f, posVec(-1f, -2.1f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "leg_back_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(1.3333f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(1.5f, degreeVec(0f, 0f, -90f), LINEAR)
            )
        )
        .addAnimation(
            "leg_back_left", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(1.25f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(1.3333f, posVec(1f, 1.25f, 0f), LINEAR),
                Keyframe(1.4167f, posVec(1f, 0.5f, 0f), LINEAR),
                Keyframe(1.5f, posVec(1f, -2.1f, 0f), LINEAR)
            )
        )
        .build()

    val POSE_PLEAD: AnimationDefinition = AnimationDefinition.Builder.withLength(0f)
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(-45f, 0f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0f, 1f), CATMULLROM)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(45f, 0f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 1f, 1f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(-14.3433f, -28.2349f, -14.3433f), CATMULLROM)
            )
        )
        .addAnimation(
            "leg_front_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(-14.3433f, 28.2349f, 14.3433f), CATMULLROM)
            )
        )
        .addAnimation(
            "tail", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(45f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "back_legs", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0f, -4f), LINEAR)
            )
        )
        .addAnimation(
            "front_legs", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(-45f, 0f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "front_legs", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0f, 1f), CATMULLROM)
            )
        )
        .build()

    val POSE_SLEEP: AnimationDefinition = AnimationDefinition.Builder.withLength(0.3333f)
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 45f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, -3f, -1f), LINEAR)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, -45f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(1f, -0.999f, 1f), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_right", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(0f, scaleVec(0.0, 0.0, 0.0), LINEAR)
            )
        )
        .addAnimation(
            "leg_front_left", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(0f, scaleVec(0.0, 0.0, 0.0), LINEAR)
            )
        )
        .addAnimation(
            "leg_back_right", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(0f, scaleVec(0.0, 0.0, 0.0), LINEAR)
            )
        )
        .addAnimation(
            "leg_back_left", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(0f, scaleVec(0.0, 0.0, 0.0), LINEAR)
            )
        )
        .addAnimation(
            "tail", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(202.5f, 45f, -180f), LINEAR)
            )
        )
        .addAnimation(
            "tail", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(2f, -3.001f, 0.75f), LINEAR)
            )
        )
        .build()

    val POSE_SIT: AnimationDefinition = AnimationDefinition.Builder.withLength(0f)
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(-67.5f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, -2f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(67.5f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 2f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "tail", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(90f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "tail", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, -1f, -1f), LINEAR)
            )
        )
        .addAnimation(
            "back_legs", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(-90f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "back_legs", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, -2f, -7f), LINEAR)
            )
        )
        .addAnimation(
            "front_legs", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(-22.5f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "front_legs", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 2f, 0f), LINEAR)
            )
        )
        .build()

    val ROOMBA_TRANSFORM: AnimationDefinition = AnimationDefinition.Builder.withLength(0f)
        .addAnimation(
            "raccoon", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(0f, scaleVec(1.5, 0.33, 1.0), LINEAR)
            )
        )
        .build()

    val BABY_TRANSFORM: AnimationDefinition = AnimationDefinition.Builder.withLength(0f)
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(0f, scaleVec(1.2, 1.2, 1.2), LINEAR)
            )
        )
        .build()
}