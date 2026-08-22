package dev.cartframes;

import dev.cartframes.client.CartFramesHud;
import dev.cartframes.client.FrameTracker;
import dev.cartframes.config.CartFramesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class CartFramesClient implements ClientModInitializer {
    public static final String MOD_ID = "cartframes";

    @Override
    public void onInitializeClient() {
        AutoConfig.register(CartFramesConfig.class, GsonConfigSerializer::new);
        FrameTracker.getInstance().register();
        CartFramesHud.register();
    }
}
