package org.teamvoided.dusk_debris.screen

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Biomes
import org.teamvoided.dusk_debris.block.entity.BiomeTintBlockEntity
import org.teamvoided.dusk_debris.net.c2s.BiomeTintUpdatePayload
import org.teamvoided.dusk_debris.screen.widget.BiomeWidget
import org.teamvoided.dusk_debris.screen.widget.EntityModelListWidget
import kotlin.jvm.optionals.getOrNull

class BiomeSelectScreen(val biomeTinter: BiomeTintBlockEntity) : Screen(TITLE), LayoutProvider {
    var biome: Holder<Biome>? = null
    var biomeWidget: BiomeWidget? = null
    val layout: HeaderAndFooterLayout = HeaderAndFooterLayout(this)
    var modelList: EntityModelListWidget? = null


    override fun init() {
        super.init()
        biome = biomeTinter.biome
        biomeWidget = BiomeWidget(48, 48, biome ?: getBiomes()!!.getHolderOrThrow(Biomes.PLAINS))
        biomeWidget?.let { entityWidget ->
            entityWidget.x = 16
            entityWidget.y = 16
            addRenderableWidget(entityWidget)
        }
        modelList = this.layout.addToContents(EntityModelListWidget(minecraft, this.width, this))
        modelList?.let { list ->
            list.addEntries(
                getBiomes()!!.holders().toList()
                    .map { holder ->
                        BiomeWidget(128, 48, holder).let { widget ->
                            widget.clickAction = { button ->
                                ClientPlayNetworking.send(
                                    BiomeTintUpdatePayload(
                                        biomeTinter.blockPos,
                                        button.biome.unwrapKey().get()
                                    )
                                )
                                biome = button.biome
                                biomeWidget?.let { ew -> ew.biome = button.biome }
                            }
                            widget
                        }
                    })
        }
        layout.visitWidgets(::addRenderableWidget)
        repositionElements()
    }

    override fun repositionElements() {
        this.layout.arrangeElements()
        this.modelList?.updateSize(this.width, this.layout)
    }

    override fun layout(): HeaderAndFooterLayout {
        return layout
    }

    companion object {

        val TITLE: MutableComponent = Component.literal("Biome Screen")

        fun getBiomes(): Registry<Biome>? {
            return Minecraft.getInstance().level?.registryAccess()?.registry(Registries.BIOME)?.getOrNull()
        }

    }
}