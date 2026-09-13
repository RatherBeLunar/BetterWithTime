package com.bwt.entities;

import com.bwt.damage_types.BwtDamageTypes;
import com.bwt.items.BwtItems;
import com.bwt.sounds.BwtSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import java.util.Arrays;

public class DynamiteEntity extends Projectile implements ItemSupplier {

    public static final int TICKS_TO_DETONATE = 100;

    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(DynamiteEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(DynamiteEntity.class, EntityDataSerializers.INT);

    protected DynamiteEntity(EntityType<DynamiteEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DynamiteEntity(double x, double y, double z, Level level) {
        this(BwtEntities.dynamiteEntity, level);
        this.setPos(x, y, z);
    }

    public DynamiteEntity(Level level, LivingEntity owner) {
        this(owner.getX(), owner.getEyeY() - (double)0.1f, owner.getZ(), level);
        this.setOwner(owner);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ITEM, ItemStack.EMPTY);
        builder.define(FUSE, -1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("fuse", getFuse());
        ItemStack itemStack = getItem();
        if (!itemStack.isEmpty()) {
            nbt.put("Item", itemStack.save(registryAccess()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setFuse(nbt.getInt("fuse"));
        if (nbt.contains("Item", Tag.TAG_COMPOUND)) {
            setItem(ItemStack.parse(registryAccess(), nbt.getCompound("Item")).orElseGet(() -> new ItemStack(getDefaultItem())));
        } else {
            setItem(new ItemStack(getDefaultItem()));
        }
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getEntityItem();
        return itemStack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemStack;
    }

    protected Item getDefaultItem() {
        return BwtItems.dynamiteItem;
    }

    public int getFuse() {
        return entityData.get(FUSE);
    }

    public void setFuse(int value) {
        entityData.set(FUSE, value);
    }

    protected ItemStack getEntityItem() {
        return this.getEntityData().get(ITEM);
    }

    public void setItem(ItemStack item) {
        this.getEntityData().set(ITEM, item.copyWithCount(1));
    }

    public void ignite() {
        entityData.set(FUSE, TICKS_TO_DETONATE);
        playSound(BwtSoundEvents.DYNAMITE_IGNITE, 0.5f, 1.0f);
    }

    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d)) {
            d = 4.0;
        }
        return distance < (d *= 64.0) * d;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            this.push(0.0, -0.04, 0.0);
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }

        int fuse = getFuse();
        if (fuse > 0) {
            setFuse(fuse - 1);
            fuse--;
            if (level().isClientSide) {
                level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), getDeltaMovement().x() * 0.1, getDeltaMovement().y() * 0.1, getDeltaMovement().z() * 0.1);
            }
        }

        if (fuse == 0) {
            discard();
            if (!level().isClientSide) {
                explode();
            }
            return;
        }

        if (fuse < 0) {
            if (this.isInLava()) {
                ignite();
                discard();
                if (!level().isClientSide) {
                    explode();
                }
                return;
            }
            if (onGround()) {
                if (getDeltaMovement().length() < 0.01) {
                    // The dynamite has come to a stop. Convert it to an item.
                    if (!level().isClientSide) {
                        convertToItem();
                        return;
                    }
                }
            }
        }
    }

    public void explode() {
        this.level().explode(this, this.getX(), this.getY(0.0625), this.getZ(), 1.5f, Level.ExplosionInteraction.TNT);
        if (isUnderWater()) {
            redneckFish();
        }
    }

    private void redneckFish() {
        for (int i = getBlockX() - 2; i <= getBlockX() + 2; i++) {
            // favor deep water
            for (int j = getBlockY() - 2; j <= getBlockY() + 4; j++) {
                for (int k = getBlockZ() - 2; k <= getBlockZ() + 2; k++) {
                    BlockPos offsetPos = new BlockPos(i, j, k);
                    if (!isValidBlockForRedneckFishing(offsetPos)) {
                        continue;
                    }
                    // averages one per j layer
                    if (random.nextInt( 25 ) == 0 ) {
                        spawnRedneckFish(offsetPos);
                    }
                }
            }
        }
    }

    private boolean isValidBlockForRedneckFishing(BlockPos pos) {
        // block must be totally surrounded by water (except positive j) to be valid
        return Arrays.stream(Direction.values())
                .filter(direction -> direction != Direction.UP)
                .map(pos::relative)
                .map(offsetPos -> level().getFluidState(offsetPos))
                .allMatch(fluidState -> fluidState.is(FluidTags.WATER));
    }

    private void spawnRedneckFish(BlockPos pos) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        LootParams lootContextParameterSet = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.DAMAGE_SOURCE, BwtDamageTypes.of(serverLevel, DamageTypes.EXPLOSION))
                .create(LootContextParamSets.ENTITY);
        LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING_FISH);
        ObjectArrayList<ItemStack> list = lootTable.getRandomItems(lootContextParameterSet);
        if (list.isEmpty()) {
            return;
        }
        ItemStack itemStack = list.get(random.nextInt(list.size()));
        if (!itemStack.is(ItemTags.FISHES)) return;
        ItemEntity itemEntity = new ItemEntity(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
        this.level().addFreshEntity(itemEntity);
    }

    private void convertToItem() {
        Containers.dropItemStack(level(), getX(), getY(), getZ(), getItem());
        discard();
    }
}
