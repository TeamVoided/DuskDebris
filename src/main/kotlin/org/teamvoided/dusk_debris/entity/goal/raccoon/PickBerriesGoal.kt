package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CaveVines
import net.minecraft.world.level.block.SweetBerryBushBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent

open class PickBerriesGoal(mob: PathfinderMob, speed: Double, range: Int, maxYDifference: Int) :
    MoveToBlockGoal(mob, speed, range, maxYDifference) {
    protected var timer: Int = 0

    override fun acceptedDistance(): Double = 2.0

    override fun shouldRecalculatePath(): Boolean = tryTicks % 100 == 0

    override fun isValidTarget(world: LevelReader, pos: BlockPos): Boolean {
        val state = world.getBlockState(pos)
        return state.`is`(Blocks.SWEET_BERRY_BUSH) && state.getValue(SweetBerryBushBlock.AGE) >= 2 || CaveVines.hasGlowBerries(state)
    }

    override fun tick() {
        if (isReachedTarget) {
            if (timer >= 40) {
                pickFromTargetPos()
            } else {
                ++timer
            }
        } else if (!isReachedTarget && mob.random.nextFloat() < 0.05f) {
            mob.playSound(SoundEvents.FOX_SNIFF, 1.0f, 1.0f)
        }

        super.tick()
    }

    protected fun pickFromTargetPos() {
        if (mob.level().gameRules.getBoolean(GameRules.RULE_MOBGRIEFING)) {
            val state: BlockState = mob.level().getBlockState(blockPos)
            if (state.`is`(Blocks.SWEET_BERRY_BUSH)) {
                pickSweetBerries(state)
            } else if (CaveVines.hasGlowBerries(state)) {
                pickGlowBerries(state)
            }
        }
    }

    private fun pickGlowBerries(state: BlockState) = CaveVines.use(mob, state, mob.level(), blockPos)

    private fun pickSweetBerries(state: BlockState) {
        val i = state.getValue(SweetBerryBushBlock.AGE) as Int
        state.setValue(SweetBerryBushBlock.AGE, 1)
        var j: Int = 1 + mob.level().random.nextInt(2) + (if (i == 3) 1 else 0)
        val itemStack: ItemStack = mob.getItemBySlot(EquipmentSlot.MAINHAND)
        if (itemStack.isEmpty) {
            mob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack(Items.SWEET_BERRIES))
            --j
        }

        if (j > 0) {
            Block.popResource(mob.level(), blockPos, ItemStack(Items.SWEET_BERRIES, j))
        }

        mob.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0f, 1.0f)
        mob.level().setBlock(blockPos, state.setValue(SweetBerryBushBlock.AGE, 1), 2)
        mob.level().gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(mob))
    }

    override fun canUse(): Boolean = !mob.isSleeping && super.canUse()

    override fun start() {
        timer = 0
//        mob.setSitting(false) TODO
        super.start()
    }
}