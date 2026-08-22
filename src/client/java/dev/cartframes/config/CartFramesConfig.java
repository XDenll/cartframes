package dev.cartframes.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "cartframes")
public class CartFramesConfig implements ConfigData {

    public boolean modEnabled = true;

    public ComboMode activeMode = ComboMode.CROSSBOW_CART;

    public HudPosition hudPosition = HudPosition.TOP_LEFT;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1920)
    public int customX = 10;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1080)
    public int customY = 10;

    public DisplayStyle displayStyle = DisplayStyle.BACKGROUND_BOX;
}
