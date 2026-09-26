package org.teamvoided.dusk_debris.util

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent


fun text(key: String) = translatable(key)
fun text(key: String, color: Int): MutableComponent = translatable(key).withColor(color)
fun text(any: Any) = translatable(any.toString())

fun literal(message: String): MutableComponent = Component.literal(message)
fun literal(any: Any) = literal(any.toString())

fun translatable(key: String): MutableComponent = Component.translatable(key)
fun translatable(key: String, vararg args: Any): MutableComponent = Component.translatable(key, *args)

fun empty(): MutableComponent = Component.empty()

fun keybind(bind: String): MutableComponent = Component.keybind(bind)

