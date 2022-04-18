package net.frozenorb.potpvp.kit.kittype;

import net.frozenorb.potpvp.util.ItemUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

import lombok.Getter;

public enum HealingMethod {

    POTIONS(
        "pot", "pots", // short singular/plural
        "health potion", "health potions", // long singular/plural
        Material.POTION,
        (short) 16421, // data for splash healing II pot
        i -> ItemUtils.countStacksMatching(i, ItemUtils.INSTANT_HEAL_POTION_PREDICATE)
    ),
    GOLDEN_APPLE(
        "gap", "gaps", // short singular/plural
        "golden apple", "golden apples", // long singular/plural
        Material.GOLDEN_APPLE,
        (short) 1, // data for enchanted golden apple
        items -> {
            int count = 0;

            for (ItemStack item : items) {
                if (item != null && item.getType() == Material.GOLDEN_APPLE && item.getData().getData() == (byte) 1) {
                    count += Math.max(1, item.getAmount());
                }
            }

            return count;
        }
    ),
    SOUP(
        "soup", "soup", // short singular/plural
        "soup", "soup", // long singular/plural
        Material.MUSHROOM_SOUP,
        (short) 0,
        i -> ItemUtils.countStacksMatching(i, ItemUtils.SOUP_PREDICATE)
    );

    @Getter private final String shortSingular;
    @Getter private final String shortPlural;
    @Getter private final String longSingular;
    @Getter private final String longPlural;

    @Getter private final Material iconType;
    @Getter private final short iconDurability;
    private final Function<ItemStack[], Integer> countFunction;

    HealingMethod(String shortSingular, String shortPlural, String longSingular, String longPlural, Material iconType, short iconDurability, Function<ItemStack[], Integer> countFunction) {
        this.shortSingular = shortSingular;
        this.shortPlural = shortPlural;
        this.longSingular = longSingular;
        this.longPlural = longPlural;
        this.iconType = iconType;
        this.iconDurability = iconDurability;
        this.countFunction = countFunction;
    }

    public int count(ItemStack[] items) {
        return countFunction.apply(items);
    }

}
