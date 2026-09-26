package org.teamvoided.dusk_debris.entity.raccoon

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.animal.Fox
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import org.teamvoided.dusk_debris.data.tags.DuskEntityTypeTags
import org.teamvoided.dusk_debris.data.tags.DuskItemTags
import org.teamvoided.dusk_debris.entity.goal.raccoon.*
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonAlignment.Companion.getPreyTargets
import org.teamvoided.dusk_debris.init.DuskAttachmentTypes
import org.teamvoided.dusk_debris.init.DuskEntities
import org.teamvoided.dusk_debris.init.DuskRegistryKeys
import org.teamvoided.dusk_debris.net.s2c.RaccoonBrainInfoPayload
import org.teamvoided.dusk_debris.util.text
import kotlin.math.min

class RaccoonEntity(type: EntityType<out RaccoonEntity>, world: Level) : Animal(type, world),
    VariantHolder<Holder<RaccoonVariant>> {

    var eatTicks = 0
    var hunger = 0
    var hasWashedFood = false
    var raccoonData = RaccoonData(random)

    val washingAnimationState = AnimationState()
    val sneezingAnimationState = AnimationState()

    /**
     * Used to store debug info on the client
     */
    var displayBrainData: MutableList<Component> = mutableListOf()
    var hideDebug = false

    init {
        setCanPickUpLoot(true)
        shouldDropLoot()
    }

    override fun registerGoals() {
        goalSelector.addGoal(1, FloatGoal(this))
        goalSelector.addGoal(2, BreedGoal(this, 1.0))
        goalSelector.addGoal(
            4, AvoidEntityGoal(this, LivingEntity::class.java, 8f, 1.6, 1.4)
            { it.type.`is`(DuskEntityTypeTags.RACCOON_RETREATS) }
        )
        goalSelector.addGoal(6, SeekShelterGoal(this, 1.25))
        goalSelector.addGoal(7, ClaimBarrelGoal(this, 1.2, 12))
        goalSelector.addGoal(7, WashFoodGoal(this, 1.2, 12))
        goalSelector.addGoal(7, RaccoonMeleeAttackGoal(this, 1.2, true))
        goalSelector.addGoal(8, PickBerriesGoal(this, 1.2, 12, 1))
        goalSelector.addGoal(8, GetFoodFromBarrelGoal(this, 1.2, 0))
        goalSelector.addGoal(9, RaccoonWanderGoal(this, 1.0))
        goalSelector.addGoal(9, RaccoonSearchForItemsGoal(this))
        goalSelector.addGoal(9, StoreItemsGoal(this, 1.2, 0))
        goalSelector.addGoal(10, LookAtPlayerGoal(this, Player::class.java, 8F))
        goalSelector.addGoal(10, RandomLookAroundGoal(this))
        //goalSelector.addGoal(15, TooFarFromBarrelGoal(this, 1.2, 0))

        // Pacifist alignment? or rename it to Pacificist or PaciFist
        //targetSelector.addGoal(1, HurtByTargetGoal(this, LivingEntity::class.java).setAlertOthers())
        targetSelector.addGoal(
            5, NearestAttackableTargetGoal(this, LivingEntity::class.java, 40, false, false) { this.getPreyTargets(it) }
        )

    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(BARREL_POS, DEFAULT_BARREL_POS) //does barrel pos even need to be synched?
        builder.define(DATA_STATE, RaccoonStates.Idle.ordinal)
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putInt(KEY_HUNGER, hunger)
        tag.putInt(KEY_EAT_TICKS, eatTicks)
        tag.putBoolean(KEY_HAS_WASHED_FOOD, hasWashedFood)
        tag.putInt(KEY_STATE, state)
        if (barrelPos != DEFAULT_BARREL_POS) {
            val barrelPosTag = CompoundTag()
            barrelPosTag.putInt("x", barrelPos.x)
            barrelPosTag.putInt("y", barrelPos.y)
            barrelPosTag.putInt("z", barrelPos.z)
            tag.put(KEY_BARREL_POS, barrelPosTag)
        }
        raccoonData.addAdditionalSaveData(tag)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        if (tag.contains(KEY_HUNGER)) hunger = tag.getInt(KEY_HUNGER)
        if (tag.contains(KEY_EAT_TICKS)) eatTicks = tag.getInt(KEY_EAT_TICKS)
        if (tag.contains(KEY_HAS_WASHED_FOOD)) hasWashedFood = tag.getBoolean(KEY_HAS_WASHED_FOOD)
        if (tag.contains(KEY_STATE)) state = tag.getInt(KEY_STATE)
        if (tag.contains(KEY_BARREL_POS, Tag.TAG_COMPOUND.toInt())) {
            val barrelPosTag = tag.getCompound(KEY_BARREL_POS)
            barrelPos = BlockPos(barrelPosTag.getInt("x"), barrelPosTag.getInt("y"), barrelPosTag.getInt("z"))
        }
        raccoonData.readAdditionalSaveData(tag)
    }

    override fun tick() {
        sendRacoonData()
        super.tick()
        if (!level().isClientSide) {
            if (barrelPos != DEFAULT_BARREL_POS && !level().getBlockState(barrelPos).`is`(Blocks.BARREL)) {
                forgetBarrel()
            }
        }
    }

    override fun interactAt(player: Player, vec3: Vec3, hand: InteractionHand): InteractionResult {
        if (hand == InteractionHand.MAIN_HAND) {
            val stack = player.getItemInHand(hand)
            if (stack.isEmpty) {
                hideDebug = !hideDebug
                return InteractionResult.SUCCESS
            }
        }

        return super.interactAt(player, vec3, hand)
    }

    override fun aiStep() {
        if (!level().isClientSide && isAlive && isEffectiveAi) {
            val heldItem = getHeldItem()
            if (hasWashedFood && canEat(heldItem) && target == null && onGround() && canMove()) {
                eatTicks++
                if (eatTicks >= getEatTime(heldItem)) {
                    val remainingStack = heldItem.finishUsingItem(level(), this)
                    if (!remainingStack.isEmpty) {
                        if (!remainingStack.`is`(heldItem.item)) {
                            hasWashedFood = false
                        }
                        setItemSlot(EquipmentSlot.MAINHAND, remainingStack)
                    }
                    playSound(getEatingSound(heldItem), 1F, 0F)
                    level().broadcastEntityEvent(this, EntityEvent.FOX_EAT)

                    eatTicks = 0
                    hunger = addFood(heldItem)
                } else if (random.nextFloat() < 0.1F) {
                    playSound(getEatingSound(heldItem), 1F, 1F)
                    level().broadcastEntityEvent(this, EntityEvent.FOX_EAT)
                }
            }
            if (tickCount % 400 == 0 && hunger > 0) {
                hunger--
                if (health < maxHealth && !isStarving()) {
                    hunger -= 2
                    heal(0.5f)
                }
            }
        }
        super.aiStep()
    }

    fun canEat(stack: ItemStack): Boolean {
        val food = stack.get(DataComponents.FOOD)
        return isFood(stack) && (isStarving() || hunger <= MAX_HUNGER - (food?.nutrition ?: 0))
    }

    fun getEatTime(heldItem: ItemStack): Int {
        val time = heldItem.get(DataComponents.FOOD)?.eatSeconds
        if (time != null) return (time * 20).toInt()
        return 40
    }

    fun addFood(heldItem: ItemStack): Int {
        val food = heldItem.get(DataComponents.FOOD)
        if (food != null) return min(MAX_HUNGER, hunger + food.nutrition) + food.saturation.toInt()
        return 0
    }

    fun isStarving(): Boolean = hunger < 5

    override fun handleEntityEvent(b: Byte) {
        super.handleEntityEvent(b)
        when (b) {
            EntityEvent.VILLAGER_HAPPY -> {
                for (i in 0..5) {
                    level().addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        getRandomX(1.0),
                        randomY,
                        getRandomZ(1.0),
                        random.nextGaussian() * 0.02,
                        random.nextGaussian() * 0.02,
                        random.nextGaussian() * 0.02
                    )
                }
            }

            EntityEvent.VILLAGER_ANGRY -> {
                for (i in 0..5) {
                    level().addParticle(
                        ParticleTypes.ANGRY_VILLAGER,
                        getRandomX(1.0),
                        randomY,
                        getRandomZ(1.0),
                        random.nextGaussian() * 0.02,
                        random.nextGaussian() * 0.02,
                        random.nextGaussian() * 0.02
                    )
                }
            }

            EntityEvent.FOX_EAT -> {
                val heldItem = getHeldItem()
                if (!heldItem.isEmpty) {
                    for (i in 0..7) {
                        val speeds = Vec3((random.nextFloat() - 0.5) * 0.1, random.nextDouble() * 0.1 + 0.1, 0.0)
                            .xRot(-xRot * Mth.DEG_TO_RAD)
                            .yRot(-yRot * Mth.DEG_TO_RAD)
                        level().addParticle(
                            ItemParticleOption(ParticleTypes.ITEM, heldItem),
                            x + lookAngle.x / 2.0,
                            y,
                            z + lookAngle.z / 2.0,
                            speeds.x,
                            speeds.y + 0.05,
                            speeds.z
                        )
                    }
                }
            }

            EntityEvent.VILLAGER_SWEAT -> {
                for (i in 0..10) {
                    val speeds = Vec3((random.nextFloat() - 0.5) * 0.1, random.nextDouble() * 0.1 + 0.1, 0.0)
                        .xRot(-xRot * Mth.DEG_TO_RAD)
                        .yRot(-yRot * Mth.DEG_TO_RAD)
                    level().addParticle(
                        ParticleTypes.SPLASH,
                        x + lookAngle.x / 2.0,
                        y,
                        z + lookAngle.z / 2.0,
                        speeds.x,
                        speeds.y + 0.05,
                        speeds.z
                    )
                }
            }
        }
    }

    fun getHeldItem() = getItemBySlot(EquipmentSlot.MAINHAND)!!

    override fun canTakeItem(stack: ItemStack): Boolean {
        val slot = getEquipmentSlotForItem(stack)
        if (!canPickup(stack)) {
            return false
        }
        return slot == EquipmentSlot.MAINHAND && super.canTakeItem(stack)
    }

    override fun canHoldItem(stack: ItemStack): Boolean {
        val heldItem = getHeldItem()
        return heldItem.isEmpty ||
                (heldItem.count < heldItem.maxStackSize && ItemStack.isSameItemSameComponents(heldItem, stack))
                || (if (isStarving()) isFood(stack) && !isFood(heldItem) else canEat(stack) && !canEat(heldItem))
    }

    fun dropItemStack(stack: ItemStack) {
        val itemEntity = ItemEntity(level(), x, y, z, stack)
        level().addFreshEntity(itemEntity)
    }

    override fun pickUpItem(itemEntity: ItemEntity) {
        val stack = itemEntity.item
        if (canHoldItem(stack)) {
            var heldStack = getHeldItem()
            if (!heldStack.isEmpty) {
                val newStack = stack.split(heldStack.maxStackSize - heldStack.count)
                heldStack.count += newStack.count
                dropItemStack(stack)
            } else {
                heldStack = stack
            }

            onItemPickup(itemEntity)
            setItemSlot(EquipmentSlot.MAINHAND, heldStack)
            setGuaranteedDrop(EquipmentSlot.MAINHAND)
            take(itemEntity, stack.count)
            itemEntity.discard()
            hasWashedFood = false
        }
    }

    fun canPickup(stack: ItemStack): Boolean = stack.isEmpty || stack.count < stack.maxStackSize

    override fun isFood(stack: ItemStack): Boolean {
        return stack.`is`(DuskItemTags.RACCOON_FOOD)
    }

    override fun isSleeping(): Boolean = RaccoonStates.entries[state].closeEyes()

    fun canMove(): Boolean = RaccoonStates.entries[state].canMove()

    override fun onSyncedDataUpdated(entityDataAccessor: EntityDataAccessor<*>) {
        if (DATA_STATE == entityDataAccessor) {
            resetAnimations()
            when (RaccoonStates.entries[state]) {
                RaccoonStates.Idle, RaccoonStates.Sitting, RaccoonStates.Sleeping -> {} //poses, not animations. done in model.
                RaccoonStates.Sneeze -> sneezingAnimationState.startIfStopped(tickCount)
                RaccoonStates.Washing -> washingAnimationState.startIfStopped(tickCount)
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor)
    }

    fun resetAnimations() {
        sneezingAnimationState.stop()
        washingAnimationState.stop()
    }

    override fun getBreedOffspring(level: ServerLevel, entity: AgeableMob): AgeableMob? {
        val baby = DuskEntities.RACCOON.create(level)
        if (baby != null) {
            baby.variant = if (random.nextBoolean()) variant else (entity as RaccoonEntity).variant
        }

        return baby
    }

    @Suppress("UnstableApiUsage")
    override fun setVariant(variant: Holder<RaccoonVariant>) {
        setAttached(DuskAttachmentTypes.RACCOON_VARIANT, variant.unwrapKey().get())
    }

    @Suppress("UnstableApiUsage")
    override fun getVariant(): Holder<RaccoonVariant> {
        return level().registryAccess().lookupOrThrow(DuskRegistryKeys.RACCOON_VARIANT)
            .getOrThrow(getAttachedOrCreate(DuskAttachmentTypes.RACCOON_VARIANT))
    }

    fun forgetBarrel() {
        barrelPos = DEFAULT_BARREL_POS
        level().broadcastEntityEvent(this, EntityEvent.VILLAGER_ANGRY)
    }

    var state: Int
        get() = entityData[DATA_STATE]
        set(state) = entityData.set(DATA_STATE, state)

    var barrelPos: BlockPos
        get() = entityData[BARREL_POS]
        set(value) = entityData.set(BARREL_POS, value)

    fun sendRacoonData() {
        if (level().isClientSide || hideDebug) {
            return
        }

        val data = mutableListOf<Component>()
        buildRacoonInfo(data)

        for (player in level().players()) {
            if (player is ServerPlayer) {
                ServerPlayNetworking.send(player, RaccoonBrainInfoPayload(id, data))
            }
        }

    }

    fun buildRacoonInfo(list: MutableList<Component>) {
        list.add(text("Barrel Pos: ${if (barrelPos == DEFAULT_BARREL_POS) "[ None ]" else barrelPos}"))
        list.add(text("Eat Ticks: $eatTicks"))
        list.add(text("Hunger: $hunger"))
        list.add(text("Has Washed Food: $hasWashedFood"))
        list.add(text("Data: $raccoonData"))
        list.add(text("Current Goals: "))
        for (goal in goalSelector.availableGoals) {
            if (goal.isRunning) {
                list.add(text("  - ${goal.goal}"))
            }
        }
    }

    companion object {

        private val DATA_STATE: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(RaccoonEntity::class.java, EntityDataSerializers.INT)
        private val BARREL_POS: EntityDataAccessor<BlockPos> =
            SynchedEntityData.defineId(RaccoonEntity::class.java, EntityDataSerializers.BLOCK_POS)

        val DEFAULT_BARREL_POS = BlockPos(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        const val MAX_HUNGER = 20

        const val WANDER_RANGE = 32 * 32
        const val BARREL_FORGET_RANGE = 64 * 64

        fun createAttributes(): AttributeSupplier.Builder {
            return Fox.createAttributes()
        }

    }
}