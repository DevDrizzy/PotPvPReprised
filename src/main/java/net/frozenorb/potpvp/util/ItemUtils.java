package net.frozenorb.potpvp.util;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.util.NumberUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@UtilityClass
public final class ItemUtils {

    private final Map<String, ItemData> NAME_MAP = new HashMap<>();

    /**
     * Checks if a {@link ItemStack} is an instant heal potion (if its type is {@link PotionType#INSTANT_HEAL})
     */
    public final Predicate<ItemStack> INSTANT_HEAL_POTION_PREDICATE =item -> {
        if (item.getType() != Material.POTION) {
            return false;
        }

        PotionType potionType = Potion.fromItemStack(item).getType();
        return potionType == PotionType.INSTANT_HEAL;
    };

    /**
     * Checks if a {@link ItemStack} is a bowl of mushroom soup (if its type is {@link Material#MUSHROOM_SOUP})
     */
    public final Predicate<ItemStack> SOUP_PREDICATE = item -> item.getType() == Material.MUSHROOM_SOUP;

    /**
     * Checks if a {@link ItemStack} is a debuff potion
     */
    public final Predicate<ItemStack> DEBUFF_POTION_PREDICATE = item -> {
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
    public final Predicate<ItemStack> EDIBLE_PREDICATE = item -> item.getType().isEdible();

    /**
     * Returns the number of stacks of items matching the predicate provided.
     *
     * @param items ItemStack array to scan
     * @param predicate The predicate which will be applied to each non-null temStack.
     * @return The amount of ItemStacks which matched the predicate, or 0 if {@code items} was null.
     */
    public int countStacksMatching(ItemStack[] items, Predicate<ItemStack> predicate) {
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

    public ItemData[] repeat(Material material, int times) {
        return repeat(material, (byte) 0, times);
    }

    public ItemData[] repeat(Material material, byte data, int times) {
        ItemData[] itemData = new ItemData[times];

        for (int i = 0; i < times; i++) {
            itemData[i] = new ItemData(material, data);
        }

        return itemData;

    }

    public ItemData[] armorOf(ArmorPart part) {
        List<ItemData> data = new ArrayList<>();

        for (ArmorType at : ArmorType.values()) {
            data.add(new ItemData(Material.valueOf(at.name() + "_" + part.name()), (short) 0));
        }

        return data.toArray(new ItemData[data.size()]);
    }

    public ItemData[] swords() {
        List<ItemData> data = new ArrayList<>();

        for (SwordType at : SwordType.values()) {
            data.add(new ItemData(Material.valueOf(at.name() + "_SWORD"), (short) 0));
        }

        return data.toArray(new ItemData[data.size()]);
    }

    public void load() {
        NAME_MAP.clear();

        List<String> lines = readLines();

        for (String line : lines) {
            String[] parts = line.split(",");

            NAME_MAP.put(parts[0], new ItemData(Material.getMaterial(Integer.parseInt(parts[1])), Short.parseShort(parts[2])));
        }
    }

    public ItemStack get(String input, int amount) {
        ItemStack item = get(input);

        if (item != null) item.setAmount(amount);

        return item;
    }

    public ItemStack get(String input) {
        if (NumberUtils.isInteger(input)) {
            return new ItemStack(Material.getMaterial(Integer.parseInt(input)));
        }

        if (input.contains(":")) {
            if (NumberUtils.isShort(input.split(":")[1])) {
                if (NumberUtils.isInteger(input.split(":")[0])) {
                    return new ItemStack(Material.getMaterial(Integer.parseInt(input.split(":")[0])), 1, Short.parseShort(input.split(":")[1]));
                } else {
                    if (!NAME_MAP.containsKey(input.split(":")[0].toLowerCase())) {
                        return null;
                    }

                    ItemData data = NAME_MAP.get(input.split(":")[0].toLowerCase());
                    return new ItemStack(data.getMaterial(), 1, Short.parseShort(input.split(":")[1]));
                }
            } else {
                return null;
            }
        }

        if (!NAME_MAP.containsKey(input)) {
            return null;
        }

        return NAME_MAP.get(input).toItemStack();
    }

    public String getName(ItemStack item) {
        if (item.getDurability() != 0) {
            net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);

            if (nmsStack != null) {
                String name = nmsStack.getName();

                if (name.contains(".")) {
                    name = WordUtils.capitalize(item.getType().toString().toLowerCase().replace("_", " "));
                }

                return name;
            }
        }

        String string = item.getType().toString().replace("_", " ");
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public void setDisplayName(ItemStack itemStack, String string) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(string);
        itemStack.setItemMeta(itemMeta);
    }

    public ItemBuilder builder(Material type) {
        return ItemBuilder.of(type);
    }

    private List<String> readLines() {
        try {
            return IOUtils.readLines(PotPvPRP.class.getClassLoader().getResourceAsStream("items.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Getter
    @AllArgsConstructor
    public class ItemData {

        private final Material material;
        private final short data;

        public String getName() {
            return ItemUtils.getName(toItemStack());
        }

        public boolean matches(ItemStack item) {
            return item != null && item.getType() == material && item.getDurability() == data;
        }

        public ItemStack toItemStack() {
            return new ItemStack(material, 1, data);
        }

    }

    public enum ArmorPart {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS
    }

    public enum ArmorType {
        DIAMOND, IRON, GOLD, LEATHER
    }

    public enum SwordType {
        DIAMOND, IRON, GOLD, STONE
    }


}
