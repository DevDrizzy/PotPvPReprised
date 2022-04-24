package net.frozenorb.potpvp.util.serialization;

import com.google.gson.*;
import net.frozenorb.potpvp.PotPvPRP;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("deprecation")
public class ItemStackAdapter implements JsonDeserializer<ItemStack>,JsonSerializer<ItemStack> {

    public JsonElement serialize(ItemStack item, Type type, JsonSerializationContext context) {
        return ItemStackAdapter.serialize(item);
    }

    public ItemStack deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        return ItemStackAdapter.deserialize(element);
    }

    public static JsonElement serialize(ItemStack item) {
        if (item == null) {
            item=new ItemStack(Material.AIR);
        }
        final JsonObject element=new JsonObject();
        element.addProperty("id", item.getTypeId());
        element.addProperty(getDataKey(item), item.getDurability());
        element.addProperty("count", item.getAmount());
        if (item.hasItemMeta()) {
            final ItemMeta meta=item.getItemMeta();
            if (meta.hasDisplayName()) {
                element.addProperty("name", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                element.add("lore", convertStringList(meta.getLore()));
            }
            if (meta instanceof LeatherArmorMeta) {
                element.addProperty("color", ((LeatherArmorMeta) meta).getColor().asRGB());
            } else if (meta instanceof SkullMeta) {
                element.addProperty("skull", ((SkullMeta) meta).getOwner());
            } else if (meta instanceof BookMeta) {
                element.addProperty("title", ((BookMeta) meta).getTitle());
                element.addProperty("author", ((BookMeta) meta).getAuthor());
                element.add("pages", convertStringList(((BookMeta) meta).getPages()));
            } else if (meta instanceof PotionMeta) {
                if (!((PotionMeta) meta).getCustomEffects().isEmpty()) {
                    element.add("potion-effects", convertPotionEffectList(((PotionMeta) meta).getCustomEffects()));
                }
            } else if (meta instanceof MapMeta) {
                element.addProperty("scaling", ((MapMeta) meta).isScaling());
            } else if (meta instanceof EnchantmentStorageMeta) {
                final JsonObject storedEnchantments=new JsonObject();
                for ( final Map.Entry<Enchantment, Integer> entry : ((EnchantmentStorageMeta) meta).getStoredEnchants().entrySet() ) {
                    storedEnchantments.addProperty(entry.getKey().getName(), entry.getValue());
                }
                element.add("stored-enchants", storedEnchantments);
            }
        }
        if (item.getEnchantments().size() != 0) {
            final JsonObject enchantments=new JsonObject();
            for ( final Map.Entry<Enchantment, Integer> entry2 : item.getEnchantments().entrySet() ) {
                enchantments.addProperty(entry2.getKey().getName(), entry2.getValue());
            }
            element.add("enchants", enchantments);
        }
        return element;
    }

    public static ItemStack deserialize(JsonElement object) {
        JsonObject enchantments;
        if (!(object instanceof JsonObject)) {
            return new ItemStack(Material.AIR);
        }
        JsonObject element=(JsonObject) object;
        int id=element.get("id").getAsInt();
        short data=element.has("damage") ? element.get("damage").getAsShort() : (element.has("data") ? element.get("data").getAsShort() : (short) 0);
        int count=element.get("count").getAsInt();
        ItemStack item=new ItemStack(id, count, data);
        ItemMeta meta=item.getItemMeta();
        if (element.has("name")) {
            meta.setDisplayName(element.get("name").getAsString());
        }
        if (element.has("lore")) {
            meta.setLore(ItemStackAdapter.convertStringList(element.get("lore")));
        }
        if (element.has("color")) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(element.get("color").getAsInt()));
        } else if (element.has("skull")) {
            ((SkullMeta) meta).setOwner(element.get("skull").getAsString());
        } else if (element.has("title")) {
            ((BookMeta) meta).setTitle(element.get("title").getAsString());
            ((BookMeta) meta).setAuthor(element.get("author").getAsString());
            ((BookMeta) meta).setPages(ItemStackAdapter.convertStringList(element.get("pages")));
        } else if (element.has("potion-effects")) {
            PotionMeta potionMeta=(PotionMeta) meta;
            for ( PotionEffect effect : ItemStackAdapter.convertPotionEffectList(element.get("potion-effects")) ) {
                potionMeta.addCustomEffect(effect, false);
            }
        } else if (element.has("scaling")) {
            ((MapMeta) meta).setScaling(element.get("scaling").getAsBoolean());
        } else if (element.has("stored-enchants")) {
            enchantments=(JsonObject) element.get("stored-enchants");
            for ( Enchantment enchantment : Enchantment.values() ) {
                if (!enchantments.has(enchantment.getName())) continue;
                ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, enchantments.get(enchantment.getName()).getAsInt(), true);
            }
        }
        item.setItemMeta(meta);
        if (element.has("enchants")) {
            enchantments=(JsonObject) element.get("enchants");
            for ( Enchantment enchantment : Enchantment.values() ) {
                if (!enchantments.has(enchantment.getName())) continue;
                item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment.getName()).getAsInt());
            }
        }
        return item;
    }

    private static String getDataKey(ItemStack item) {
        if (item.getType() == Material.AIR) {
            return "data";
        }
        if (Enchantment.DURABILITY.canEnchantItem(item)) {
            return "damage";
        }
        return "data";
    }

    public static JsonArray convertStringList(Collection<String> strings) {
        JsonArray ret=new JsonArray();
        for ( String string : strings ) {
            ret.add(new JsonPrimitive(string));
        }
        return ret;
    }

    public static List<String> convertStringList(JsonElement jsonElement) {
        JsonArray array=jsonElement.getAsJsonArray();
        ArrayList<String> ret=new ArrayList<>();
        for ( JsonElement element : array ) {
            ret.add(element.getAsString());
        }
        return ret;
    }

    public static JsonArray convertPotionEffectList(Collection<PotionEffect> potionEffects) {
        JsonArray ret=new JsonArray();
        for ( PotionEffect e : potionEffects ) {
            ret.add(PotionEffectAdapter.toJson(e));
        }
        return ret;
    }

    public static List<PotionEffect> convertPotionEffectList(JsonElement jsonElement) {
        if (jsonElement == null) {
            return null;
        }
        if (!jsonElement.isJsonArray()) {
            return null;
        }
        JsonArray array=jsonElement.getAsJsonArray();
        ArrayList<PotionEffect> ret=new ArrayList<>();
        for ( JsonElement element : array ) {
            PotionEffect e=PotionEffectAdapter.fromJson(element);
            if (e == null) continue;
            ret.add(e);
        }
        return ret;
    }
}

