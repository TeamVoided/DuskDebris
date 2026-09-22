package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.client.model.AgeableListModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.model.geom.builders.PartDefinition
import org.teamvoided.dusk_debris.entity.RaccoonEntity

class RaccoonEntityModel(val root: ModelPart) : AgeableListModel<RaccoonEntity>() {

    override fun headParts(): Iterable<ModelPart?> {
        return listOf()
    }

    override fun bodyParts(): Iterable<ModelPart?> {
        return listOf(root)
    }

    override fun setupAnim(
        entity: RaccoonEntity,
        limbAngle: Float,
        limbDistance: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
    }

    companion object {
        val texturedModelData: LayerDefinition
            get() {
                val modelData = MeshDefinition()
                val modelPartData = modelData.root

                val body: PartDefinition = modelPartData.addOrReplaceChild(
                    "body",
                    CubeListBuilder().texOffs(19, 12)
                        .addBox(-4F, -6F, -4F, 8F, 11F, 8F),
                    PartPose.offsetAndRotation(0F, 17F, 3F, 1.5708f, 0F, 0F)
                )

                body.addOrReplaceChild(
                    "head",
                    CubeListBuilder().texOffs(1, 5)
                        .addBox(-3F, -2F, -5F, 6F, 5F, 5F)
                        .texOffs(3, 1).addBox(-3F, -4F, -4F, 2F, 2F, 1F)
                        .texOffs(11, 1).addBox(1F, -4F, -4F, 2F, 2F, 1F)
                        .texOffs(6, 16).addBox(-1F, 1F, -7F, 2F, 2F, 2F),
                    PartPose.offsetAndRotation(0F, -6F, 0F, -1.5708f, 0F, 0F)
                )

                body.addOrReplaceChild(
                    "tail",
                    CubeListBuilder().texOffs(47, 1)
                        .addBox(-2F, -1.9239f, -2.3827f, 4F, 11F, 4F),
                    PartPose.offsetAndRotation(0F, 6F, 1F, -0.3927f, 0F, 0F)
                )

                body.addOrReplaceChild(
                    "leg_front_right",
                    CubeListBuilder().texOffs(1, 25)
                        .addBox(-1F, -1F, -1F, 2F, 4F, 2F),
                    PartPose.offsetAndRotation(-2F, -4F, -4F, -1.5708f, 0F, 0F)
                )

                body.addOrReplaceChild(
                    "leg_front_left",
                    CubeListBuilder().texOffs(10, 25)
                        .addBox(-1F, -1F, -1F, 2F, 4F, 2F),
                    PartPose.offsetAndRotation(2F, -4F, -4F, -1.5708f, 0F, 0F)
                )

                modelPartData.addOrReplaceChild(
                    "leg_back_right",
                    CubeListBuilder().texOffs(1, 25)
                        .addBox(-1F, -1F, -1F, 2F, 4F, 2F),
                    PartPose.offset(-2F, 21F, 6F)
                )

                modelPartData.addOrReplaceChild(
                    "leg_back_left",
                    CubeListBuilder().texOffs(10, 25)
                        .addBox(-1F, -1F, -1F, 2F, 4F, 2F),
                    PartPose.offset(2F, 21F, 6F)
                )

                return LayerDefinition.create(modelData, 64, 32)
            }
    }
}