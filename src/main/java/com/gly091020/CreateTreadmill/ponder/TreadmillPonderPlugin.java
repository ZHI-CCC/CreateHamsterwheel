package com.gly091020.CreateTreadmill.ponder;

import com.gly091020.CreateTreadmill.CreateTreadmillMod;
import com.gly091020.CreateTreadmill.maid.MaidPonder;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

public class TreadmillPonderPlugin {

    public static void register() {
        registerScenes();
        registerTags();
    }

    public static void registerScenes() {
        ResourceLocation schematic = new ResourceLocation(CreateTreadmillMod.MOD_ID, "treadmill/run");
        ResourceLocation speedupSchematic = new ResourceLocation(CreateTreadmillMod.MOD_ID, "treadmill/speedup");
        ResourceLocation component = CreateTreadmillMod.TREADMILL_BLOCK.getId();

        PonderRegistry.addStoryBoard(new PonderStoryBoardEntry(
                Scenes::treadmillRun,
                CreateTreadmillMod.MOD_ID,
                schematic,
                component
        ).highlightAllTags()); // 添加标签

        PonderRegistry.addStoryBoard(new PonderStoryBoardEntry(
                Scenes::treadmillFly,
                CreateTreadmillMod.MOD_ID,
                schematic,
                component
        ));

        PonderRegistry.addStoryBoard(new PonderStoryBoardEntry(
                Scenes::treadmillSpeedUp,
                CreateTreadmillMod.MOD_ID,
                speedupSchematic,
                component
        ));

        if (ModList.get().isLoaded("touhou_little_maid")) {
            MaidPonder.registry();
        }
    }

    public static void registerTags() {
        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_SOURCES)
                .add(CreateTreadmillMod.TREADMILL_BLOCK);
    }
}