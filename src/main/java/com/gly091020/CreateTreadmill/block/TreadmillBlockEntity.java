package com.gly091020.CreateTreadmill.block;

import com.gly091020.CreateTreadmill.CreateTreadmillMod;
import com.gly091020.CreateTreadmill.Part;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static com.gly091020.CreateTreadmill.block.TreadmillBlock.PART;
import static com.gly091020.CreateTreadmill.block.TreadmillBlock.findPart;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class TreadmillBlockEntity extends GeneratingKineticBlockEntity {
    private LivingEntity onTreadmillEntity;
    private int speedUpTimer = 0;
    private int entityTimer = Integer.MAX_VALUE;

    public TreadmillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getBlockState().getValue(PART) != Part.BOTTOM_FRONT) {
            return;
        }

        if (onTreadmillEntity != null) {
            if (onTreadmillEntity.isRemoved() || !onTreadmillEntity.position().closerThan(getFixedPos(), 2)) {
                setOnTreadmillEntity(null);
                return;
            }
            setPos();
            speedUp();
            onTreadmillEntity.setDeltaMovement(Vec3.atLowerCornerOf(getBlockState().getValue(HorizontalKineticBlock.HORIZONTAL_FACING).getNormal()).multiply(0.3f, 0, 0.3f));
            if (onTreadmillEntity instanceof Player player) {
                onTreadmillEntity.hurtMarked = true;
                if (player.isShiftKeyDown() || player.getPose() == Pose.SITTING) {
                    setOnTreadmillEntity(null);
                }
            } else {
                onTreadmillEntity.lookAt(EntityAnchorArgument.Anchor.EYES, onTreadmillEntity.getEyePosition().relative(getBlockState().getValue(HorizontalKineticBlock.HORIZONTAL_FACING), 1));
                onTreadmillEntity.setPose(Pose.STANDING);
                if (onTreadmillEntity instanceof TamableAnimal tamableAnimal) {
                    tamableAnimal.setInSittingPose(false);
                }
            }
            dropIt();
        }

        if (speedUpTimer > 0) {
            speedUpTimer--;
            if (speedUpTimer == 0) update();
        }
        if (entityTimer <= 0) {
            setOnTreadmillEntity(null);
        } else if (entityTimer < Integer.MAX_VALUE) {
            entityTimer--;
            if (speedUpTimer > 0) {
                entityTimer--;
            }
        }
    }

    @Override
    public float getGeneratedSpeed() {
        if (onTreadmillEntity == null)
            return 0;

        int speedUp = this.speedUpTimer > 0 ? 2 : 1;
        switch (getBlockState().getValue(TreadmillBlock.HORIZONTAL_FACING)) {
            case NORTH, EAST:
                return getSettingSpeed() * speedUp;
            case SOUTH, WEST:
                return -getSettingSpeed() * speedUp;
        }
        return 0;
    }

    private void update() {
        updateGeneratedRotation();
        notifyUpdate();
        sendData();
    }

    public void setOnTreadmillEntity(@Nullable LivingEntity onTreadmillEntity) {
        if (this.onTreadmillEntity == onTreadmillEntity) return;

        if (onTreadmillEntity == null && this.onTreadmillEntity != null) {
            this.onTreadmillEntity.setDeltaMovement(Vec3.ZERO);
            CreateTreadmillMod.WALKING_ENTITY.remove(this.onTreadmillEntity.getId());
            this.onTreadmillEntity.walkAnimation.setSpeed(0);
        }
        if (onTreadmillEntity != null) {
            CreateTreadmillMod.WALKING_ENTITY.put(onTreadmillEntity.getId(), onTreadmillEntity);
        } else {
            speedUpTimer = 0;
            entityTimer = Integer.MAX_VALUE;
        }
        this.onTreadmillEntity = onTreadmillEntity;
        setPos();
        update();
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag() {
        CompoundTag update = super.getUpdateTag();
        update.putInt("speedup_timer", speedUpTimer);
        update.putInt("entity_timer", entityTimer);
        if (onTreadmillEntity != null)
            update.putInt("entity", onTreadmillEntity.getId());
        return update;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        speedUpTimer = tag.getInt("speedup_timer");
        entityTimer = tag.getInt("entity_timer");
        if (tag.contains("entity")) {
            if (level != null) {
                Entity entity = level.getEntity(tag.getInt("entity"));
                if (entity instanceof LivingEntity le) {
                    this.onTreadmillEntity = le;
                } else {
                    this.onTreadmillEntity = null;
                }
            }
        } else {
            this.onTreadmillEntity = null;
        }
    }

    public void setEntityTimer(int entityTimer) {
        if (!CreateTreadmillMod.CONFIG.TREADMILL_BREAK.get()) {
            this.entityTimer = Integer.MAX_VALUE;
            return;
        }
        this.entityTimer = entityTimer;
    }

    public Entity getOnTreadmillEntity() {
        return onTreadmillEntity;
    }

    public void setPos() {
        if (onTreadmillEntity != null) {
            onTreadmillEntity.setPos(getFixedPos());
            onTreadmillEntity.setOnGround(true);
        }
    }

    public void speedUp() {
        if (!CreateTreadmillMod.CONFIG.TREADMILL_SPEED_UP.get()) {
            return;
        }
        if (onTreadmillEntity.hurtTime > 0 && !(onTreadmillEntity.getLastHurtMob() instanceof Player)) {
            if (onTreadmillEntity.getLastDamageSource() != null && speedUpTimer <= 0) {
                speedUpTimer = 1200;
                update();
            }
        }
    }

    public Vec3 getFixedPos() {
        var p = this.getBlockPos().above();
        var y = p.getY() + 5.5 / 16;
        switch (getBlockState().getValue(HORIZONTAL_FACING)) {
            case WEST: return new Vec3(p.getX() + 1, y, p.getZ() + 0.5);
            case EAST: return new Vec3(p.getX(), y, p.getZ() + 0.5);
            case NORTH: return new Vec3(p.getX() + 0.5, y, p.getZ() + 1);
            case SOUTH: return new Vec3(p.getX() + 0.5, y, p.getZ());
        }
        return Vec3.atCenterOf(p);
    }

    private void dropIt() {
        if (!CreateTreadmillMod.CONFIG.TREADMILL_DROP_IT.get()) {
            return;
        }
        switch (getBlockState().getValue(TreadmillBlock.HORIZONTAL_FACING)) {
            case NORTH, EAST:
                if (getSpeed() < 0) {
                    float m = 3f * (Math.abs(getSpeed()) / 256);
                    onTreadmillEntity.setDeltaMovement(Vec3.atLowerCornerOf(getBlockState().getValue(HorizontalKineticBlock.HORIZONTAL_FACING).getNormal()).multiply(m, 0, m));
                    setOnTreadmillEntity(null);
                }
                break;
            case SOUTH, WEST:
                if (getSpeed() > 0) {
                    float m = 3f * (Math.abs(getSpeed()) / 256);
                    onTreadmillEntity.setDeltaMovement(Vec3.atLowerCornerOf(getBlockState().getValue(HorizontalKineticBlock.HORIZONTAL_FACING).getNormal()).multiply(m, 0, m));
                    setOnTreadmillEntity(null);
                }
                break;
        }
    }

    public float getSettingSpeed() {
        return CreateTreadmillMod.CONFIG.TREADMILL_BASE_SPEED.get();
    }

    public static TreadmillBlockEntity getBlockEntityByEntity(Entity entity) {
        if (entity == null || entity.level().isClientSide()) return null;
        var level = entity.level();
        if (level.getBlockState(entity.blockPosition()).is(CreateTreadmillMod.TREADMILL_BLOCK.get())) {
            var part = TreadmillBlock.findPart(level, level.getBlockState(entity.blockPosition()),
                    entity.blockPosition(), Part.BOTTOM_FRONT);
            if (level.getBlockEntity(part) instanceof TreadmillBlockEntity treadmillBlockEntity) {
                return treadmillBlockEntity;
            }
        }
        return null;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (getBlockState().getValue(PART) != Part.BOTTOM_FRONT) {
            if (level == null) return false;
            BlockEntity be = level.getBlockEntity(findPart(level, getBlockState(), getBlockPos(), Part.BOTTOM_FRONT));
            if (be instanceof TreadmillBlockEntity mainBe && mainBe != this) {
                return mainBe.addToGoggleTooltip(tooltip, isPlayerSneaking);
            }
            return false;
        }

        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        addToolTip(tooltip);
        return true;
    }

    public void addToolTip(List<Component> tooltip) {
        if (speedUpTimer > 0) {
            tooltip.add(Component.translatable("tip.createtreadmill.speedup", speedUpTimer / 20));
        }
        if (entityTimer > 0 && entityTimer < Integer.MAX_VALUE) {
            tooltip.add(Component.translatable("tip.createtreadmill.break", entityTimer / 20));
        }
    }
}