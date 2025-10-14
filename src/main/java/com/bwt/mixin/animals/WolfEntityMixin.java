package com.bwt.mixin.animals;

import com.bwt.entities.GoToAndPickUpBreedingItemGoal;
import com.bwt.entities.PickUpBreedingItemWhileSittingGoal;
import com.bwt.items.BwtItems;
import com.bwt.mixin.accessors.MobEntityAccessorMixin;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends TameableEntity implements MobEntityAccessorMixin {
    protected WolfEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final TrackedData<Boolean> IS_FED = DataTracker.registerData(WolfEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);


    @Inject(method = "initDataTracker", at = @At("TAIL"))
    public void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(IS_FED, false);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void bwt$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("IsFed", this.bwt$isFed());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void bwt$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("IsFed")) {
            this.bwt$setIsFed(nbt.getBoolean("IsFed"));
        }
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    public void addGoal(CallbackInfo ci) {
        this.getGoalSelector().add(1, new PickUpBreedingItemWhileSittingGoal(
                this,
                1.7,
                wolf -> !wolf.getDataTracker().get(IS_FED) || wolf.getHealth() < wolf.getMaxHealth(),
                this::feed
        ));
        this.getGoalSelector().add(7, new GoToAndPickUpBreedingItemGoal(
                this,
                8,
                1.8,
                1,
                wolf -> !wolf.getDataTracker().get(IS_FED) || wolf.getHealth() < wolf.getMaxHealth(),
                this::feed
        ));
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!this.isBaby() && this.isTamed() && this.isBreedingItem(itemStack) && !isFed()) {
            if (this.getWorld().isClient()) {
                cir.setReturnValue(ActionResult.CONSUME);
                return;
            }
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            this.feed(itemStack);
            cir.setReturnValue(ActionResult.success(this.getWorld().isClient()));
        }
    }

    @Inject(method = "isBreedingItem", at = @At("HEAD"), cancellable = true)
    public void isBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(BwtItems.kibbleItem)) {
            cir.setReturnValue(true);
            return;
        }
        if (stack.isOf(Items.ROTTEN_FLESH) || stack.isOf(BwtItems.wolfChopItem) || stack.isOf(BwtItems.cookedWolfChopItem)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        World world = getWorld();
        Random random = world.getRandom();
        if (world.isClient) {
            return;
        }
        if (isBaby() || !isFed()) {
            return;
        }
        // A wolf produces dung on average every 20 minutes if in the light
        // This check represents once every 10 minutes, to be further filtered by the darkness check
        if (random.nextInt(24000) >= 2) {
            return;
        }
        if (!this.isInTheDark() && !random.nextBoolean()) {
            return;
        }
        if (attemptProduceDung()) {
            this.setIsFed(false);
        }
    }

    @Unique
    public boolean attemptProduceDung() {
        World world = getWorld();
        Random random = getRandom();
        
        double dungVectorX = Math.sin(Math.toRadians(getHeadYaw()));
        double dungVectorZ = -Math.cos(Math.toRadians(getHeadYaw()));

        double dungPosX = getX() + dungVectorX;
        double dungPosY = getY() + 0.25D;
        double dungPosZ = getZ() + dungVectorZ;
        BlockPos dungBlockPos = BlockPos.ofFloored(dungPosX, dungPosY, dungPosZ);

        if (!isPathToBlockOpenToDung(dungBlockPos))
        {
            return false;
        }

        ItemEntity itemEntity = new ItemEntity(world, dungPosX, dungPosY, dungPosZ, new ItemStack(BwtItems.dungItem));
        float velocityFactor = 0.05F;

        itemEntity.setVelocity(
                dungVectorX * 10.0f * velocityFactor,
                (float)random.nextGaussian() * velocityFactor + 0.2F,
                dungVectorZ * 10.0f * velocityFactor
        );
        itemEntity.setPickupDelay(10);
        world.spawnEntity(itemEntity);
        world.playSound(this, getBlockPos(), BwtSoundEvents.WOLF_DUNG_PRODUCTION, getSoundCategory(), 0.2f, 1.25f);
        world.playSound(this, getBlockPos(), BwtSoundEvents.WOLF_DUNG_EFFORT, getSoundCategory(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        
        for (int counter = 0; counter < 5; counter++) {
            double smokeX = getX() + (dungVectorX * 0.5f) + (random.nextDouble() * 0.25F);
            double smokeY = getY() + random.nextDouble() * 0.5F + 0.25F;
            double smokeZ = getZ() + (dungVectorZ * 0.5f) + (random.nextDouble() * 0.25F);
            world.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0D, 0D, 0D);
        }

        return true;
    }

    @Unique
    protected boolean isPathToBlockOpenToDung(BlockPos dungBlockPos) {
        if (!isBlockOpenToDung(dungBlockPos.getX(), dungBlockPos.getY(), dungBlockPos.getZ())) {
            return false;
        }

        int wolfX = MathHelper.floor(getX());
        int wolfZ = MathHelper.floor(getZ());

        int deltaX = dungBlockPos.getX() - wolfX;
        int deltaZ = dungBlockPos.getZ() - wolfZ;

        if (deltaX != 0 && deltaZ != 0) {
            // we're producing dung on a diagonal. Test to make sure that we're not warping dung through blocked off corners
            return isBlockOpenToDung(wolfX, dungBlockPos.getY(), dungBlockPos.getZ()) || isBlockOpenToDung(dungBlockPos.getX(), dungBlockPos.getY(), wolfZ);
        }
        return true;
    }

    @Unique
    protected boolean isBlockOpenToDung(int x, int y, int z) {
        World world = getWorld();
        BlockState blockState = world.getBlockState(new BlockPos(x, y, z));
        FluidState fluidState = world.getFluidState(new BlockPos(x, y, z));

        return !fluidState.isEmpty() || blockState.isIn(BlockTags.FIRE) || blockState.isReplaceable();
    }

    @Unique
    public boolean isFed() {
        return getDataTracker().get(IS_FED);
    }

    @Unique
    public void setIsFed(boolean value) {
        getDataTracker().set(IS_FED, value);
    }

    @Unique
    public void feed(int hungerValue) {
        setIsFed(isFed() || hungerValue > 0);
    }

    @Unique
    public void feed(ItemStack itemStack) {
        int nutrition = itemStack.isOf(BwtItems.kibbleItem) ? 2 : itemStack.getOrDefault(DataComponentTypes.FOOD, new FoodComponent.Builder().build()).nutrition();
        heal(nutrition * 2);
        feed(nutrition);
    }

    @Unique
    public boolean isInTheDark() {
        return getWorld().getLightLevel(getBlockPos()) < 5;
    }
}
