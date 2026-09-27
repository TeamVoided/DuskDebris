@file:Suppress("unused")

package org.teamvoided.dusk_debris.util

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.*
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.teamvoided.dusks_and_dungeons.DusksAndDungeons.MODID
import kotlin.jvm.optionals.getOrNull

const val pi = 3.1415927f
const val rotate360 = 6.28319f

fun isDev() = FabricLoader.getInstance().isDevelopmentEnvironment

fun <T : Any> isModHolder(holder: Holder<T>) = holder.`is` { it.location().namespace == MODID }

fun <T : Any> HolderLookup.Provider.getModHolders(registry: ResourceKey<Registry<T>>): List<Holder.Reference<T>> {
    return lookup(registry).getOrNull()
        ?.listElements()
        ?.filter(::isModHolder)
        ?.toList() ?: listOf()
}

fun <T : Any> getModHolders(registry: Registry<T>): List<Holder.Reference<T>> = registry.holders()
    .filter(::isModHolder)
    .toList()

fun <T : Any> getModEntries(registry: Registry<T>): List<T> = registry.holders()
    .filter(::isModHolder)
    .map(Holder<T>::value)
    .toList()

fun <V : Any, T : V> Registry<V>.register(id: ResourceLocation, entry: T): T = Registry.register(this, id, entry)
fun <V : Any, T : V> Registry<T>.registerHolder(id: ResourceLocation, entry: T): Holder.Reference<T> =
    Registry.registerForHolder(this, id, entry)

fun <T : Any, R : Registry<T>> ResourceKey<R>.tag(id: ResourceLocation): TagKey<T> = TagKey.create(this, id)
fun <T : Any, R : Registry<T>> ResourceKey<R>.key(id: ResourceLocation): ResourceKey<T> = ResourceKey.create(this, id)

fun <T : Any> Level.getTag(tag: TagKey<T>): HolderSet.Named<T>? {
    return registryAccess().lookup(tag.registry).getOrNull()?.get(tag)?.getOrNull()
}

fun ResourceKey<*>.toLangKey(): String = location().toLangKey()
fun ResourceLocation.toLangKey(): String = toLanguageKey().replace("/", ".")

fun <T : Any> ensureUnique(id: ResourceKey<T>, registry: Registry<T>) = ensureUnique(id.location(), registry)
fun <T : Any> ensureUnique(id: ResourceLocation, registry: Registry<T>) {
    require(!registry.containsKey(id)) { "Duplicate block: $id" }
}

fun ResourceKey<LootTable>.isEmpty(): Boolean = this == BuiltInLootTables.EMPTY
