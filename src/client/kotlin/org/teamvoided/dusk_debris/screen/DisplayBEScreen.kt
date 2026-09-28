package org.teamvoided.dusk_debris.screen

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.level.block.Blocks
import org.teamvoided.dusk_debris.block.entity.DisplayBlockEntity
import org.teamvoided.dusk_debris.net.c2s.DisplayUpdatePayload
import org.teamvoided.dusk_debris.screen.widget.BlockStateWidget
import org.teamvoided.dusk_debris.screen.widget.EntityModelListWidget

class DisplayBEScreen(val displayBE: DisplayBlockEntity) : Screen(TITLE), LayoutProvider {

    var selectedStateWidget: BlockStateWidget = BlockStateWidget(48, 48, Blocks.GRASS_BLOCK.defaultBlockState())
    val layout: HeaderAndFooterLayout = HeaderAndFooterLayout(this)
    var listWidget: EntityModelListWidget? = null

    override fun init() {
        super.init()
        selectedStateWidget = BlockStateWidget(48, 48, displayBE.state)
        selectedStateWidget.x = 16
        selectedStateWidget.y = 16
        addRenderableWidget(selectedStateWidget)

        listWidget = this.layout.addToContents(EntityModelListWidget(minecraft, this.width, this))
        listWidget?.let { list ->
            list.addEntries(
                BuiltInRegistries.BLOCK.map { block ->
                    BlockStateWidget(128, 48, block.defaultBlockState()).let { widget ->
                        widget.clickAction = { button ->
                            ClientPlayNetworking.send(DisplayUpdatePayload(displayBE.blockPos, button.state))
                            selectedStateWidget.state = button.state
                        }
                        widget
                    }
                })
        }
        layout.visitWidgets(::addRenderableWidget)
        repositionElements()
    }

    override fun repositionElements() {
        layout.arrangeElements()
        listWidget?.updateSize(width, layout)
    }

    override fun layout(): HeaderAndFooterLayout = layout

    override fun isPauseScreen(): Boolean = false

    companion object {

        val TITLE: MutableComponent = Component.literal("Display Screen")

    }
}