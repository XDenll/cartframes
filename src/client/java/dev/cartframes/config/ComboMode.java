package dev.cartframes.config;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

public enum ComboMode {

    CROSSBOW_CART("Crossbow Cart", List.of(
            Seq.place(Items.TNT_MINECART),
            Seq.place(Items.RAIL),
            Seq.use(Items.FLINT_AND_STEEL),
            Seq.use(Items.CROSSBOW)
    )),

    OVERLOAD_CART("Overload Cart", List.of(
            Seq.place(Items.RAIL),
            Seq.place(Items.TNT_MINECART),
            Seq.place(Items.TNT_MINECART),
            Seq.use(Items.FLINT_AND_STEEL),
            Seq.use(Items.CROSSBOW)
    )),

    NORMAL_INSTA_CART("Normal Insta Cart", List.of(
            Seq.flameBow(),
            Seq.place(Items.RAIL),
            Seq.place(Items.TNT_MINECART)
    ));

    private final String displayName;
    private final List<Seq> sequence;

    ComboMode(String displayName, List<Seq> sequence) {
        this.displayName = displayName;
        this.sequence = sequence;
    }

    public String getDisplayName() { return displayName; }
    public List<Seq> getSequence() { return sequence; }

    /**
     * One combo step. Per-step indexing (not "seen item") is what correctly
     * handles Overload Cart's two consecutive TNT minecart placements.
     */
    public record Seq(Item item, boolean requireFlame) {

        static Seq place(Item item) { return new Seq(item, false); }
        static Seq use(Item item)   { return new Seq(item, false); }
        static Seq flameBow()       { return new Seq(Items.BOW, true); }

        /**
         * NOTE ON THE FLAME CHECK:
         * As of 1.20.5+/1.21, enchantments are data-driven registry entries.
         * Enchantments.FLAME is only a ResourceKey<Enchantment> — it cannot be
         * compared directly against a Set<Holder<Enchantment>>, and it cannot be
         * resolved into a Holder without a RegistryAccess. That RegistryAccess is
         * only available through a live Player/Level, never in a static enum
         * context. This is why `matches` takes the Player: it looks the holder up
         * from player.level().registryAccess() at call time, then asks
         * EnchantmentHelper for the level on the stack.
         */
        public boolean matches(Player player, ItemStack stack) {
            if (stack == null || stack.isEmpty()) return false;
            if (stack.getItem() != item) return false;
            if (!requireFlame) return true;

            Holder<Enchantment> flameHolder = player.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.FLAME);

            return EnchantmentHelper.getItemEnchantmentLevel(flameHolder, stack) > 0;
        }
    }
}
