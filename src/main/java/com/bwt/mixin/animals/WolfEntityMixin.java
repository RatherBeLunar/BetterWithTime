package com.bwt.mixin.animals;

import com.bwt.entities.GoToAndPickUpBreedingItemGoal;
import com.bwt.entities.PickUpBreedingItemWhileSittingGoal;
import com.bwt.entities.WolfIsFedAccess;
import com.bwt.items.BwtItems;
import com.bwt.mixin.accessors.MobEntityAccessorMixin;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends TamableAnimal implements MobEntityAccessorMixin, WolfIsFedAccess {
    protected WolfEntityMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private static final EntityDataAccessor<Boolean> IS_FED = SynchedEntityData.defineId(WolfEntityMixin.class, EntityDataSerializers.BOOLEAN);


    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    public void initDataTracker(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(IS_FED, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void bwt$writeCustomDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean("IsFed", this.bwt$isFed());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void bwt$readCustomDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("IsFed")) {
            this.bwt$setIsFed(nbt.getBoolean("IsFed"));
        }
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    public void addGoal(CallbackInfo ci) {
        this.getGoalSelector().addGoal(1, new PickUpBreedingItemWhileSittingGoal(
                this,
                1.7,
                wolf -> !wolf.getEntityData().get(IS_FED) || wolf.getHealth() < wolf.getMaxHealth(),
                this::bwt$feed
        ));
        this.getGoalSelector().addGoal(7, new GoToAndPickUpBreedingItemGoal(
                this,
                8,
                1.8,
                1,
                wolf -> !wolf.getEntityData().get(IS_FED) || wolf.getHealth() < wolf.getMaxHealth(),
                this::bwt$feed
        ));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    public void interactMob(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!this.isBaby() && this.isTame() && this.isFood(itemStack) && !bwt$isFed()) {
            if (this.level().isClientSide()) {
                cir.setReturnValue(InteractionResult.CONSUME);
                return;
            }
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            this.bwt$feed(itemStack);
            cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide()));
        }
    }

    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    public void isBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(BwtItems.kibbleItem)) {
            cir.setReturnValue(true);
            return;
        }
        if (stack.is(Items.ROTTEN_FLESH) || stack.is(BwtItems.wolfChopItem) || stack.is(BwtItems.cookedWolfChopItem)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        Level level = level();
        RandomSource random = level.getRandom();
        if (level.isClientSide) {
            return;
        }
        if (isBaby() || !bwt$isFed()) {
            return;
        }
        // A wolf produces dung on average every 20 minutes if in the light
        // This check represents once every 10 minutes, to be further filtered by the darkness check
        if (random.nextInt(24000) >= 2) {
            return;
        }
        if (!this.bwt$isInTheDark() && !random.nextBoolean()) {
            return;
        }
        if (attemptProduceDung()) {
            this.bwt$setIsFed(false);
        }
    }

    @Unique
    public boolean attemptProduceDung() {
        Level level = level();
        RandomSource random = getRandom();
        
        double dungVectorX = Math.sin(Math.toRadians(getYHeadRot()));
        double dungVectorZ = -Math.cos(Math.toRadians(getYHeadRot()));

        double dungPosX = getX() + dungVectorX;
        double dungPosY = getY() + 0.25D;
        double dungPosZ = getZ() + dungVectorZ;
        BlockPos dungBlockPos = BlockPos.containing(dungPosX, dungPosY, dungPosZ);

        if (!isPathToBlockOpenToDung(dungBlockPos))
        {
            return false;
        }

        ItemEntity itemEntity = new ItemEntity(level, dungPosX, dungPosY, dungPosZ, new ItemStack(BwtItems.dungItem));
        float velocityFactor = 0.05F;

        itemEntity.setDeltaMovement(
                dungVectorX * 10.0f * velocityFactor,
                (float)random.nextGaussian() * velocityFactor + 0.2F,
                dungVectorZ * 10.0f * velocityFactor
        );
        itemEntity.setPickUpDelay(10);
        level.addFreshEntity(itemEntity);
        level.playSound(this, blockPosition(), BwtSoundEvents.WOLF_DUNG_PRODUCTION, getSoundSource(), 0.2f, 1.25f);
        level.playSound(this, blockPosition(), BwtSoundEvents.WOLF_DUNG_EFFORT, getSoundSource(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        
        for (int counter = 0; counter < 5; counter++) {
            double smokeX = getX() + (dungVectorX * 0.5f) + (random.nextDouble() * 0.25F);
            double smokeY = getY() + random.nextDouble() * 0.5F + 0.25F;
            double smokeZ = getZ() + (dungVectorZ * 0.5f) + (random.nextDouble() * 0.25F);
            level.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0D, 0D, 0D);
        }

        return true;
    }

    @Unique
    protected boolean isPathToBlockOpenToDung(BlockPos dungBlockPos) {
        if (!bwt$isBlockOpenToDung(dungBlockPos.getX(), dungBlockPos.getY(), dungBlockPos.getZ())) {
            return false;
        }

        int wolfX = Mth.floor(getX());
        int wolfZ = Mth.floor(getZ());

        int deltaX = dungBlockPos.getX() - wolfX;
        int deltaZ = dungBlockPos.getZ() - wolfZ;

        if (deltaX != 0 && deltaZ != 0) {
            // we're producing dung on a diagonal. Test to make sure that we're not warping dung through blocked off corners
            return bwt$isBlockOpenToDung(wolfX, dungBlockPos.getY(), dungBlockPos.getZ()) || bwt$isBlockOpenToDung(dungBlockPos.getX(), dungBlockPos.getY(), wolfZ);
        }
        return true;
    }

    @Unique
    protected boolean bwt$isBlockOpenToDung(int x, int y, int z) {
        Level level = level();
        BlockPos blockPos = new BlockPos(x, y, z);
        BlockState blockState = level.getBlockState(blockPos);
        FluidState fluidState = level.getFluidState(blockPos);
        VoxelShape collisionShape = blockState.getCollisionShape(level, blockPos);

        return !fluidState.isEmpty()
                || blockState.is(BlockTags.FIRE)
                || blockState.canBeReplaced()
                || collisionShape.isEmpty()
                || collisionShape.bounds().maxY + blockPos.getY() - 0.1 <= getY();
    }

    @Override
    public boolean bwt$isFed() {
        return getEntityData().get(IS_FED);
    }

    @Unique
    public void bwt$setIsFed(boolean value) {
        getEntityData().set(IS_FED, value);
    }

    @Unique
    public void bwt$feed(int hungerValue) {
        bwt$setIsFed(bwt$isFed() || hungerValue > 0);
    }

    @Unique
    public void bwt$feed(ItemStack itemStack) {
        int nutrition = itemStack.is(BwtItems.kibbleItem) ? 2 : itemStack.getOrDefault(DataComponents.FOOD, new FoodProperties.Builder().build()).nutrition();
        heal(nutrition * 2);
        bwt$feed(nutrition);
    }

    @Unique
    public boolean bwt$isInTheDark() {
        return level().getMaxLocalRawBrightness(blockPosition()) < 5;
    }
}
