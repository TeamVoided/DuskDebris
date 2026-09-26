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
import net.minecraft.world.entity.Display.BillboardConstraints
import net.minecraft.world.entity.Display.TextDisplay
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.animal.sniffer.Sniffer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonStates
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonStates.Companion.setState
import org.teamvoided.dusk_debris.spell.Spell
import org.teamvoided.dusk_debris.util.spellController
import org.teamvoided.dusk_debris.util.toBlockPos
import org.teamvoided.dusk_debris.util.variant

object DuskCommands {
    fun init() = CommandRegistrationCallback.EVENT.register { dispatcher, ctx, _ ->
        val killSummoned = literal("killSummoned").executes(this::killSummoned).build()
        dispatcher.root.addChild(killSummoned)
        val sniffers = literal("sniffers").executes(this::sniffer).build()
        dispatcher.root.addChild(sniffers)
        val raccoons = literal("raccoons").executes(this::raccoon).build()
        dispatcher.root.addChild(raccoons)

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

    fun killSummoned(cx: CommandContext<CommandSourceStack>): Int {
        cx.source.level.server.commands.performPrefixedCommand(cx.source, "/kill @e[tag=summoned_with_command]")
        return 1
    }

    fun sniffer(cx: CommandContext<CommandSourceStack>): Int {
        val world = cx.source.level
        val sourcePos = cx.source.position ?: return 0
        world.registryAccess().registryOrThrow(DuskRegistryKeys.SNIFFER_VARIANT).holders().toList()
            .forEachIndexed { variantIdx, variant ->
                val pos = sourcePos.add(EntityType.SNIFFER.width * 2.0 * variantIdx, 0.0, 0.0)

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
                name.billboardConstraints = BillboardConstraints.CENTER
                name.addTag("summoned_with_command")
                world.addFreshEntity(name)

            }
        return 1
    }

    fun raccoon(cx: CommandContext<CommandSourceStack>): Int {
        val world = cx.source.level
        val sourcePos = cx.source.position ?: return 0
        world.registryAccess().registryOrThrow(DuskRegistryKeys.RACCOON_VARIANT).holders().toList()
            .forEachIndexed { variantIdx, variant ->
                val variantOffset = DuskEntities.RACCOON.width * 5.0 * variantIdx
                RaccoonStates.COMMAND_LIST.forEachIndexed { stateIdx, state ->
                    val pos = sourcePos.add(
                        variantOffset,
                        0.0,
                        DuskEntities.RACCOON.width * 5.0 * -stateIdx
                    )

                    val raccoon = lobotomize(RaccoonEntity(DuskEntities.RACCOON, world))
                    raccoon.setPos(pos)
                    raccoon.variant = variant
                    raccoon.setState(state.second)
                    raccoon.setItemSlot(EquipmentSlot.MAINHAND, ItemStack(Items.EMERALD))
                    world.addFreshEntity(raccoon)
                    //raccoon.isBaby = true
                    //raccoon.setPos(pos.add(0.0, DuskEntities.RACCOON.height.toDouble(), 0.0))
                    //world.addFreshEntity(raccoon)

                    val name = TextDisplay(EntityType.TEXT_DISPLAY, world)
                    name.setPos(pos.add(0.0, DuskEntities.RACCOON.height * 2.0, 0.0))
                    name.text = Component.literal(state.first)
                    name.billboardConstraints = BillboardConstraints.CENTER
                    name.addTag("summoned_with_command")
                    world.addFreshEntity(name)

                }
                val name = TextDisplay(EntityType.TEXT_DISPLAY, world)
                name.setPos(
                    sourcePos.add(
                        variantOffset,
                        DuskEntities.RACCOON.height * 3.0,
                        0.0
                    )
                )
                name.text = Component.literal(variant.unwrapKey().get().location().path.toString())
                name.billboardConstraints = BillboardConstraints.CENTER
                name.addTag("summoned_with_command")
                world.addFreshEntity(name)
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