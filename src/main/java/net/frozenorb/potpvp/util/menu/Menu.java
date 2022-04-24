package net.frozenorb.potpvp.util.menu;

import net.frozenorb.potpvp.PotPvPRP;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Menu {

    static {
        currentlyOpenedMenus = new HashMap<>();
        checkTasks = new HashMap<>();
    }

    private static Method openInventoryMethod;
    @Getter private ConcurrentHashMap<Integer, Button> buttons = new ConcurrentHashMap<>();

    @Getter @Setter private boolean autoUpdate = false;
    @Getter @Setter private boolean updateAfterClick = true;
    @Getter @Setter private boolean placeholder = false;
    @Getter @Setter private boolean noncancellingInventory = false;

    @Getter private static Map<UUID,Menu> currentlyOpenedMenus;
    @Getter private static Map<UUID,BukkitRunnable> checkTasks;

    private Inventory createInventory(Player player) {

        final Inventory inventory = PotPvPRP.getInstance().getServer().createInventory(player, size(player), getTitle(player));

        for (Map.Entry<Integer, Button> buttonEntry : getButtons(player).entrySet()) {

            this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());

            final ItemStack item = createItemStack(player, buttonEntry.getValue());

            inventory.setItem(buttonEntry.getKey(), item);
        }

        if (this.isPlaceholder()) {

            final Button placeholder = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15);

            for (int index = 0; index < this.size(player); index++) {

                if (this.getButtons(player).get(index) == null) {
                    this.buttons.put(index, placeholder);
                    inventory.setItem(index, placeholder.getButtonItem(player));
                }

            }

        }

        return inventory;
    }

    private static Method getOpenInventoryMethod() {
        if (openInventoryMethod == null) {
            try {
                openInventoryMethod = CraftHumanEntity.class.getDeclaredMethod("openCustomInventory", Inventory.class, EntityPlayer.class, String.class);
                openInventoryMethod.setAccessible(true);
            } catch (NoSuchMethodException var1) {
                var1.printStackTrace();
            }
        }

        return openInventoryMethod;
    }

    private ItemStack createItemStack(Player player, Button button) {
        ItemStack item = button.getButtonItem(player);

        if (item.getType() != Material.SKULL_ITEM) {

            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                meta.setDisplayName(meta.getDisplayName() + "§k§e§r§e§m");
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public void openMenu(Player player) {

        final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        final Inventory inventory = this.createInventory(player);

        try {
            getOpenInventoryMethod().invoke(player,inventory,entityPlayer,"minecraft:chest");
            this.update(player);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    private void update(Player player) {
        cancelCheck(player);
        currentlyOpenedMenus.put(player.getUniqueId(),this);

        this.onOpen(player);

        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {

                if (!player.isOnline()) {
                    Menu.cancelCheck(player);
                    Menu.currentlyOpenedMenus.remove(player.getUniqueId());
                }

                if (isAutoUpdate()) {
                    player.getOpenInventory().getTopInventory().setContents(createInventory(player).getContents());
                }

            }
        };

        runnable.runTaskTimer(PotPvPRP.getInstance(), 10L, 10L);
        checkTasks.put(player.getUniqueId(), runnable);
    }


    public static void cancelCheck(Player player) {
        if (checkTasks.containsKey(player.getUniqueId())) {
            checkTasks.remove(player.getUniqueId()).cancel();
        }

    }

    public int size(Player player) {
        int highest = 0;

        for (int buttonValue : getButtons(player).keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }

    public int getSlot(int x, int y) {
        return ((9 * y) + x);
    }


    public abstract String getTitle(Player player);

    public abstract Map<Integer, Button> getButtons(Player player);

    public void onOpen(Player player) {}

    public void onClose(Player player) {}

}