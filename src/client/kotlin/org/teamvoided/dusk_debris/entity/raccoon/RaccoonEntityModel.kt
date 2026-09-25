package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.client.model.AgeableHierarchicalModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import kotlin.math.sin

class RaccoonEntityModel(val root: ModelPart) : AgeableHierarchicalModel<RaccoonEntity>(8f, 3.35f) {
    val raccoon: ModelPart = root.getChild("raccoon")
    val body: ModelPart = this.raccoon.getChild("body")
    val head: ModelPart = this.body.getChild("head")
    val tail: ModelPart = this.body.getChild("tail")
    val frontLegs: ModelPart = this.raccoon.getChild("front_legs")
    val legFrontRight: ModelPart = this.frontLegs.getChild("leg_front_right")
    val legFrontLeft: ModelPart = this.frontLegs.getChild("leg_front_left")
    val backLegs: ModelPart = this.raccoon.getChild("back_legs")
    val legBackRight: ModelPart = this.backLegs.getChild("leg_back_right")
    val legBackLeft: ModelPart = this.backLegs.getChild("leg_back_left")

    override fun root(): ModelPart = root

    override fun setupAnim(
        entity: RaccoonEntity,
        limbAngle: Float,
        limbDistance: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        root.allParts.forEach(ModelPart::resetPose)
        head.xRot = headPitch * (Math.PI.toFloat() / 180f)
        head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        tail(entity, ageInTicks)
        animateWalk(RaccoonAnimation.WALK, limbAngle, limbDistance, 3f, 1f)
        animate(entity.washingAnimationState, RaccoonAnimation.RUMMAGE, ageInTicks)
        animate(entity.sneezingAnimationState, RaccoonAnimation.HEAD_SHAKE, ageInTicks)

        if (entity.state == RaccoonEntity.SITTING_STATE)
            applyStatic(RaccoonAnimation.POSE_SIT)
        else if (entity.state == RaccoonEntity.SLEEPING_STATE)
            applyStatic(RaccoonAnimation.POSE_SLEEP)

        if (entity.hasCustomName() && "roomba" == entity.name.string.lowercase())
            applyStatic(RaccoonAnimation.ROOMBA_TRANSFORM)
        //if (young)
        //    applyStatic(RaccoonAnimation.BABY_TRANSFORM)
    }

    private fun tail(entity: RaccoonEntity, ageInTicks: Float) {
        val state = entity.state
        when (state) {
            RaccoonEntity.IDLE_STATE, RaccoonEntity.SNEEZE_STATE -> if (ageInTicks % 100 < 10)
                tail.yRot += sin(ageInTicks * (Math.PI.toFloat() / 5f))

            RaccoonEntity.SITTING_STATE, RaccoonEntity.SLEEPING_STATE -> {} /* no reaction */
            RaccoonEntity.WASHING_STATE -> { /* excited ones */
                if (ageInTicks % 30 < 10) {
                    tail.yRot += sin(ageInTicks * (Math.PI.toFloat() / 5f))
                } else {
                    tail.yRot += sin((ageInTicks % 30) * (Math.PI.toFloat() / -10f))
                }
            }
        }
    }


    companion object {
        val texturedModelData: LayerDefinition
            get() {
                val meshDefinition = MeshDefinition()
                val partDefinition = meshDefinition.root

                val raccoon = partDefinition.addOrReplaceChild(
                    "raccoon",
                    CubeListBuilder.create(),
                    PartPose.offset(0f, 24f, 0f)
                )

                val body = raccoon.addOrReplaceChild(
                    "body",
                    CubeListBuilder.create().texOffs(26, 0)
                        .addBox(-4f, -3f, -6f, 8f, 8f, 11f),
                    PartPose.offset(0f, -8f, 3f)
                )

                val head = body.addOrReplaceChild(
                    "head",
                    CubeListBuilder.create().texOffs(1, 5)
                        .addBox(-3f, -4f, -5f, 6f, 5f, 5f)
                        .texOffs(3, 1).addBox(-3f, -6f, -4f, 2f, 2f, 1f)
                        .texOffs(11, 1).addBox(1f, -6f, -4f, 2f, 2f, 1f)
                        .texOffs(1, 17).addBox(-1f, -1f, -7f, 2f, 2f, 2f),
                    PartPose.offset(0f, 3f, -6f)
                )

                val tail = body.addOrReplaceChild(
                    "tail",
                    CubeListBuilder.create().texOffs(0, 17)
                        .addBox(-2f, -2f, -1f, 4f, 4f, 11f),
                    PartPose.offsetAndRotation(0f, 0f, 5f, -0.3927f, 0f, 0f)
                )

                val frontLegs =
                    raccoon.addOrReplaceChild("front_legs", CubeListBuilder.create(), PartPose.offset(0f, -8f, 3f))

                val legFrontRight = frontLegs.addOrReplaceChild(
                    "leg_front_right",
                    CubeListBuilder.create().texOffs(19, 3)
                        .addBox(-1f, -1f, -1f, 2f, 4f, 2f),
                    PartPose.offset(-2f, 5f, -4f)
                )

                val legFrontLeft = frontLegs.addOrReplaceChild(
                    "leg_front_left",
                    CubeListBuilder.create().texOffs(28, 3)
                        .addBox(-1f, -1f, -1f, 2f, 4f, 2f),
                    PartPose.offset(2f, 5f, -4f)
                )

                val backLegs =
                    raccoon.addOrReplaceChild("back_legs", CubeListBuilder.create(), PartPose.offset(0f, -3f, 6f))

                val legBackRight = backLegs.addOrReplaceChild(
                    "leg_back_right",
                    CubeListBuilder.create().texOffs(19, 3)
                        .addBox(-1f, -1f, -1f, 2f, 4f, 2f),
                    PartPose.offset(-2f, 0f, 0f)
                )

                val legBackLeft = backLegs.addOrReplaceChild(
                    "leg_back_left",
                    CubeListBuilder.create().texOffs(28, 3)
                        .addBox(-1f, -1f, -1f, 2f, 4f, 2f),
                    PartPose.offset(2f, 0f, 0f)
                )

                return LayerDefinition.create(meshDefinition, 64, 32)
            }
    }
}