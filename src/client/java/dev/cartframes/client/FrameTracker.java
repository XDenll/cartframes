package dev.cartframes.client;

import dev.cartframes.config.CartFramesConfig;
import dev.cartframes.config.ComboMode;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class FrameTracker {
    private static final int RESULT_LIFETIME_TICKS = 200;
    private static final FrameTracker INSTANCE = new FrameTracker();

    private final CartFramesConfig config;
    private boolean active;
    private int step;
    private int clickCount;
    private int runningFrames;
    private int lastFrames = -1;
    private int resultTicksRemaining;

    // A single physical right-click fires UseBlockCallback AND UseItemCallback
    // (interactBlock -> interactItemInternal when the block callback PASSes),
    // and entity interactions may also fire UseEntityCallback. All of these can
    // land in the same client tick for one click. Dedupe on the tick counter so
    // one physical action only ever advances the state machine once.
    private int tickCounter;
    private int lastHandledTick = -1;

    private FrameTracker() {
        this.config = AutoConfig.getConfigHolder(CartFramesConfig.class).getConfig();
    }

    public static FrameTracker getInstance() {
        return INSTANCE;
    }

    public void register() {
        // These callbacks fire on both logical sides. Comparing the player
        // reference to Minecraft.getInstance().player below filters out the
        // integrated server thread re-firing the same interaction.
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            onUse(player, hand);
            return InteractionResult.PASS;
        });
        UseItemCallback.EVENT.register((player, world, hand) -> {
            onUse(player, hand);
            return InteractionResult.PASS;
        });
        // Igniting a TNT minecart, or firing a bow/crossbow at an entity, goes
        // through the entity-interaction path, which UseBlockCallback/UseItemCallback
        // do not cover on their own.
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            onUse(player, hand);
            return InteractionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounter++;
            if (active && config.modEnabled) {
                runningFrames++; // "frames" == client ticks, per the spec
            }
            if (resultTicksRemaining > 0 && --resultTicksRemaining == 0) {
                lastFrames = -1;
            }
        });
    }

    private void onUse(Player player, InteractionHand hand) {
        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player) return;
        if (!config.modEnabled) return;

        if (tickCounter == lastHandledTick) return; // same physical click, already handled
        lastHandledTick = tickCounter;

        List<ComboMode.Seq> seq = config.activeMode.getSequence();
        if (seq.isEmpty()) return;

        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return; // empty-hand interactions don't disturb a run

        if (!active) {
            if (seq.get(0).matches(player, held)) {
                active = true;
                step = 1;
                clickCount = 1;
                runningFrames = 0;
                lastFrames = -1; // hide the previous result while a new run is in progress
                resultTicksRemaining = 0;
            }
            return;
        }

        if (clickCount >= config.activeMode.getMaxClicks()) {
            completeRun();
            return;
        }

        if (seq.get(step).matches(player, held)) {
            step++;
            clickCount++;
            if (clickCount >= config.activeMode.getMaxClicks()) {
                completeRun();
            }
        } else {
            reset(); // wrong item for the expected next step -> reset the timer
        }
    }

    private void reset() {
        active = false;
        step = 0;
        clickCount = 0;
        runningFrames = 0;
    }

    private void completeRun() {
        lastFrames = runningFrames; // save the final frame count
        resultTicksRemaining = RESULT_LIFETIME_TICKS;
        reset();                    // stop the timer
    }

    public boolean isActive()     { return active; }
    public boolean hasResult()    { return lastFrames >= 0; }
    public int getLastFrames()    { return lastFrames; }
    public int getRunningFrames() { return runningFrames; }
}
