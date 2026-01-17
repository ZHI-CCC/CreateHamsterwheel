package com.gly091020.CreateTreadmill.renderer;

import com.gly091020.CreateTreadmill.CreateTreadmillClient;
import com.gly091020.CreateTreadmill.Part;
import com.gly091020.CreateTreadmill.block.TreadmillBlock;
import com.gly091020.CreateTreadmill.block.TreadmillBlockEntity;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TreadmillVisual extends KineticBlockEntityInstance<TreadmillBlockEntity> {

    protected RotatingData shaft;

    public TreadmillVisual(MaterialManager materialManager, TreadmillBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    public void init() {
        if (blockState.getValue(TreadmillBlock.PART) != Part.BOTTOM_FRONT)
            return;

        BlockState shaftState = KineticBlockEntityRenderer.shaft(getRotationAxis());
        shaft = getRotatingMaterial().getModel(shaftState).createInstance();
        setup(shaft);

        shaft.setBlockLight(15)
                .setSkyLight(15);


        updateLight();
    }

    @Override
    public void update() {
        if (shaft != null) {
            updateRotation(shaft);
        }
    }

    @Override
    public void updateLight() {
    }

    @Override
    public void remove() {
        if (shaft != null)
            shaft.delete();
    }
}