package com.gly091020.CreateTreadmill;

import com.gly091020.CreateTreadmill.config.ClothConfigScreenGetter;
import com.gly091020.CreateTreadmill.ponder.TreadmillPonderPlugin;
import com.gly091020.CreateTreadmill.renderer.TreadmillVisual;
import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;
import com.simibubi.create.foundation.config.ui.BaseConfigScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class CreateTreadmillClient {

    public static final PartialModel BELT_MODEL = new PartialModel(new ResourceLocation(CreateTreadmillMod.MOD_ID, "block/belt"));
    public static final SpriteShiftEntry BELT_SHIFT = SpriteShifter.get(new ResourceLocation(CreateTreadmillMod.MOD_ID, "block/belt"), new ResourceLocation(CreateTreadmillMod.MOD_ID, "block/belt_shift"));

    public static void onCtorClient(ModLoadingContext context, IEventBus modEventBus) {
        modEventBus.addListener(CreateClient::clientInit);
        modEventBus.addListener(CreateTreadmillClient::clientInit);

        context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(
                (mc, parent) -> {
                    if (ModList.get().isLoaded("cloth_config")) {
                        return ClothConfigScreenGetter.get(parent);
                    }
                    return new BaseConfigScreen(parent, CreateTreadmillMod.MOD_ID);
                }
        ));
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        InstancedRenderRegistry.configure(CreateTreadmillMod.TREADMILL_ENTITY.get())
                .factory(TreadmillVisual::new)
                .apply();

        event.enqueueWork(TreadmillPonderPlugin::register);
    }
}