package org.teamvoided.dusk_debris.data.gen.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.ai.village.poi.PoiTypes
import org.teamvoided.dusk_debris.data.tags.DuskPoITags
import org.teamvoided.dusk_debris.init.DuskPointsOfInterests
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
            .add(DuskPointsOfInterests.WOOD_WASP_NEST)
            .add(DuskPointsOfInterests.WOOD_WASP_HIVE)
    }

    fun vanillaTags() {}

    fun conventionTags() {}


    override fun reverseLookup(element: PoiType): ResourceKey<PoiType> {
        return super.reverseLookup(element)//  element.builtInRegistryHolder().key()
    }
}