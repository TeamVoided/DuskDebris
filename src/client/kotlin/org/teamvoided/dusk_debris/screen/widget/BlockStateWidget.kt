package org.teamvoided.dusk_debris.screen.widget

import com.mojang.blaze3d.platform.Lighting
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ComponentPath
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.FocusNavigationEvent
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.CommonComponents
import net.minecraft.world.level.block.state.BlockState
import org.teamvoided.dusk_debris.util.text


class BlockStateWidget(
    width: Int, height: Int, var state: BlockState,
) : AbstractWidget(0, 0, width, height, CommonComponents.EMPTY) {
    var clickAction: (BlockStateWidget) -> Unit = {}

    override fun renderWidget(gui: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        gui.pose().pushPose()
        gui.pose().translate(x.toFloat(), y.toFloat(), 0f)
        val scale = 24f
        gui.pose().translate(scale * 1.5f, scale * 1.3f, 1f)
        gui.pose().scale(1f, -1f, 1f)
        gui.pose().scale(scale, scale, 1f)
        gui.flush()
//        Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(-30f).add(Axis.YP.rotationDegrees(225f)))
        Lighting.setupForFlatItems()
//        Light
//        gui.pose().mulPose(Axis.XN.rotationDegrees(Minecraft.getInstance().player!!.tickCount + delta))
        gui.pose().mulPose(Axis.XP.rotationDegrees(30f))
        gui.pose().mulPose(Axis.YP.rotationDegrees(225f))
        Minecraft.getInstance().blockRenderer.renderSingleBlock(
            state,
            gui.pose(),
            gui.bufferSource(),
            15728880,
            OverlayTexture.NO_OVERLAY
        )
        gui.pose().popPose()
        gui.flush()
        Lighting.setupFor3DItems()

        gui.pose().pushPose()
        gui.pose().translate(0f, 0f, 10f)
        gui.text(state.block.name, x, y + height - Minecraft.getInstance().font.lineHeight)
        gui.renderOutline(x, y, width, height, 0x0f_ff_ff_ff.toInt())
        gui.pose().popPose()

    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        super.onClick(mouseX, mouseY)
        clickAction(this)
    }

    override fun updateWidgetNarration(builder: NarrationElementOutput) = Unit
    override fun isActive(): Boolean = false
    override fun nextFocusPath(event: FocusNavigationEvent): ComponentPath? = null
}
