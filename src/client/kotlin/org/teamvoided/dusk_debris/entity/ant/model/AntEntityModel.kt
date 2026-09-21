package org.teamvoided.dusk_debris.entity.ant.model

import net.minecraft.client.model.HierarchicalModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.util.Mth
import org.joml.Vector3f
import org.teamvoided.dusk_debris.entity.AntEntity
import org.teamvoided.dusk_debris.entity.ant.animation.AntAnimations

class AntEntityModel(private val root: ModelPart) : HierarchicalModel<AntEntity>() {
    val ant: ModelPart = root.getChild("ant")
    val abdomen: ModelPart = this.ant.getChild("abdomen")
    val head: ModelPart = this.abdomen.getChild("head")
    val mandibles: ModelPart = this.head.getChild("mandibles")
    val rightMandible: ModelPart = this.mandibles.getChild("right_mandible")
    val leftMandible: ModelPart = this.mandibles.getChild("left_mandible")
    val antennae: ModelPart = this.head.getChild("antennae")
    val rightAntenna: ModelPart = this.antennae.getChild("right_antenna")
    val leftAntenna: ModelPart = this.antennae.getChild("left_antenna")
    val legs: ModelPart = this.abdomen.getChild("legs")
    val rightLegs: ModelPart = this.legs.getChild("right_legs")
    val frontRight: ModelPart = this.rightLegs.getChild("front_right")
    val backRight: ModelPart = this.rightLegs.getChild("back_right")
    val middleRight: ModelPart = this.rightLegs.getChild("middle_right")
    val leftLegs: ModelPart = this.legs.getChild("left_legs")
    val frontLeft: ModelPart = this.leftLegs.getChild("front_left")
    val middleLeft: ModelPart = this.leftLegs.getChild("middle_left")
    val backLeft: ModelPart = this.leftLegs.getChild("back_left")
    val thorax: ModelPart = this.abdomen.getChild("thorax")

    override fun setupAnim(
        entity: AntEntity,
        limbAngle: Float,
        limbDistance: Float,
        tickProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        root().allParts.forEach(ModelPart::resetPose)
        if (entity.state == AntEntity.TUNNELING_STATE) {
            root.y += 160
        }

        head.xRot = headPitch * (Math.PI.toFloat() / 180f)
        head.yRot = headYaw * (Math.PI.toFloat() / 180f)
        animateWalk(AntAnimations.WALK, limbAngle, limbDistance, 9f, 1f)
        swayers(limbAngle, limbDistance, tickProgress)
        animate(entity.emergeAnimationState, AntAnimations.EMERGE, tickProgress, 1f)
        animate(entity.diggingAnimationState, AntAnimations.DIG, tickProgress, 1f)
    }

    private fun swayers(limbAngle: Float, limbDistance: Float, tickProgress: Float) {
        val cosin: Float = tickProgress * 0.1f + limbAngle * 0.5f
        val mult: Float = 0.5f + limbDistance * 0.04f
        rightAntenna.xRot = Mth.cos(cosin) * mult
        leftAntenna.xRot = Mth.cos(cosin * 1.2f) * mult
        rightAntenna.yRot = Mth.cos(cosin * 0.9f) * mult * 0.2f
        leftAntenna.yRot = Mth.cos(cosin * 1.1f) * mult * 0.2f
        rightMandible.yRot = Mth.cos(cosin * 0.8f) * 0.1f
        leftMandible.yRot = Mth.cos(cosin * 0.6f) * 0.1f
    }


    override fun root(): ModelPart = root

    companion object {
        val texturedModelData: LayerDefinition
            get() {
                val meshDefinition = MeshDefinition()
                val partDefinition = meshDefinition.root

                val ant = partDefinition.addOrReplaceChild(
                    "ant",
                    CubeListBuilder.create(),
                    PartPose.offset(0f, 24f, 0f)
                )

                val abdomen = ant.addOrReplaceChild(
                    "abdomen",
                    CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-3f, -7f, -6f, 6f, 4f, 12f, CubeDeformation(0f)),
                    PartPose.offset(0f, 0f, 0f)
                )

                val head = abdomen.addOrReplaceChild(
                    "head",
                    CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4f, -5f, -8f, 8f, 8f, 8f, CubeDeformation(0f)),
                    PartPose.offset(0f, -5f, -6f)
                )

                val mandibles =
                    head.addOrReplaceChild("mandibles", CubeListBuilder.create(), PartPose.offset(0f, 2f, -8f))

                val rightMandible = mandibles.addOrReplaceChild(
                    "right_mandible",
                    CubeListBuilder.create().texOffs(56, 0).mirror()
                        .addBox(-2f, -1f, -2f, 2f, 2f, 2f, CubeDeformation(0f)).mirror(false),
                    PartPose.offset(3f, 0f, 0f)
                )

                val leftMandible = mandibles.addOrReplaceChild(
                    "left_mandible",
                    CubeListBuilder.create().texOffs(56, 0)
                        .addBox(0f, -1f, -2f, 2f, 2f, 2f, CubeDeformation(0f)),
                    PartPose.offset(-3f, 0f, 0f)
                )

                val antennae =
                    head.addOrReplaceChild("antennae", CubeListBuilder.create(), PartPose.offset(0f, -5f, -8f))

                val rightAntenna = antennae.addOrReplaceChild(
                    "right_antenna",
                    CubeListBuilder.create().texOffs(26, -8)
                        .addBox(0f, -3f, -8f, 0f, 4f, 8f, CubeDeformation(0f)),
                    PartPose.offset(-2f, 0f, 0f)
                )

                val leftAntenna = antennae.addOrReplaceChild(
                    "left_antenna",
                    CubeListBuilder.create().texOffs(26, -8).mirror()
                        .addBox(0f, -3f, -8f, 0f, 4f, 8f, CubeDeformation(0f)).mirror(false),
                    PartPose.offset(2f, 0f, 0f)
                )

                val legs =
                    abdomen.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0f, -4f, 0f))

                val rightLegs =
                    legs.addOrReplaceChild("right_legs", CubeListBuilder.create(), PartPose.offset(-3f, 0f, 0f))

                val frontRight = rightLegs.addOrReplaceChild(
                    "front_right",
                    CubeListBuilder.create().texOffs(40, 28)
                        .addBox(-9f, -1f, -1f, 10f, 2f, 2f, CubeDeformation(0f)),
                    PartPose.offsetAndRotation(0f, 0f, -3.5f, 0f, -0.3927f, -0.3927f)
                )

                val backRight = rightLegs.addOrReplaceChild(
                    "back_right",
                    CubeListBuilder.create().texOffs(40, 28)
                        .addBox(-9f, -1f, -1f, 10f, 2f, 2f, CubeDeformation(0f)),
                    PartPose.offsetAndRotation(0f, 0f, 3.5f, 0f, 0.3927f, -0.3927f)
                )

                val middleRight = rightLegs.addOrReplaceChild(
                    "middle_right",
                    CubeListBuilder.create().texOffs(40, 28)
                        .addBox(-9f, -1f, -1f, 10f, 2f, 2f, CubeDeformation(0f)),
                    PartPose.offsetAndRotation(0f, 0f, 0f, 0f, 0f, -0.3927f)
                )

                val leftLegs =
                    legs.addOrReplaceChild("left_legs", CubeListBuilder.create(), PartPose.offset(3f, 0f, 0f))

                val frontLeft = leftLegs.addOrReplaceChild(
                    "front_left",
                    CubeListBuilder.create().texOffs(40, 28).mirror()
                        .addBox(-1f, -1f, -1f, 10f, 2f, 2f, CubeDeformation(0f)).mirror(false),
                    PartPose.offsetAndRotation(0f, 0f, -3.5f, 0f, 0.3927f, 0.3927f)
                )

                val middleLeft = leftLegs.addOrReplaceChild(
                    "middle_left",
                    CubeListBuilder.create().texOffs(40, 28).mirror()
                        .addBox(-1f, -1f, -1f, 10f, 2f, 2f, CubeDeformation(0f)).mirror(false),
                    PartPose.offsetAndRotation(0f, 0f, 0f, 0f, 0f, 0.3927f)
                )

                val backLeft = leftLegs.addOrReplaceChild(
                    "back_left",
                    CubeListBuilder.create().texOffs(40, 28).mirror()
                        .addBox(-1f, -1f, -1f, 10f, 2f, 2f, CubeDeformation(0f)).mirror(false),
                    PartPose.offsetAndRotation(0f, 0f, 3.5f, 0f, -0.3927f, 0.3927f)
                )

                val thorax = abdomen.addOrReplaceChild(
                    "thorax",
                    CubeListBuilder.create().texOffs(24, 7)
                        .addBox(-5f, -6f, 0f, 10f, 10f, 10f, CubeDeformation(0f)),
                    PartPose.offset(0f, -5f, 6f)
                )

                return LayerDefinition.create(meshDefinition, 64, 32)
            }
    }
}