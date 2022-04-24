package net.frozenorb.potpvp.util.serialization;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ItemStackSerializer {

    public static final BasicDBObject AIR = new BasicDBObject();

    static {
        AIR.put("type", "AIR");
        AIR.put("amount", 1);
        AIR.put("data", 0);
    }

    private ItemStackSerializer() {
    }

    public static BasicDBObject serialize(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return (AIR);
        }

        BasicDBObject item = new BasicDBObject("type", itemStack.getType().toString()).append("amount", itemStack.getAmount()).append("data", itemStack.getDurability());
        BasicDBList enchants = new BasicDBList();
        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            enchants.add(new BasicDBObject("enchantment", entry.getKey().getName()).append("level", entry.getValue()));
        }
        if (itemStack.getEnchantments().size() > 0)
            item.append("enchants", enchants);
        if (itemStack.hasItemMeta()) {
            ItemMeta m = itemStack.getItemMeta();
            BasicDBObject meta = new BasicDBObject("displayName", m.getDisplayName());
            if (m.getLore() != null) {
                //BasicDBList lore = new BasicDBList();
                //lore.addAll(m.getLore());
                //meta.append("lore", lore);
            }
            item.append("meta", meta);
        }
        return item;
    }

    public static ItemStack deserialize(BasicDBObject dbObject) {
        if (dbObject == null || dbObject.isEmpty()) {
            return (new ItemStack(Material.AIR));
        }

        Material type = Material.valueOf(dbObject.getString("type"));
        ItemStack item = new ItemStack(type, dbObject.getInt("amount"));
        item.setDurability(Short.parseShort(dbObject.getString("data")));

        if (dbObject.containsField("enchants")) {
            BasicDBList enchs = (BasicDBList) dbObject.get("enchants");
            for (Object o : enchs) {
                BasicDBObject enchant = (BasicDBObject) o;
                item.addUnsafeEnchantment(Enchantment.getByName(enchant.getString("enchantment")), enchant.getInt("level"));
            }
        }

        if (dbObject.containsField("meta")) {
            BasicDBObject meta = (BasicDBObject) dbObject.get("meta");
            ItemMeta m = item.getItemMeta();
            if (meta.containsField("displayName")) {
                m.setDisplayName(meta.getString("displayName"));
            }
            if (meta.containsField("lore")) {
                //m.setLore((List<String>) ((BasicDBList)meta.get("lore")).stream().collect(Collectors.toList()));
            }
            item.setItemMeta(m);
        }

        return (item);
    }

}