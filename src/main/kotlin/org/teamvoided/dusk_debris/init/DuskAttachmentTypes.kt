package org.teamvoided.dusk_debris.init

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import org.teamvoided.dusk_debris.DuskDebris.id
import org.teamvoided.dusk_debris.block.attachments.ExhaustData
import org.teamvoided.dusk_debris.data.gen.providers.variants.SnifferVariants
import org.teamvoided.dusk_debris.data.variants.DuskRaccoonVariants
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonVariant
import org.teamvoided.dusk_debris.entity.variant.SnifferVariant

@Suppress("UnstableApiUsage")
object DuskAttachmentTypes {
    fun init() {}


    @JvmField
    val SNIFFER_VARIANT: AttachmentType<ResourceKey<SnifferVariant>> =
        AttachmentRegistry.create(id("sniffer_variant")) { builder: AttachmentRegistry.Builder<ResourceKey<SnifferVariant>> ->
            builder
                .initializer { SnifferVariants.DEFAULT }
                .persistent(ResourceKey.codec(DuskRegistryKeys.SNIFFER_VARIANT))
                .syncWith(ResourceKey.streamCodec(DuskRegistryKeys.SNIFFER_VARIANT), AttachmentSyncPredicate.all())
        }
    val RACCOON_VARIANT: AttachmentType<ResourceKey<RaccoonVariant>> =
        AttachmentRegistry.create(id("raccoon_variant")) { builder ->
            builder
                .initializer(DuskRaccoonVariants::DEFAULT)
                .persistent(ResourceKey.codec(DuskRegistryKeys.RACCOON_VARIANT))
                .syncWith(ResourceKey.streamCodec(DuskRegistryKeys.RACCOON_VARIANT), AttachmentSyncPredicate.all())
        }

    val EXHAUST_DATA: AttachmentType<Map<BlockPos, ExhaustData>> =
        AttachmentRegistry.create(id("exhaust_data")) { builder ->
            builder.persistent(ExhaustData.MAP_CODEC)
        }
}