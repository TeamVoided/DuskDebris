package org.teamvoided.dusk_debris.screen.widget

import com.mojang.blaze3d.platform.Lighting
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ComponentPath
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.FocusNavigationEvent
import net.minecraft.client.sounds.SoundManager
import net.minecraft.core.Holder
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.util.Mth.lerp
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.biome.Biome
import org.teamvoided.dusk_debris.util.text
import kotlin.math.min

class BiomeWidget(
    width: Int, height: Int,
    var biome: Holder<Biome>,
) : AbstractWidget(0, 0, width, height, CommonComponents.EMPTY) {
    var clickAction: (BiomeWidget) -> Unit = {}

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val name = biome.unwrapKey().get().location().toLanguageKey("biome")
        graphics.text(Component.translatable(name), x, y + height - (Minecraft.getInstance().font.lineHeight))
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        super.onClick(mouseX, mouseY)
        clickAction(this)
    }

    override fun updateWidgetNarration(builder: NarrationElementOutput) = Unit
    override fun isActive(): Boolean = false
    override fun nextFocusPath(event: FocusNavigationEvent): ComponentPath? = null
}
