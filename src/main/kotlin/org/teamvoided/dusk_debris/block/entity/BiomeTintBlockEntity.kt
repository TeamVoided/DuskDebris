package org.teamvoided.dusk_debris.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.WolfVariant
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.teamvoided.dusk_debris.init.DuskBlockEntities.BIOME_TINTER
import org.teamvoided.dusk_debris.init.DuskBlockEntities.STATUE
import org.teamvoided.dusk_debris.util.key
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull

class BiomeTintBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(BIOME_TINTER, pos, state) {
    var biome: Holder<Biome>? = null

    override fun saveAdditional(nbt: CompoundTag, lookupProvider: HolderLookup.Provider?) {
        super.saveAdditional(nbt, lookupProvider)
        if (biome != null) {
            nbt.putString("biome", biome!!.unwrapKey().get().location().toString())
        }
    }

    override fun loadAdditional(nbt: CompoundTag, lookupProvider: HolderLookup.Provider?) {
        super.loadAdditional(nbt, lookupProvider)
        if (nbt.contains("biome")){
            biome = level?.registryAccess()?.registry(Registries.BIOME)?.getOrNull()
                ?.getHolder(Registries.BIOME.key(ResourceLocation.parse(nbt.getString("biome"))))?.getOrNull()
        }
    }

    override fun getUpdateTag(lookupProvider: HolderLookup.Provider): CompoundTag {
        return this.saveCustomOnly(lookupProvider)
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

}