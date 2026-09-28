package org.teamvoided.dusk_debris.screen.widget

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ComponentPath
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.FocusNavigationEvent
import net.minecraft.core.Holder
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.level.biome.Biome
import org.teamvoided.dusk_debris.util.text

class BiomeWidget(
    width: Int, height: Int,
    var biome: Holder<Biome>,
) : AbstractWidget(0, 0, width, height, CommonComponents.EMPTY) {
    var clickAction: (BiomeWidget) -> Unit = {}

    override fun renderWidget(gui: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val name = biome.unwrapKey().get().location().toLanguageKey("biome")
        gui.text(Component.translatable(name), x, y + (height - Minecraft.getInstance().font.lineHeight) / 2)
        gui.renderOutline(x - 1, y - 1, width + 1, height + 1, -1)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        super.onClick(mouseX, mouseY)
        clickAction(this)
    }

    override fun updateWidgetNarration(builder: NarrationElementOutput) = Unit
    override fun isActive(): Boolean = false
    override fun nextFocusPath(event: FocusNavigationEvent): ComponentPath? = null
}
