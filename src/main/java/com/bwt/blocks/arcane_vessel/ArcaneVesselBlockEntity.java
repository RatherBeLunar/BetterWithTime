package com.bwt.blocks.arcane_vessel;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.data.BwtDataAttachments;
import com.bwt.data.DragonOrbData;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.bwt.blocks.MechPowerBlockBase.MECH_POWERED;

public class ArcaneVesselBlockEntity extends BlockEntity
{
    static final public int MAX_CONTAINED_EXPERIENCE = 1000;

    static final private int MIN_TEMPLE_EXPERIENCE = 200;
    static final private int MAX_TEMPLE_EXPERIENCE = 256;

    static final public int MAX_VISUAL_EXPERIENCE_LEVEL = 10;
    private static final Property<Direction> FACING = Properties.FACING;

    private final int xpEjectUnitSize = 20;

    private int visualExperienceLevel;

    private int containedRegularExperience;
    private int containedDragonExperience;


    public ArcaneVesselBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.arcaneVesselBlockEntity, pos, state);
        visualExperienceLevel = 0;

        containedRegularExperience = 0;
        containedDragonExperience = 0;
    }


    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("regXP", containedRegularExperience);
        nbt.putInt("dragXP", containedDragonExperience);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        containedRegularExperience = nbt.getInt("regXP");
        containedDragonExperience = nbt.getInt("dragXP");

        int iTotalExperience = containedRegularExperience + containedDragonExperience;

        visualExperienceLevel = MAX_VISUAL_EXPERIENCE_LEVEL * iTotalExperience / MAX_CONTAINED_EXPERIENCE;

        if (iTotalExperience > 0 && visualExperienceLevel == 0) {
            visualExperienceLevel = 1;
        }

    }

    public void setContainedRegularExperience(int xp) {
        containedRegularExperience = xp;
        validateVisualExperience();
    }

    public void validateVisualExperience() {
        int iTotalExperience = containedRegularExperience + containedDragonExperience;

        int iNewVisualExperience = (int)((float) MAX_VISUAL_EXPERIENCE_LEVEL * ((float)iTotalExperience / (float) MAX_CONTAINED_EXPERIENCE) );

        if (iTotalExperience > 0 && iNewVisualExperience == 0) {
            iNewVisualExperience = 1;
        }

        // mark block to be sent to client
        if (iNewVisualExperience != visualExperienceLevel && world != null) {
            visualExperienceLevel = iNewVisualExperience;

            world.markDirty(pos);
        }
    }

    public boolean attemptToSwallowXPOrb(World world, BlockPos pos, ExperienceOrbEntity orb)
    {
        int totalContainedXP = containedRegularExperience + containedDragonExperience;
        int remainingSpace = MAX_CONTAINED_EXPERIENCE - totalContainedXP;

        if (remainingSpace > 0) {
            int xpToAddToInventory;

            var data = orb.getAttached(BwtDataAttachments.dragonOrbData);
            assert data != null;
            orb.setAttached(BwtDataAttachments.dragonOrbData, new DragonOrbData(true));

            int amount = orb.getExperienceAmount();

            if (amount <= remainingSpace)
            {
                xpToAddToInventory = amount;

                orb.discard();
            } else {
                xpToAddToInventory = remainingSpace;
            }

            if (data.isValid()) {
                setContainedDragonExperience(containedDragonExperience + xpToAddToInventory);
            } else {
                setContainedRegularExperience(containedRegularExperience + xpToAddToInventory);
            }

            return true;
        }

        return false;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ArcaneVesselBlockEntity vesselBE) {
        if (!(state.getBlock() instanceof ArcaneVesselBlock)) {
            // shouldn't happen
            return;
        }

        if (state.get(MECH_POWERED)) {
            Direction facing = state.get(FACING);
            vesselBE.attemptToSpillXPFromInv(world, pos, facing, vesselBE);
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, ArcaneVesselBlockEntity be) {

    }

    private void attemptToSpillXPFromInv(World world, BlockPos pos, Direction facing, ArcaneVesselBlockEntity be) {
        int xpToSpill = 0;
        boolean spillDragonOrb = false;

        if (containedDragonExperience > 0 || containedRegularExperience > 0 && !isTiltedOutputBlocked(world, pos, facing))
        {
            if (containedDragonExperience > 0) {
                spillDragonOrb = true;

                xpToSpill = Math.min(containedDragonExperience, xpEjectUnitSize);

                be.setContainedDragonExperience(containedDragonExperience - xpToSpill);
            } else {
                xpToSpill = Math.min(containedRegularExperience, xpEjectUnitSize);

                setContainedRegularExperience(containedRegularExperience - xpToSpill);
            }
        }

        if (xpToSpill > 0) {
            spillXPOrb(xpToSpill, spillDragonOrb, facing);
        }
    }

    public void setContainedDragonExperience(int iExperience) {
        containedDragonExperience = iExperience;

        validateVisualExperience();
    }

    private boolean isTiltedOutputBlocked(World world, BlockPos pos, Direction facing) {
        pos.add(facing.getVector());
        BlockState state = world.getBlockState(pos);
        if (!world.isAir(pos)) {
            if (!state.isAir()) {
                return state.isSolidBlock(world, pos);
            }
        }

        return false;
    }

    public void ejectContentsOnBlockBreak() {
        while (containedRegularExperience > 0) {
            int iEjectSize = Math.min(containedRegularExperience, xpEjectUnitSize);
            ejectXPOrbOnBlockBreak(iEjectSize, false);
            containedRegularExperience -= iEjectSize;
        }

        while (containedDragonExperience > 0) {
            int iEjectSize = Math.min(containedDragonExperience, xpEjectUnitSize);
            ejectXPOrbOnBlockBreak(iEjectSize, true);
            containedDragonExperience -= iEjectSize;
        }
    }


    public void spillXPOrb(int xpValue, boolean bDragonOrb, Direction facing) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Vec3d dirVec = convertBlockFacingToVector(facing).multiply(0.5);

        Vec3d spawnPos = new Vec3d(
                this.getPos().getX() + 0.5 + dirVec.x,
                this.getPos().getY() + 0.25 + dirVec.y,
                this.getPos().getZ() + 0.5 + dirVec.z + world.random.nextFloat() * 0.3
        );

        if (Math.abs(dirVec.x) > 0.1) {
            spawnPos = spawnPos.add((world.random.nextFloat() * 0.5) - 0.25, 0, 0);
        } else {
            spawnPos = spawnPos.add(0, 0, (world.random.nextFloat() * 0.5) - 0.25);
        }

        ExperienceOrbEntity orb = new ExperienceOrbEntity(world, spawnPos.x, spawnPos.y, spawnPos.z, xpValue);

        Vec3d velocity = convertBlockFacingToVector(facing).multiply(0.1);
        orb.setVelocity(velocity);

        var data = orb.getAttached(BwtDataAttachments.dragonOrbData);
        if (data != null) {
            data.setValid(true);
        }

        serverWorld.spawnEntity(orb);
    }

    private void ejectXPOrbOnBlockBreak(int iXPValue, boolean bDragonOrb) {
        if (world == null) return;
        double xOffset = world.getRandom().nextDouble() * 0.7D + 0.15D;
        double yOffset = world.getRandom().nextDouble() * 0.7D + 0.15D;
        double zOffset = world.getRandom().nextDouble() * 0.7D + 0.15D;

        ExperienceOrbEntity orb = new ExperienceOrbEntity(world,
                this.getPos().getX() + xOffset, this.getPos().getY() + yOffset, this.getPos().getZ() + zOffset,
                iXPValue);

        var data = orb.getAttached(BwtDataAttachments.dragonOrbData);
        if (data != null) {
            data.setValid(true);
        }

        orb.prevX = (float)world.getRandom().nextGaussian() * 0.05F;
        orb.prevY = (float)world.getRandom().nextGaussian() * 0.05F + 0.2F;
        orb.prevZ = (float)world.getRandom().nextGaussian() * 0.05F;

        world.spawnEntity(orb);
    }

    public int getContainedTotalExperience() {
        return containedDragonExperience + containedRegularExperience;
    }

    public int getContainedRegularExperience()
    {
        return containedRegularExperience;
    }

    /**
     * Returns a normalized vector in the direction of the block facing.
     */
    public static Vec3d convertBlockFacingToVector(Direction direction) {
        return new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()).normalize();
    }

}
