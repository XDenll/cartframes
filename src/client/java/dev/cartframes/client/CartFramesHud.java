package dev.cartframes.client;

import dev.cartframes.config.CartFramesConfig;
import dev.cartframes.config.DisplayStyle;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.DeltaTracker;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public final class CartFramesHud {
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int BOX_COLOR  = 0x80000000;

    private CartFramesHud() {}

    public static void register() {
        HudRenderCallback.EVENT.register(CartFramesHud::render);
    }

    private static void render(GuiGraphics guiGraphics, DeltaTracker tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        CartFramesConfig cfg = AutoConfig.getConfigHolder(CartFramesConfig.class).getConfig();
        if (!cfg.modEnabled) return;

        FrameTracker tracker = FrameTracker.getInstance();
        if (!tracker.isActive() && !tracker.hasResult()) return;
        int frames = tracker.isActive()
            ? tracker.getRunningFrames()
            : tracker.getLastFrames();

        String text = (cfg.displayStyle == DisplayStyle.BRACKETS)
                ? "[ " + frames + " ]"
                : String.valueOf(frames);

        Font font = mc.font;
        int textWidth = font.width(text);
        int lineHeight = font.lineHeight;
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        int x, y;
        switch (cfg.hudPosition) {
            case TOP_LEFT     -> { x = 2; y = 2; }
            case TOP_RIGHT    -> { x = sw - textWidth - 2; y = 2; }
            case MIDDLE       -> { x = (sw - textWidth) / 2; y = (sh - lineHeight) / 2; }
            case BOTTOM       -> { x = (sw - textWidth) / 2; y = sh - lineHeight - 2; }
            case BOTTOM_LEFT  -> { x = 2; y = sh - lineHeight - 2; }
            case BOTTOM_RIGHT -> { x = sw - textWidth - 2; y = sh - lineHeight - 2; }
            case CUSTOM       -> { x = cfg.customX; y = cfg.customY; }
            default           -> { x = 2; y = 2; }
        }

        guiGraphics.pose().pushMatrix();
        try {
            if (cfg.displayStyle == DisplayStyle.BACKGROUND_BOX) {
                guiGraphics.fill(x - 2, y - 2, x + textWidth + 2, y + lineHeight + 2, BOX_COLOR);
            }
            guiGraphics.drawString(font, text, x, y, TEXT_COLOR);
        } finally {
            guiGraphics.pose().popMatrix();
        }
    }
}
