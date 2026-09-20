package org.teamvoided.dusk_debris.entity.ant.animation

import net.minecraft.client.animation.AnimationChannel
import net.minecraft.client.animation.AnimationChannel.Interpolations.*
import net.minecraft.client.animation.AnimationDefinition
import net.minecraft.client.animation.Keyframe
import net.minecraft.client.animation.KeyframeAnimations.*

object AntAnimations {
    val WALK: AnimationDefinition = AnimationDefinition.Builder.withLength(1.5f).looping()
        .addAnimation(
            "abdomen", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.375f, posVec(0f, 1f, 0f), CATMULLROM),
                Keyframe(0.75f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(1.125f, posVec(0f, 1f, 0f), CATMULLROM),
                Keyframe(1.5f, posVec(0f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.25f, degreeVec(1f, 0f, 0f), CATMULLROM),
                Keyframe(0.625f, degreeVec(-1f, 0f, 0f), CATMULLROM),
                Keyframe(0.9167f, degreeVec(1f, 0f, 0f), CATMULLROM),
                Keyframe(1.2917f, degreeVec(-1f, 0f, 0f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "antennae", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.25f, degreeVec(3f, 0f, 0f), CATMULLROM),
                Keyframe(0.625f, degreeVec(-3f, 0f, 0f), CATMULLROM),
                Keyframe(0.9167f, degreeVec(3f, 0f, 0f), CATMULLROM),
                Keyframe(1.2917f, degreeVec(-3f, 0f, 0f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "front_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, -15f, 0f), LINEAR),
                Keyframe(0.375f, degreeVec(0f, 0f, -10f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0f, 15f, 0f), LINEAR),
                Keyframe(1.125f, degreeVec(0f, 0f, 10f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, -15f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "back_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, -15f, 0f), LINEAR),
                Keyframe(0.375f, degreeVec(0f, 0f, -10f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0f, 15f, 0f), LINEAR),
                Keyframe(1.125f, degreeVec(0f, 0f, 10f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, -15f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "middle_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 15f, 0f), LINEAR),
                Keyframe(0.375f, degreeVec(0f, 0f, 10f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0f, -15f, 0f), LINEAR),
                Keyframe(1.125f, degreeVec(0f, 0f, -10f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, 15f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "front_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, -15f, 0f), LINEAR),
                Keyframe(0.375f, degreeVec(0f, 0f, -10f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0f, 15f, 0f), LINEAR),
                Keyframe(1.125f, degreeVec(0f, 0f, 10f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, -15f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "middle_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 15f, 0f), LINEAR),
                Keyframe(0.375f, degreeVec(0f, 0f, 10f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0f, -15f, 0f), LINEAR),
                Keyframe(1.125f, degreeVec(0f, 0f, -10f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, 15f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "back_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, -15f, 0f), LINEAR),
                Keyframe(0.375f, degreeVec(0f, 0f, -10f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0f, 15f, 0f), LINEAR),
                Keyframe(1.125f, degreeVec(0f, 0f, 10f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, -15f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "thorax", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.25f, degreeVec(-3f, 0f, 0f), CATMULLROM),
                Keyframe(0.625f, degreeVec(3f, 0f, 0f), CATMULLROM),
                Keyframe(0.9167f, degreeVec(-3f, 0f, 0f), CATMULLROM),
                Keyframe(1.2917f, degreeVec(3f, 0f, 0f), CATMULLROM),
                Keyframe(1.5f, degreeVec(0f, 0f, 0f), LINEAR)
            )
        )
        .build()

    val PICK_UP: AnimationDefinition = AnimationDefinition.Builder.withLength(0.4167f)
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.125f, degreeVec(7.93f, 0f, 0f), LINEAR),
                Keyframe(0.3333f, degreeVec(-22.5f, 0f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "right_mandible", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.3333f, degreeVec(0f, 20f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "left_mandible", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.3333f, degreeVec(0f, -20f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "antennae", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.125f, degreeVec(-20f, 0f, 0f), LINEAR),
                Keyframe(0.25f, degreeVec(10f, 0f, 0f), CATMULLROM),
                Keyframe(0.3333f, degreeVec(0f, 0f, 0f), CATMULLROM)
            )
        )
        .build()

    val DIG: AnimationDefinition = AnimationDefinition.Builder.withLength(1f)
        .addAnimation(
            "abdomen", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(0.25f, degreeVec(7.5f, 0f, 0f), LINEAR),
                Keyframe(0.5f, degreeVec(7.5f, 0f, 0f), LINEAR),
                Keyframe(0.75f, degreeVec(80f, 0f, 0f), CATMULLROM),
                Keyframe(1f, degreeVec(85f, 0f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "abdomen", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0f, posVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(0.25f, posVec(0f, 0f, 8f), LINEAR),
                Keyframe(0.625f, posVec(0f, 0f, 8f), LINEAR),
                Keyframe(0.75f, posVec(0f, -2f, 6f), CATMULLROM),
                Keyframe(1f, posVec(0f, -10f, 6f), LINEAR)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0833f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(0.4583f, degreeVec(-25f, 0f, 0f), LINEAR),
                Keyframe(0.625f, degreeVec(45f, 0f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.6667f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(1f, posVec(0f, 0f, 2f), LINEAR)
            )
        )
        .addAnimation(
            "antennae", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(0.25f, degreeVec(-30f, 0f, 0f), LINEAR),
                Keyframe(0.3333f, degreeVec(-60f, 0f, 0f), LINEAR),
                Keyframe(0.375f, degreeVec(-60f, 0f, 0f), LINEAR),
                Keyframe(0.4583f, degreeVec(60f, 0f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "front_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.0417f, degreeVec(0f, -10f, -5f), LINEAR),
                Keyframe(0.125f, degreeVec(0f, -20f, 10f), CATMULLROM),
                Keyframe(0.2917f, degreeVec(10.7157f, -20.4515f, -2.9242f), LINEAR),
                Keyframe(0.5f, degreeVec(0f, -50f, 30f), CATMULLROM),
                Keyframe(0.6667f, degreeVec(0f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "back_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.2083f, degreeVec(0f, -40f, 0f), LINEAR),
                Keyframe(0.3333f, degreeVec(0f, -10f, 20f), CATMULLROM),
                Keyframe(0.4167f, degreeVec(0f, -10f, -10f), LINEAR),
                Keyframe(0.5f, degreeVec(0f, -10f, -10f), LINEAR),
                Keyframe(0.625f, degreeVec(-6f, -12f, -40f), LINEAR),
                Keyframe(0.9167f, degreeVec(0f, 35f, -10f), LINEAR)
            )
        )
        .addAnimation(
            "back_right", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.875f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(1f, posVec(0f, 0f, -1f), LINEAR)
            )
        )
        .addAnimation(
            "middle_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(0.0417f, degreeVec(0f, -20f, 0f), CATMULLROM),
                Keyframe(0.125f, degreeVec(0f, -12.5f, 12.5f), CATMULLROM),
                Keyframe(0.25f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.5417f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.875f, degreeVec(0f, 50f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "front_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.0417f, degreeVec(0f, 10f, 5f), LINEAR),
                Keyframe(0.125f, degreeVec(0f, 20f, -10f), CATMULLROM),
                Keyframe(0.2917f, degreeVec(10.7157f, 20.4515f, 2.9242f), LINEAR),
                Keyframe(0.5f, degreeVec(0f, 50f, -30f), CATMULLROM),
                Keyframe(0.6667f, degreeVec(0f, 0f, 0f), LINEAR)
            )
        )
        .addAnimation(
            "middle_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(0.0417f, degreeVec(0f, 20f, 0f), CATMULLROM),
                Keyframe(0.125f, degreeVec(0f, 12.5f, -12.5f), CATMULLROM),
                Keyframe(0.25f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.5417f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.875f, degreeVec(0f, -50f, 0f), CATMULLROM)
            )
        )
        .addAnimation(
            "back_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
                Keyframe(0.2083f, degreeVec(0f, 40f, 0f), LINEAR),
                Keyframe(0.3333f, degreeVec(0f, 10f, -20f), CATMULLROM),
                Keyframe(0.4167f, degreeVec(0f, 10f, 10f), LINEAR),
                Keyframe(0.5f, degreeVec(0f, 10f, 10f), LINEAR),
                Keyframe(0.625f, degreeVec(-6f, 12f, 40f), LINEAR),
                Keyframe(0.9167f, degreeVec(0f, -35f, 10f), LINEAR)
            )
        )
        .addAnimation(
            "back_left", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.875f, posVec(0f, 0f, 0f), LINEAR),
                Keyframe(1f, posVec(0f, 0f, -1f), LINEAR)
            )
        )
        .addAnimation(
            "thorax", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.875f, posVec(0f, 0f, 0f), CATMULLROM),
                Keyframe(1f, posVec(0f, 0f, -6f), CATMULLROM)
            )
        )
        .build()
    
    val EMERGE: AnimationDefinition? = AnimationDefinition.Builder.withLength(0.75f)
        .addAnimation(
            "abdomen", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.2083f, degreeVec(-85.0f, 0.0f, 0.0f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0.0f, 0.0f, 0.0f), CATMULLROM)
            )
        )
        .addAnimation(
            "abdomen", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.125f, posVec(0.0f, -12.0f, 6.0f), CATMULLROM),
                Keyframe(0.4167f, posVec(0.0f, 0.0f, 5.0f), LINEAR),
                Keyframe(0.7083f, posVec(0.0f, 0.0f, 0.0f), LINEAR)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.125f, degreeVec(22.5f, 0.0f, 0.0f), CATMULLROM),
                Keyframe(0.4167f, degreeVec(0.0f, 0.0f, 0.0f), CATMULLROM)
            )
        )
        .addAnimation(
            "head", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, posVec(0.0f, 0.0f, 5.0f), LINEAR),
                Keyframe(0.2917f, posVec(0.0f, 0.0f, 0.0f), LINEAR)
            )
        )
        .addAnimation(
            "antennae", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0833f, degreeVec(80.0f, 0.0f, 0.0f), CATMULLROM),
                Keyframe(0.5f, degreeVec(-20.0f, 0.0f, 0.0f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0.0f, 0.0f, 0.0f), CATMULLROM)
            )
        )
        .addAnimation(
            "front_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.25f, degreeVec(0.0f, -40.0f, 0.0f), CATMULLROM),
                Keyframe(0.4583f, degreeVec(0.0f, 20.0f, -30.0f), CATMULLROM),
                Keyframe(0.5833f, degreeVec(0.0f, 20.0f, -10.0f), CATMULLROM),
                Keyframe(0.6667f, degreeVec(0.0f, 10.0f, 10.0f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0.0f, 0.0f, 0.0f), CATMULLROM)
            )
        )
        .addAnimation(
            "back_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.5833f, degreeVec(0.0f, 35.0f, -10.0f), LINEAR),
                Keyframe(0.6667f, degreeVec(-5.0f, 35.0f, 10.0f), LINEAR),
                Keyframe(0.75f, degreeVec(0.0f, 0.0f, 0.0f), LINEAR)
            )
        )
        .addAnimation(
            "back_right", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.375f, posVec(0.0f, 0.0f, -3.0f), LINEAR),
                Keyframe(0.75f, posVec(0.0f, 0.0f, 0.0f), LINEAR)
            )
        )
        .addAnimation(
            "middle_right", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.3333f, degreeVec(0.0f, -45.0f, 0.0f), LINEAR),
                Keyframe(0.5f, degreeVec(-20.0f, 10.0f, 0.0f), LINEAR),
                Keyframe(0.625f, degreeVec(0.0f, -15.0f, 10.0f), CATMULLROM),
                Keyframe(0.7083f, degreeVec(0.0f, 0.0f, 0.0f), CATMULLROM)
            )
        )
        .addAnimation(
            "front_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.25f, degreeVec(0.0f, 40.0f, 0.0f), CATMULLROM),
                Keyframe(0.4583f, degreeVec(0.0f, -20.0f, 30.0f), CATMULLROM),
                Keyframe(0.5833f, degreeVec(0.0f, -20.0f, 10.0f), CATMULLROM),
                Keyframe(0.6667f, degreeVec(0.0f, -10.0f, -10.0f), CATMULLROM),
                Keyframe(0.75f, degreeVec(0.0f, 0.0f, 0.0f), CATMULLROM)
            )
        )
        .addAnimation(
            "middle_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.3333f, degreeVec(0.0f, 45.0f, 0.0f), LINEAR),
                Keyframe(0.5f, degreeVec(-20.0f, -10.0f, 0.0f), LINEAR),
                Keyframe(0.625f, degreeVec(0.0f, 15.0f, -10.0f), CATMULLROM),
                Keyframe(0.7083f, degreeVec(0.0f, 0.0f, 0.0f), CATMULLROM)
            )
        )
        .addAnimation(
            "back_left", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.5833f, degreeVec(0.0f, -35.0f, 10.0f), LINEAR),
                Keyframe(0.6667f, degreeVec(-5.0f, -35.0f, -10.0f), LINEAR),
                Keyframe(0.75f, degreeVec(0.0f, 0.0f, 0.0f), LINEAR)
            )
        )
        .addAnimation(
            "back_left", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.375f, posVec(0.0f, 0.0f, -3.0f), LINEAR),
                Keyframe(0.75f, posVec(0.0f, 0.0f, 0.0f), LINEAR)
            )
        )
        .addAnimation(
            "thorax", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.5833f, degreeVec(-30.0f, 0.0f, 0.0f), CATMULLROM),
                Keyframe(0.6667f, degreeVec(0.0f, 0.0f, 0.0f), CATMULLROM)
            )
        )
        .addAnimation(
            "thorax", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.2917f, posVec(0.0f, -2.0f, -9.0f), LINEAR),
                Keyframe(0.4583f, posVec(0.0f, -1.0f, -3.0f), LINEAR),
                Keyframe(0.625f, posVec(0.0f, -1.0f, -1.0f), LINEAR),
                Keyframe(0.75f, posVec(0.0f, 0.0f, 0.0f), CATMULLROM)
            )
        )
        .build()
    
}