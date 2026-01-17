package com.gly091020.CreateTreadmill.renderer;

import com.gly091020.CreateTreadmill.CreateTreadmillClient;
import com.gly091020.CreateTreadmill.Part;
import com.gly091020.CreateTreadmill.block.TreadmillBlock;
import com.gly091020.CreateTreadmill.block.TreadmillBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.jozufozu.flywheel.backend.Backend;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class TreadmillRenderer extends KineticBlockEntityRenderer<TreadmillBlockEntity> {
    public TreadmillRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(TreadmillBlockEntity be, BlockState state) {
        return CachedBufferer.block(KineticBlockEntityRenderer.KINETIC_BLOCK,
                KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be)));
    }

    @Override
    public boolean shouldRender(@NotNull TreadmillBlockEntity blockEntity, @NotNull Vec3 cameraPos) {
        return blockEntity.getBlockState().getValue(TreadmillBlock.PART) == Part.BOTTOM_FRONT;
    }

    @Override
    protected void renderSafe(TreadmillBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if(be.getBlockState().getValue(TreadmillBlock.PART) != Part.BOTTOM_FRONT){return;}

        if(!Backend.canUseInstancing(be.getLevel())){
            super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        }

        int lightAbove = be.getLevel() != null ? LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().above()) : light;
        renderBelt(be, ms, buffer, lightAbove);
    }

    private void renderBelt(TreadmillBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light) {
        var facing = be.getBlockState().getValue(TreadmillBlock.HORIZONTAL_FACING);
        SpriteShiftEntry spriteShift = CreateTreadmillClient.BELT_SHIFT;

        SuperByteBuffer beltBuffer = CachedBufferer.partial(CreateTreadmillClient.BELT_MODEL, be.getBlockState())
                .light(light);

        float d = 0;

        switch (facing){
            case WEST: d = 0; break;
            case EAST: d = 180; break;
            case SOUTH: d = 90; break;
            case NORTH: d = 270; break;
        }

        ms.pushPose();

        ms.translate(0.5, 1.0, 0.5);
        ms.mulPose(new Quaternionf().rotationY(d * Mth.DEG_TO_RAD));

        float shiftAmount = 1.0f / 16.0f;
        ms.translate(shiftAmount, 0, 0);

        ms.translate(-0.5, 0, -0.5);

        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        float renderTick = AnimationTickHolder.getRenderTime(be.getLevel());
        Direction.AxisDirection axisDirection = facing.getAxisDirection();
        float speed = be.getSpeed();
        if(facing == Direction.SOUTH || facing == Direction.NORTH){
            speed = -speed;
        }
        double scroll = speed * renderTick * axisDirection.getStep() / (31.5 * 16);
        scroll = scroll - Math.floor(scroll);
        scroll = scroll * 0.062;

        beltBuffer.shiftUVScrolling(spriteShift, (float) scroll);
        beltBuffer.renderInto(ms, vb);
        ms.popPose();
    }
}