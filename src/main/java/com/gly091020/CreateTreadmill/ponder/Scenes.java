package com.gly091020.CreateTreadmill.ponder;

import com.gly091020.CreateTreadmill.block.TreadmillBlockEntity;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;

public class Scenes {
    public static void treadmillRun(SceneBuilder builder, SceneBuildingUtil util){
        builder.title("treadmill_run", "跑步机的使用");
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
                .text("这是一台跑步机。")
                .pointAt(util.vector.of(2, 2, 2));
        builder.addKeyframe();
        builder.idle(50);
        builder.world.createEntity(level -> {
            var entity = new Villager(EntityType.VILLAGER, level);
            entity.setPos(1, 1, 1);
            var e = level.getBlockEntity(util.grid.at(2, 1, 3));
            if(e instanceof TreadmillBlockEntity treadmillBlockEntity){
                treadmillBlockEntity.setOnTreadmillEntity(entity);
            }
            return entity;
        });
        builder.idle(10);
        builder.world.setKineticSpeed(selection, -32);
        builder.overlay.showText(40)
                .placeNearTarget()
                .text("可以让生物在上面向前跑来驱动跑步机")
                .pointAt(util.vector.of(2, 2, 2));
        builder.idle(45);
        builder.addKeyframe();
        builder.overlay.showText(40)
                .placeNearTarget()
                .text("玩家可以用拴绳将生物带到跑步机上")
                .pointAt(util.vector.of(2, 2, 2));
        builder.idle(45);
        builder.overlay.showText(40)
                .placeNearTarget()
                .text("但是，它们可能不太愿意被压榨……")
                .pointAt(util.vector.of(2, 2, 2));
        builder.idle(45);
        builder.markAsFinished();
    }

    public static void treadmillFly(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("treadmill_fly", "装B让你___");
        builder.configureBasePlate(0, 0, 5);
        builder.world.showSection(util.select.layer(0), Direction.UP);

        BlockPos pos1 = util.grid.at(2, 1, 2);
        BlockPos pos2 = util.grid.at(2, 2, 3);
        Selection selection = util.select.fromTo(pos1, pos2);

        builder.idle(5);
        builder.world.showSection(selection, Direction.DOWN);
        builder.world.setKineticSpeed(selection, 32);

        builder.overlay.showText(40)
                .placeNearTarget()
                .text("这台跑步机的应力接反了……")
                .pointAt(util.vector.of(2, 2, 2));
        builder.idle(50);
        builder.overlay.showText(40)
                .placeNearTarget()
                .text("所以上面的生物会被 飞起来！")
                .pointAt(util.vector.of(2, 2, 2));
        builder.idle(50);
        builder.overlay.showText(40)
                .placeNearTarget()
                .text("我不会移动实体……假装有个村民被推了出去")
                .pointAt(util.vector.of(2, 2, 2));
        builder.idle(40);
        builder.markAsFinished();
    }

    public static void treadmillSpeedUp(SceneBuilder builder, SceneBuildingUtil util) {
        builder.title("treadmill_speedup", "加速");
        builder.configureBasePlate(0, 0, 5);
        builder.world.showSection(util.select.layer(0), Direction.UP);

        BlockPos pos1 = util.grid.at(2, 1, 2);
        BlockPos pos2 = util.grid.at(2, 2, 3);
        Selection selection = util.select.fromTo(pos1, pos2);

        BlockPos pos3 = util.grid.at(3, 1, 0);
        BlockPos pos4 = util.grid.at(4, 3, 4);
        Selection selection1 = util.select.fromTo(pos3, pos4);

        builder.idle(5);
        builder.world.showSection(selection1, Direction.DOWN);
        var hand = util.select.position(util.grid.at(2, 5, 2));
        builder.world.showSection(hand, Direction.DOWN);
        builder.idle(5);
        builder.world.showSection(selection, Direction.DOWN);
        builder.idle(5);
        builder.world.showSection(util.select.position(util.grid.at(1, 1, 3)), Direction.DOWN);
        builder.world.createEntity(level -> {
            var entity = new Villager(EntityType.VILLAGER, level);
            entity.setPos(1, 1, 1);
            var e = level.getBlockEntity(util.grid.at(2, 1, 3));
            if(e instanceof TreadmillBlockEntity treadmillBlockEntity){
                treadmillBlockEntity.setOnTreadmillEntity(entity);
            }
            return entity;
        });
        builder.world.setKineticSpeed(selection, -32);
        builder.world.setKineticSpeed(util.select.position(util.grid.at(1, 1, 3)), -32);
        builder.idle(20);
        builder.overlay.showText(40)
                .text("32 RPM")
                .pointAt(util.vector.of(1.5, 1.5, 3));
        builder.idle(40);
        builder.world.setKineticSpeed(hand, -64);
        builder.world.setKineticSpeed(util.select.position(util.grid.at(1, 1, 3)), -64);
        builder.world.moveDeployer(util.grid.at(2, 5, 2), 1, 5);
        builder.idle(5);
        builder.world.setKineticSpeed(selection, -64);
        builder.world.moveDeployer(util.grid.at(2, 5, 2), -1, 5);
        builder.addKeyframe();
        builder.overlay.showText(40)
                .placeNearTarget()
                .text("当跑步机上的实体被攻击时，跑步机会加速一分钟（两倍速度，不可叠加）")
                .pointAt(util.vector.of(2, 2, 2));
        builder.idle(45);
        builder.overlay.showText(40)
                .text("64 RPM")
                .pointAt(util.vector.of(1.5, 1.5, 3));
        builder.idle(55);
        builder.overlay.showText(40)
                .placeNearTarget()
                .text("但我相信，跑步机上的生物会更想逃脱")
                .pointAt(util.vector.of(2, 2, 2));
        builder.idle(45);
        builder.markAsFinished();
    }
}