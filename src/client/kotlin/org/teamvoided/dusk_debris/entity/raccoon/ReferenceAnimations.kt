package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.client.animation.AnimationChannel
import net.minecraft.client.animation.AnimationChannel.Interpolations.*
import net.minecraft.client.animation.AnimationDefinition
import net.minecraft.client.animation.Keyframe
import net.minecraft.client.animation.KeyframeAnimations.*

class ReferenceAnimations {
    val SNIFFER_DIG = AnimationDefinition.Builder.withLength(8f).addAnimation(
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
    ).addAnimation(
        "body",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.3333f, posVec(0f, 1f, 0f), LINEAR),
            Keyframe(1.5f, posVec(0f, -7f, 0f), LINEAR)
        )
    ).addAnimation(
        "body",
        AnimationChannel(
            AnimationChannel.Targets.SCALE,
            Keyframe(0f, scaleVec(1.0, 1.0, 1.0), LINEAR),
            Keyframe(1.5f, scaleVec(1.0, 1.0, 1.0), LINEAR),
            Keyframe(1.5417f, scaleVec(1.04, 0.98, 1.02), LINEAR),
            Keyframe(1.5833f, scaleVec(1.0, 1.0, 1.0), LINEAR)
        )
    ).addAnimation(
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
            Keyframe(2.875f, degreeVec(10.95951f, 13.57454f, -14.93501f), CATMULLROM),
            Keyframe(3.2083f, degreeVec(47.5f, 0f, 0f), CATMULLROM),
            Keyframe(3.5833f, degreeVec(55f, 0f, 0f), CATMULLROM),
            Keyframe(3.7917f, degreeVec(4.2932f, -16.187f, 10.90042f), CATMULLROM),
            Keyframe(4.125f, degreeVec(47.5f, 0f, 0f), CATMULLROM),
            Keyframe(4.4167f, degreeVec(54.71135f, 7.98009f, -5.56662f), CATMULLROM),
            Keyframe(4.5f, degreeVec(55.72895f, -6.77684f, 4.46197f), CATMULLROM),
            Keyframe(4.5833f, degreeVec(54.71135f, 7.98009f, -5.56662f), CATMULLROM),
            Keyframe(4.6667f, degreeVec(55.72895f, -6.77684f, 4.46197f), CATMULLROM),
            Keyframe(4.75f, degreeVec(54.71135f, 7.98009f, -5.56662f), CATMULLROM),
            Keyframe(4.8333f, degreeVec(55.72895f, -6.77684f, 4.46197f), CATMULLROM),
            Keyframe(5f, degreeVec(65f, 0f, 0f), CATMULLROM),
            Keyframe(5.75f, degreeVec(65f, 0f, 0f), CATMULLROM),
            Keyframe(5.9167f, degreeVec(-32.5f, 0f, 0f), CATMULLROM),
            Keyframe(6.25f, degreeVec(0f, 0f, 0f), LINEAR)
        )
    ).addAnimation(
        "head", AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(0.625f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.375f, posVec(0f, 1f, 0f), LINEAR),
            Keyframe(1.5f, posVec(0f, 1f, 0f), LINEAR),
            Keyframe(1.5833f, posVec(0f, 1f, 0f), LINEAR),
            Keyframe(1.875f, posVec(0f, 1f, 0f), LINEAR),
            Keyframe(2.0833f, posVec(0f, 3f, 0f), LINEAR),
            Keyframe(2.2917f, posVec(0f, 6f, 0f), LINEAR),
            Keyframe(2.6667f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(3.2083f, posVec(0f, 4f, 0f), LINEAR),
            Keyframe(3.5833f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(4.125f, posVec(0f, 4f, 0f), LINEAR),
            Keyframe(5f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(5.75f, posVec(0f, 1f, 0f), LINEAR),
            Keyframe(6f, posVec(0f, 1.5f, 0f), LINEAR),
            Keyframe(6.25f, posVec(0f, 1f, 0f), LINEAR)
        )
    ).addAnimation(
        "left_ear",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, -2.5f), LINEAR),
            Keyframe(1.25f, degreeVec(0f, 0f, -2.5f), LINEAR),
            Keyframe(1.4167f, degreeVec(0f, 0f, -50f), LINEAR),
            Keyframe(1.5833f, degreeVec(0f, 0f, -30f), LINEAR),
            Keyframe(5.9167f, degreeVec(0f, 0f, -30f), LINEAR),
            Keyframe(6.0833f, degreeVec(0f, 0f, -65f), LINEAR),
            Keyframe(6.3333f, degreeVec(0f, 0f, -30f), LINEAR)
        )
    ).addAnimation(
        "right_ear",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, 2.5f), LINEAR),
            Keyframe(1.25f, degreeVec(0f, 0f, 2.5f), LINEAR),
            Keyframe(1.4167f, degreeVec(0f, 0f, 50f), LINEAR),
            Keyframe(1.5833f, degreeVec(0f, 0f, 30f), LINEAR),
            Keyframe(5.9167f, degreeVec(0f, 0f, 30f), LINEAR),
            Keyframe(6.0833f, degreeVec(0f, 0f, 65f), LINEAR),
            Keyframe(6.3333f, degreeVec(0f, 0f, 30f), LINEAR)
        )
    ).addAnimation(
        "right_front_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.2083f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.375f, degreeVec(0f, 0f, 90f), LINEAR)
        )
    ).addAnimation(
        "right_front_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.2083f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.2917f, posVec(-2f, -0.75f, 0f), LINEAR),
            Keyframe(1.375f, posVec(-4f, -5.5f, 0f), LINEAR)
        )
    ).addAnimation(
        "right_mid_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.25f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.4167f, degreeVec(0f, 0f, 90f), LINEAR)
        )
    ).addAnimation(
        "right_mid_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.25f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.3333f, posVec(-2f, -0.75f, 0f), LINEAR),
            Keyframe(1.4167f, posVec(-4f, -5.5f, 0f), LINEAR)
        )
    ).addAnimation(
        "right_hind_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.3333f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.5f, degreeVec(0f, 0f, 90f), LINEAR)
        )
    ).addAnimation(
        "right_hind_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.3333f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.4167f, posVec(-2f, -0.75f, 0f), LINEAR),
            Keyframe(1.5f, posVec(-4f, -5.5f, 0f), LINEAR)
        )
    ).addAnimation(
        "left_front_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.2083f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.375f, degreeVec(0f, 0f, -90f), LINEAR)
        )
    ).addAnimation(
        "left_front_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.2083f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.2917f, posVec(2f, -0.75f, 0f), LINEAR),
            Keyframe(1.375f, posVec(4f, -5.5f, 0f), LINEAR)
        )
    ).addAnimation(
        "left_mid_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.25f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.4167f, degreeVec(0f, 0f, -90f), LINEAR)
        )
    ).addAnimation(
        "left_mid_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.25f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.3333f, posVec(2f, -0.75f, 0f), LINEAR),
            Keyframe(1.4167f, posVec(4f, -5.5f, 0f), LINEAR)
        )
    ).addAnimation(
        "left_hind_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.3333f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.5f, degreeVec(0f, 0f, -90f), LINEAR)
        )
    ).addAnimation(
        "left_hind_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.3333f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.4167f, posVec(2f, -0.75f, 0f), LINEAR),
            Keyframe(1.5f, posVec(4f, -5.5f, 0f), LINEAR)
        )
    ).build()
    val SNIFFER_STAND_UP = AnimationDefinition.Builder.withLength(3f).addAnimation(
        "body",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0.25f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(0.75f, degreeVec(2.5f, 0f, 0f), LINEAR),
            Keyframe(1.5f, degreeVec(-2.5f, 0f, 0f), LINEAR),
            Keyframe(1.7083f, degreeVec(0f, 0f, 0f), LINEAR)
        )
    ).addAnimation(
        "body",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0.25f, posVec(0f, -7f, 0f), LINEAR),
            Keyframe(0.75f, posVec(0f, -7f, 0f), LINEAR),
            Keyframe(1.5f, posVec(0f, 0f, 0f), LINEAR),
            Keyframe(1.7083f, posVec(0f, 0f, 0f), LINEAR)
        )
    ).addAnimation(
        "head",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(0.3333f, degreeVec(-5f, 0f, 0f), LINEAR),
            Keyframe(0.7083f, degreeVec(0f, 0f, 0f), LINEAR),
            Keyframe(1f, degreeVec(10f, 0f, 0f), LINEAR),
            Keyframe(1.375f, degreeVec(0f, 0f, 0f), LINEAR)
        )
    ).addAnimation(
        "head",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0f, posVec(0f, 1f, 0f), LINEAR),
            Keyframe(1.375f, posVec(0f, 1f, 0f), LINEAR)
        )
    ).addAnimation(
        "left_ear",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, -30f), LINEAR),
            Keyframe(0.9167f, degreeVec(0f, 0f, -30f), LINEAR),
            Keyframe(1.2083f, degreeVec(0f, 0f, -5f), LINEAR)
        )
    ).addAnimation(
        "right_ear",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, 30f), LINEAR),
            Keyframe(0.9167f, degreeVec(0f, 0f, 30f), LINEAR),
            Keyframe(1.2083f, degreeVec(0f, 0f, 5f), LINEAR)
        )
    ).addAnimation(
        "right_front_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, 90f), CATMULLROM),
            Keyframe(0.4583f, degreeVec(0f, 0f, 0f), CATMULLROM)
        )
    ).addAnimation(
        "right_front_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0f, posVec(-4f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.2083f, posVec(6f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.4583f, posVec(0f, 0f, 0f), CATMULLROM)
        )
    ).addAnimation(
        "right_mid_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0.0833f, degreeVec(0f, 0f, 90f), CATMULLROM),
            Keyframe(0.5833f, degreeVec(0f, 0f, 0f), CATMULLROM)
        )
    ).addAnimation(
        "right_mid_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0.0833f, posVec(-4f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.3333f, posVec(6f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.5833f, posVec(0f, 0f, 0f), CATMULLROM)
        )
    ).addAnimation(
        "right_hind_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0.1667f, degreeVec(0f, 0f, 90f), CATMULLROM),
            Keyframe(0.6667f, degreeVec(0f, 0f, 0f), CATMULLROM)
        )
    ).addAnimation(
        "right_hind_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0.1667f, posVec(-4f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.4167f, posVec(6f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.6667f, posVec(0f, 0f, 0f), CATMULLROM)
        )
    ).addAnimation(
        "left_front_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0f, degreeVec(0f, 0f, -90f), CATMULLROM),
            Keyframe(0.4583f, degreeVec(0f, 0f, 0f), CATMULLROM)
        )
    ).addAnimation(
        "left_front_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0f, posVec(4f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.2083f, posVec(-6f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.4583f, posVec(0f, 0f, 0f), CATMULLROM)
        )
    ).addAnimation(
        "left_mid_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0.0833f, degreeVec(0f, 0f, -90f), CATMULLROM),
            Keyframe(0.5833f, degreeVec(0f, 0f, 0f), CATMULLROM)
        )
    ).addAnimation(
        "left_mid_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0.0833f, posVec(4f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.3333f, posVec(-6f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.5833f, posVec(0f, 0f, 0f), CATMULLROM)
        )
    ).addAnimation(
        "left_hind_leg",
        AnimationChannel(
            AnimationChannel.Targets.ROTATION,
            Keyframe(0.1667f, degreeVec(0f, 0f, -90f), CATMULLROM),
            Keyframe(0.6667f, degreeVec(0f, 0f, 0f), CATMULLROM)
        )
    ).addAnimation(
        "left_hind_leg",
        AnimationChannel(
            AnimationChannel.Targets.POSITION,
            Keyframe(0.1667f, posVec(4f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.4167f, posVec(-6f, -5.5f, 0f), CATMULLROM),
            Keyframe(0.6667f, posVec(0f, 0f, 0f), CATMULLROM)
        )
    ).build()
}