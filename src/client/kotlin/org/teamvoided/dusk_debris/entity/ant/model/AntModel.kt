package org.teamvoided.dusk_debris.entity.ant.model

import net.minecraft.client.model.HierarchicalModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import org.teamvoided.dusk_debris.entity.AntEntity
import org.teamvoided.dusk_debris.entity.ant.animation.AntAnimations

@Suppress("unused")
class AntModel(root: ModelPart) : HierarchicalModel<AntEntity>() {
    private val ant: ModelPart = root.getChild("ant")
    private val abdomen: ModelPart = this.ant.getChild("abdomen")
    private val head: ModelPart = this.abdomen.getChild("head")
    private val mandibles: ModelPart = this.head.getChild("mandibles")
    private val rightMandible: ModelPart = this.mandibles.getChild("right_mandible")
    private val leftMandible: ModelPart = this.mandibles.getChild("left_mandible")
    private val antennae: ModelPart = this.head.getChild("antennae")
    private val rightAntenna: ModelPart = this.antennae.getChild("right_antenna")
    private val leftAntenna: ModelPart = this.antennae.getChild("left_antenna")
    private val legs: ModelPart = this.abdomen.getChild("legs")
    private val rightLegs: ModelPart = this.legs.getChild("right_legs")
    private val frontRight: ModelPart = this.rightLegs.getChild("front_right")
    private val backRight: ModelPart = this.rightLegs.getChild("back_right")
    private val middleRight: ModelPart = this.rightLegs.getChild("middle_right")
    private val leftLegs: ModelPart = this.legs.getChild("left_legs")
    private val frontLeft: ModelPart = this.leftLegs.getChild("front_left")
    private val middleLeft: ModelPart = this.leftLegs.getChild("middle_left")
    private val backLeft: ModelPart = this.leftLegs.getChild("back_left")
    private val thorax: ModelPart = this.abdomen.getChild("thorax")

    override fun setupAnim(
        entity: AntEntity,
        limbAngle: Float,
        limbDistance: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        root().allParts.forEach(ModelPart::resetPose)
        this.animateWalk(AntAnimations.WALK, limbAngle, limbDistance, 16.5f, 2.5f)
    }

    override fun root(): ModelPart = ant

    companion object {
        val texturedModelData: LayerDefinition
            get() {
                val meshDefinition = MeshDefinition()
                val partDefinition = meshDefinition.root

                val ant = partDefinition.addOrReplaceChild(
                    "ant",
                    CubeListBuilder.create(),
                    PartPose.offset(0.0f, 24.0f, 0.0f)
                )

                val abdomen = ant.addOrReplaceChild(
                    "abdomen",
                    CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-3.0f, -7.0f, -6.0f, 6.0f, 4.0f, 12.0f, CubeDeformation(0.0f)),
                    PartPose.offset(0.0f, 0.0f, 0.0f)
                )

                val head = abdomen.addOrReplaceChild(
                    "head",
                    CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0f, -5.0f, -8.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                    PartPose.offset(0.0f, -5.0f, -6.0f)
                )

                val mandibles =
                    head.addOrReplaceChild("mandibles", CubeListBuilder.create(), PartPose.offset(0.0f, 2.0f, -8.0f))

                val rightMandible = mandibles.addOrReplaceChild(
                    "right_mandible",
                    CubeListBuilder.create().texOffs(56, 0).mirror()
                        .addBox(-2.0f, -1.0f, -2.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)).mirror(false),
                    PartPose.offset(3.0f, 0.0f, 0.0f)
                )

                val leftMandible = mandibles.addOrReplaceChild(
                    "left_mandible",
                    CubeListBuilder.create().texOffs(56, 0)
                        .addBox(0.0f, -1.0f, -2.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                    PartPose.offset(-3.0f, 0.0f, 0.0f)
                )

                val antennae =
                    head.addOrReplaceChild("antennae", CubeListBuilder.create(), PartPose.offset(0.0f, -5.0f, -8.0f))

                val rightAntenna = antennae.addOrReplaceChild(
                    "right_antenna",
                    CubeListBuilder.create().texOffs(26, -8)
                        .addBox(0.0f, -3.0f, -8.0f, 0.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                    PartPose.offset(-2.0f, 0.0f, 0.0f)
                )

                val leftAntenna = antennae.addOrReplaceChild(
                    "left_antenna",
                    CubeListBuilder.create().texOffs(26, -8).mirror()
                        .addBox(0.0f, -3.0f, -8.0f, 0.0f, 4.0f, 8.0f, CubeDeformation(0.0f)).mirror(false),
                    PartPose.offset(2.0f, 0.0f, 0.0f)
                )

                val legs =
                    abdomen.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0f, -4.0f, 0.0f))

                val rightLegs =
                    legs.addOrReplaceChild("right_legs", CubeListBuilder.create(), PartPose.offset(-3.0f, 0.0f, 0.0f))

                val frontRight = rightLegs.addOrReplaceChild(
                    "front_right",
                    CubeListBuilder.create().texOffs(40, 28)
                        .addBox(-9.0f, -1.0f, -1.0f, 10.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                    PartPose.offsetAndRotation(0.0f, 0.0f, -3.5f, 0.0f, -0.3927f, -0.3927f)
                )

                val backRight = rightLegs.addOrReplaceChild(
                    "back_right",
                    CubeListBuilder.create().texOffs(40, 28)
                        .addBox(-9.0f, -1.0f, -1.0f, 10.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                    PartPose.offsetAndRotation(0.0f, 0.0f, 3.5f, 0.0f, 0.3927f, -0.3927f)
                )

                val middleRight = rightLegs.addOrReplaceChild(
                    "middle_right",
                    CubeListBuilder.create().texOffs(40, 28)
                        .addBox(-9.0f, -1.0f, -1.0f, 10.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                    PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.3927f)
                )

                val leftLegs =
                    legs.addOrReplaceChild("left_legs", CubeListBuilder.create(), PartPose.offset(3.0f, 0.0f, 0.0f))

                val frontLeft = leftLegs.addOrReplaceChild(
                    "front_left",
                    CubeListBuilder.create().texOffs(40, 28).mirror()
                        .addBox(-1.0f, -1.0f, -1.0f, 10.0f, 2.0f, 2.0f, CubeDeformation(0.0f)).mirror(false),
                    PartPose.offsetAndRotation(0.0f, 0.0f, -3.5f, 0.0f, 0.3927f, 0.3927f)
                )

                val middleLeft = leftLegs.addOrReplaceChild(
                    "middle_left",
                    CubeListBuilder.create().texOffs(40, 28).mirror()
                        .addBox(-1.0f, -1.0f, -1.0f, 10.0f, 2.0f, 2.0f, CubeDeformation(0.0f)).mirror(false),
                    PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.3927f)
                )

                val backLeft = leftLegs.addOrReplaceChild(
                    "back_left",
                    CubeListBuilder.create().texOffs(40, 28).mirror()
                        .addBox(-1.0f, -1.0f, -1.0f, 10.0f, 2.0f, 2.0f, CubeDeformation(0.0f)).mirror(false),
                    PartPose.offsetAndRotation(0.0f, 0.0f, 3.5f, 0.0f, -0.3927f, 0.3927f)
                )

                val thorax = abdomen.addOrReplaceChild(
                    "thorax",
                    CubeListBuilder.create().texOffs(24, 7)
                        .addBox(-5.0f, -6.0f, 0.0f, 10.0f, 10.0f, 10.0f, CubeDeformation(0.0f)),
                    PartPose.offset(0.0f, -5.0f, 6.0f)
                )

                return LayerDefinition.create(meshDefinition, 64, 32)
            }
    }
}