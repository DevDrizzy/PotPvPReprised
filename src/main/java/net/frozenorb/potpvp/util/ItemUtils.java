package net.frozenorb.potpvp.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.function.Predicate;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ItemUtils {

    /**
     * Checks if a {@link ItemStack} is an instant heal potion (if its type is {@link PotionType#INSTANT_HEAL})
     */
    public static final Predicate<ItemStack> INSTANT_HEAL_POTION_PREDICATE = item -> {
        if (item.getType() != Material.POTION) {
            return false;
        }

        PotionType potionType = Potion.fromItemStack(item).getType();
        return potionType == PotionType.INSTANT_HEAL;
    };

    /**
     * Checks if a {@link ItemStack} is a bowl of mushroom soup (if its type is {@link Material#MUSHROOM_SOUP})
     */
    public static final Predicate<ItemStack> SOUP_PREDICATE = item -> item.getType() == Material.MUSHROOM_SOUP;

    /**
     * Checks if a {@link ItemStack} is a debuff potion
     */
    public static final Predicate<ItemStack> DEBUFF_POTION_PREDICATE = item -> {
        if (item.getType() == Material.POTION) {
            PotionType type = Potion.fromItemStack(item).getType();
            return type == PotionType.WEAKNESS || type == PotionType.SLOWNESS
                || type == PotionType.POISON || type == PotionType.INSTANT_DAMAGE;
        } else {
            return false;
        }
    };

    /**
     * Checks if a {@link ItemStack} is edible (if its type passes {@link Material#isEdible()})
     */
    public static final Predicate<ItemStack> EDIBLE_PREDICATE = item -> item.getType().isEdible();

    /**
     * Returns the number of stacks of items matching the predicate provided.
     *
     * @param items ItemStack array to scan
     * @param predicate The predicate which will be applied to each non-null temStack.
     * @return The amount of ItemStacks which matched the predicate, or 0 if {@code items} was null.
     */
    public static int countStacksMatching(ItemStack[] items, Predicate<ItemStack> predicate) {
        if (items == null) {
            return 0;
        }

        int amountMatching = 0;

        for (ItemStack item : items) {
            if (item != null && predicate.test(item)) {
                amountMatching++;
            }
        }

        return amountMatching;
    }

}