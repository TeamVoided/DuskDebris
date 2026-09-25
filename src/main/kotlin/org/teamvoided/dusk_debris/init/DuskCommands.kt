package org.teamvoided.dusk_debris.init

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.ResourceArgument
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Display.TextDisplay
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.animal.sniffer.Sniffer
import org.teamvoided.dusk_debris.entity.RaccoonEntity
import org.teamvoided.dusk_debris.spell.Spell
import org.teamvoided.dusk_debris.util.spellController
import org.teamvoided.dusk_debris.util.toBlockPos
import org.teamvoided.dusk_debris.util.variant

object DuskCommands {
    fun init() = CommandRegistrationCallback.EVENT.register { dispatcher, ctx, _ ->
        val sniffers = literal("sniffers").executes(this::sniffer).build()
        dispatcher.root.addChild(sniffers)

        val worldEvent = literal("worldEvent").build()
        dispatcher.root.addChild(worldEvent)
        val eventID = argument("eventID", IntegerArgumentType.integer()).executes { cx ->
            val world = cx.source.level
            val pos = cx.source.position
            val eventID = IntegerArgumentType.getInteger(cx, "eventID")
            world.levelEvent(eventID, pos.toBlockPos(), 0)

            0
        }.build()
        worldEvent.addChild(eventID)

        val spell = literal("spell").build()
        dispatcher.root.addChild(spell)
        val spellType = argument("spell_id", ResourceArgument.resource(ctx, DuskRegistryKeys.SPELL))
            .executes { spell(it, ResourceArgument.getResource(it, "spell_id", DuskRegistryKeys.SPELL)) }
            .build()
        spell.addChild(spellType)

        //val spline = literal("spline").executes { splineCommand(it) }.build()
        //dispatcher.root.addChild(spline)
    }


    fun spell(cx: CommandContext<CommandSourceStack>, registryEntry: Holder.Reference<Spell<*, *>>): Int {
        val player = cx.source.player ?: return 0
        player.spellController.setSpell(player, registryEntry)
        player.displayClientMessage(Component.literal("applied spell " + registryEntry.unwrapKey().toString()), false)
        return 1
    }

    fun sniffer(cx: CommandContext<CommandSourceStack>): Int {
        val world = cx.source.level
        val player = cx.source.player ?: return 0
        world.registryAccess().registryOrThrow(DuskRegistryKeys.SNIFFER_VARIANT).holders().toList()
            .forEachIndexed { variantIdx, variant ->
                val pos = player.position().add(EntityType.SNIFFER.width * 2.0 * variantIdx, 0.0, 0.0)

                val sniffer = lobotomize(Sniffer(EntityType.SNIFFER, world))
                sniffer.setPos(pos)
                sniffer.variant = variant
                world.addFreshEntity(sniffer)
                sniffer.isBaby
                sniffer.setPos(pos.add(0.0, sniffer.bbHeight.toDouble(), 0.0))
                world.addFreshEntity(sniffer)

                val name = TextDisplay(EntityType.TEXT_DISPLAY, world)
                name.setPos(pos.add(0.0, 3.0, 0.0))
                name.text = Component.literal(variant.unwrapKey().get().location().toString())
                name.addTag("summoned_with_command")
                world.addFreshEntity(name)

            }
        return 1
    }

    fun raccoon(cx: CommandContext<CommandSourceStack>): Int {
        val world = cx.source.level
        val player = cx.source.player ?: return 0
        world.registryAccess().registryOrThrow(DuskRegistryKeys.RACCOON_VARIANT).holders().toList()
            .forEachIndexed { variantIdx, variant ->
                RaccoonEntity.STATES.forEachIndexed { stateIdx, state ->
                    val pos = player.position().add(
                        DuskEntities.RACCOON.width * 2.0 * variantIdx,
                        0.0,
                        DuskEntities.RACCOON.width * 2.0 * stateIdx
                    )

                    val raccoon = lobotomize(RaccoonEntity(DuskEntities.RACCOON, world))
                    raccoon.setPos(pos)
                    raccoon.variant = variant
                    raccoon.state = state.second
                    world.addFreshEntity(raccoon)
                    raccoon.isBaby
                    raccoon.setPos(pos.add(0.0, raccoon.bbHeight.toDouble(), 0.0))
                    world.addFreshEntity(raccoon)

                    val name = TextDisplay(EntityType.TEXT_DISPLAY, world)
                    name.setPos(pos.add(0.0, raccoon.bbHeight.toDouble() * 2, 0.0))
                    name.text = Component.literal(variant.unwrapKey().get().location().toString() + ", " + state.first)
                    name.addTag("summoned_with_command")
                    world.addFreshEntity(name)

                }
            }
        return 1
    }

    private fun <T : Mob> lobotomize(entity: T): T {
        entity.isInvulnerable = true
        entity.isNoAi = true
        entity.isSilent = true
        entity.yRot = 0f
        entity.addTag("summoned_with_command")
        return entity
    }
}