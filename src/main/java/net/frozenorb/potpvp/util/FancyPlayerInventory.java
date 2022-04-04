package net.frozenorb.potpvp.util;

import net.frozenorb.potpvp.PotPvPSI;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PlayerInventory;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FancyPlayerInventory extends PlayerInventory {

    private static final Map<UUID, FancyPlayerInventory> storage = new HashMap<>();
    private static final Set<UUID> open = new HashSet<>();

    private CraftPlayer owner;
    private boolean playerOnline = false;
    private ItemStack[] extra = new ItemStack[5];
    private CraftInventory inventory = new CraftInventory(this);

    private FancyPlayerInventory(Player player) {
        super(((CraftPlayer) player).getHandle());
        this.owner = ((CraftPlayer) player);
        this.playerOnline = player.isOnline();
        this.items = this.player.inventory.items;
        this.armor = this.player.inventory.armor;

        storage.put(owner.getUniqueId(), this);
    }

    private Inventory getBukkitInventory() {
        return inventory;
    }

    private void removalCheck() {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> owner.saveData());

        if (transaction.isEmpty() && !playerOnline) {
            storage.remove(owner.getUniqueId());
        }
    }

    private void onJoin(Player joined) {
        if (!playerOnline) {
            CraftPlayer player = (CraftPlayer) joined;
            player.getHandle().inventory.items = this.items;
            player.getHandle().inventory.armor = this.armor;
            playerOnline = true;

            Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> owner.saveData());
        }
    }

    private void onQuit() {
        playerOnline = false;

        this.removalCheck();
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        super.onClose(who);
        open.remove(who.getUniqueId());
        this.removalCheck();
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[getSize()];

        System.arraycopy(items, 0, contents, 0, items.length);
        System.arraycopy(items, 0, contents, items.length, armor.length);

        return contents;
    }

    @Override
    public int getSize() {
        return super.getSize() + 5;
    }

    @Override
    public ItemStack getItem(int i) {
        ItemStack[] is;

        if (i >= 0 && i <= 4) {
            i = getReversedArmorSlotNum(i);
            is = this.armor;
        } else if (i >= 5 && i <= 8) {
            i -= 4;
            is = this.extra;
        } else {
            i -= 9;
            is = this.items;

            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        }
        else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        return is[i];
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        ItemStack[] is;

        if (i >= 0 && i <= 4) {
            i = getReversedArmorSlotNum(i);
            is = this.armor;
        } else if (i >= 5 && i <= 8) {
            i -= 4;
            is = this.extra;
        } else {
            i -= 9;
            is = this.items;

            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        }
        else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        if (is[i] != null) {
            ItemStack itemstack;

            if (is[i].count <= j) {
                itemstack = is[i];
                is[i] = null;
                return itemstack;
            }
            else {
                itemstack = is[i].cloneAndSubtract(j);
                if (is[i].count == 0) {
                    is[i] = null;
                }

                return itemstack;
            }
        }
        else {
            return null;
        }
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        ItemStack[] is;

        if (i >= 0 && i <= 4) {
            i = getReversedArmorSlotNum(i);
            is = this.armor;
        } else if (i >= 5 && i <= 8) {
            i -= 4;
            is = this.extra;
        } else {
            i -= 9;
            is = this.items;

            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        }
        else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        if (is[i] != null) {
            ItemStack itemstack = is[i];

            is[i] = null;
            return itemstack;
        }
        else {
            return null;
        }
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        ItemStack[] is;

        if (i >= 0 && i <= 4) {
            i = getReversedArmorSlotNum(i);
            is = this.armor;
        } else if (i >= 5 && i <= 8) {
            i -= 4;
            is = this.extra;
        } else {
            i -= 9;
            is = this.items;

            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        }
        else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        if (is == this.extra) {
            owner.getHandle().drop(itemstack, true);
            itemstack = null;
        }

        is[i] = itemstack;

        owner.getHandle().defaultContainer.b();
    }

    private int getReversedItemSlotNum(int i) {
        return i >= 27 ? i - 27 : i + 9;
    }

    private int getReversedArmorSlotNum(int i) {
        switch (i) {
            case 0:
                return 3;
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 0;
            default:
                return i;
        }
    }


    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    public static void open(Player owner, Player openFor) {
        FancyPlayerInventory inventory = storage.containsKey(owner.getUniqueId()) ? storage.get(owner.getUniqueId()) : new FancyPlayerInventory(owner);
        openFor.openInventory(inventory.getBukkitInventory());

        open.add(openFor.getUniqueId());
    }

    public static boolean isViewing(Player player) {
        return open.contains(player.getUniqueId());
    }

    public static void join(Player player) {
        if (storage.containsKey(player.getUniqueId())) {
            storage.get(player.getUniqueId()).onJoin(player);
        }
    }

    public static void quit(Player player) {
        if (storage.containsKey(player.getUniqueId())) {
            storage.get(player.getUniqueId()).onQuit();
        }
    }

}