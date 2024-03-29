package com.bwt.mixin.animals;

import com.bwt.entities.GoToAndPickUpBreedingItemGoal;
import com.bwt.entities.PickUpBreedingItemWhileSittingGoal;
import com.bwt.items.BwtItems;
import com.bwt.mixin.accessors.MobEntityAccessorMixin;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin implements MobEntityAccessorMixin {
    @Shadow protected abstract float getSoundVolume();

    @Unique
    private static final TrackedData<Boolean> IS_FED = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    @Inject(method = "initDataTracker", at = @At("TAIL"))
    public void initDataTracker(CallbackInfo ci) {
        ((WolfEntity) ((Object) this)).getDataTracker().startTracking(IS_FED, false);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    public void addGoal(CallbackInfo ci) {
        this.getGoalSelector().add(1, new PickUpBreedingItemWhileSittingGoal(
                (WolfEntity) ((Object) this),
                1.5,
                wolf -> !wolf.getDataTracker().get(IS_FED),
                this::feed
        ));
        this.getGoalSelector().add(7, new GoToAndPickUpBreedingItemGoal(
                (WolfEntity) ((Object) this),
                8,
                1.5,
                1,
                wolf -> !wolf.getDataTracker().get(IS_FED),
                this::feed
        ));
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        WolfEntity wolfThis = ((WolfEntity) ((Object) this));
        ItemStack itemStack = player.getStackInHand(hand);
        if (!wolfThis.isBaby() && wolfThis.isTamed() && wolfThis.isBreedingItem(itemStack) && !isFed()) {
            if (wolfThis.getWorld().isClient) {
                cir.setReturnValue(ActionResult.CONSUME);
                return;
            }
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            this.feed(itemStack);
            cir.setReturnValue(ActionResult.SUCCESS);
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
        WolfEntity wolfThis = ((WolfEntity) ((Object) this));
        World world = wolfThis.getWorld();
        Random random = world.getRandom();
        if (world.isClient) {
            return;
        }
        if (wolfThis.isBaby() || !isFed()) {
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
        WolfEntity wolfThis = ((WolfEntity) ((Object) this));
        World world = wolfThis.getWorld();
        Random random = wolfThis.getRandom();
        
        double dungVectorX = Math.sin(Math.toRadians(wolfThis.getHeadYaw()));
        double dungVectorZ = -Math.cos(Math.toRadians(wolfThis.getHeadYaw()));

        double dungPosX = wolfThis.getX() + dungVectorX;
        double dungPosY = wolfThis.getY() + 0.25D;
        double dungPosZ = wolfThis.getZ() + dungVectorZ;
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
        world.playSound(wolfThis, wolfThis.getBlockPos(), BwtSoundEvents.WOLF_DUNG_PRODUCTION, wolfThis.getSoundCategory(), 0.2f, 1.25f);
        world.playSound(wolfThis, wolfThis.getBlockPos(), BwtSoundEvents.WOLF_DUNG_EFFORT, wolfThis.getSoundCategory(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        
        for (int counter = 0; counter < 5; counter++) {
            double smokeX = wolfThis.getX() + (dungVectorX * 0.5f) + (random.nextDouble() * 0.25F);
            double smokeY = wolfThis.getY() + random.nextDouble() * 0.5F + 0.25F;
            double smokeZ = wolfThis.getZ() + (dungVectorZ * 0.5f) + (random.nextDouble() * 0.25F);
            world.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0D, 0D, 0D);
        }

        return true;
    }

    @Unique
    protected boolean isPathToBlockOpenToDung(BlockPos dungBlockPos) {
        WolfEntity wolfThis = ((WolfEntity) ((Object) this));

        if (!isBlockOpenToDung(dungBlockPos.getX(), dungBlockPos.getY(), dungBlockPos.getZ())) {
            return false;
        }

        int wolfX = MathHelper.floor(wolfThis.getX());
        int wolfZ = MathHelper.floor(wolfThis.getZ());

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
        WolfEntity wolfThis = ((WolfEntity) ((Object) this));
        World world = wolfThis.getWorld();
        BlockState blockState = world.getBlockState(new BlockPos(x, y, z));
        FluidState fluidState = world.getFluidState(new BlockPos(x, y, z));

        return !fluidState.isEmpty() || blockState.isIn(BlockTags.FIRE) || blockState.isReplaceable();
    }

    @Unique
    public boolean isFed() {
        return ((WolfEntity) ((Object) this)).getDataTracker().get(IS_FED);
    }

    @Unique
    public void feed(int hungerValue) {
        setIsFed(isFed() || hungerValue > 0);
    }

    @Unique
    public void feed(ItemStack itemStack) {
        int hunger = itemStack.isOf(BwtItems.kibbleItem) ? 2 : Optional.ofNullable(itemStack.getFoodComponent()).orElse(new FoodComponent.Builder().build()).getHunger();
        ((WolfEntity) ((Object) this)).heal(hunger);
        this.feed(hunger);
    }

    @Unique
    public void setIsFed(boolean value) {
        ((WolfEntity) ((Object) this)).getDataTracker().set(IS_FED, value);
    }

    @Unique
    public boolean isInTheDark() {
        WolfEntity wolfThis = ((WolfEntity) ((Object) this));
        return wolfThis.getWorld().getLightLevel(wolfThis.getBlockPos()) < 5;
    }
}
