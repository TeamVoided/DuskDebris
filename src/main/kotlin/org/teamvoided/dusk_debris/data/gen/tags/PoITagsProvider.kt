package org.teamvoided.dusk_debris.data.gen.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.ai.village.poi.PoiTypes
import net.minecraft.world.level.material.Fluid
import org.teamvoided.dusk_debris.data.DuskPoITypes
import org.teamvoided.dusk_debris.data.tags.DuskPoITags
import java.util.concurrent.CompletableFuture

class PoITagsProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider<PoiType>(o, Registries.POINT_OF_INTEREST_TYPE, r) {
    override fun addTags(arg: HolderLookup.Provider) {
        duskTags()
        vanillaTags()
        conventionTags()
    }

    fun duskTags() {
        tag(DuskPoITags.RACCOON_BARREL)
            .add(PoiTypes.FISHERMAN)
        tag(DuskPoITags.WOOD_WASP_HOME)
            .add(DuskPoITypes.WOOD_WASP_NEST)
            .add(DuskPoITypes.WOOD_WASP_HIVE)
    }

    fun vanillaTags() {}

    fun conventionTags() {}


    override fun reverseLookup(element: PoiType): ResourceKey<PoiType> {
        return super.reverseLookup(element)//  element.builtInRegistryHolder().key()
    }
}