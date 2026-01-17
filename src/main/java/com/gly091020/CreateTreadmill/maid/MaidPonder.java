package com.gly091020.CreateTreadmill.maid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.CreateTreadmill.CreateTreadmillMod;
import com.gly091020.CreateTreadmill.block.TreadmillBlockEntity;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class MaidPonder {
    public static void registry() {
        // 修正：使用 PonderStoryBoardEntry 手动注册
        ResourceLocation schematic = new ResourceLocation(CreateTreadmillMod.MOD_ID, "treadmill/run");
        ResourceLocation component = CreateTreadmillMod.TREADMILL_BLOCK.getId();

        PonderRegistry.addStoryBoard(new PonderStoryBoardEntry(
                MaidPonder::treadmillMaid,
                CreateTreadmillMod.MOD_ID,
                schematic,
                component
        ));
    }

    public static void treadmillMaid(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("treadmill_maid", "跑步机的使用");
        builder.configureBasePlate(0, 0, 5);
        builder.world.showSection(util.select.layer(0), Direction.UP);

        BlockPos pos1 = util.grid.at(2, 1, 2);
        BlockPos pos2 = util.grid.at(2, 2, 3);
        Selection selection = util.select.fromTo(pos1, pos2);

        builder.idle(5);
        builder.world.showSection(selection, Direction.DOWN);
        builder.idle(10);
        builder.overlay.showText(40)
                .placeNearTarget()
                .text("如果你安装了车万女仆，你可以指定女仆任务为“跑步机”来让女仆使用跑步机")
                .pointAt(util.vector.of(2, 2, 2));
        builder.addKeyframe();
        builder.world.createEntity(level -> {
            var entity = new EntityMaid(level);
            entity.setPos(1, 1, 1);
            var e = level.getBlockEntity(util.grid.at(2, 1, 3));
            if (e instanceof TreadmillBlockEntity treadmillBlockEntity) {
                treadmillBlockEntity.setOnTreadmillEntity(entity);
            }
            entity.walkAnimation.setSpeed(3);
            return entity;
        });
        builder.world.setKineticSpeed(selection, -32);
        builder.idle(60);
        builder.overlay.showText(40)
                .placeNearTarget()
                .text("当然，之前的操作也是通用的……")
                .pointAt(util.vector.of(2, 2, 2));
        builder.addKeyframe();
        builder.idle(45);
        builder.overlay.showText(40)
                .placeNearTarget()
                .text("而且女仆不会轻易逃脱……")
                .pointAt(util.vector.of(2, 2, 2));
        builder.addKeyframe();
        builder.idle(45);
        builder.markAsFinished();
    }
}