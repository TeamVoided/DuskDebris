package org.teamvoided.dusk_debris.screen

import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.screens.Screen

interface LayoutProvider {
    fun self() = this as Screen
    fun layout(): HeaderAndFooterLayout
}