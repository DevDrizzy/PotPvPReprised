package net.frozenorb.potpvp.command.impl;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.kit.kittype.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/8/2022
 * Project: potpvp-reprised
 */

public class KitCommands implements PotPvPCommand {

    //TODO: Do this lol
    private static final String[] HELP_MESSAGE = {
            ChatColor.DARK_PURPLE + PotPvPLang.LONG_LINE,
            "§5§lKit Commands",
            ChatColor.DARK_PURPLE + PotPvPLang.LONG_LINE,
            "§c " + PotPvPLang.LEFT_ARROW_NAKED + " §a/kit create <name>",
            "§c " + PotPvPLang.LEFT_ARROW_NAKED + " §a/kit delete <kitType>",
            ChatColor.DARK_PURPLE + PotPvPLang.LONG_LINE,
    };

    @Command(name = "", desc = "Help message for kits")
    @Require("potpvp.kit.admin")
    public void help(@Sender Player sender) {
        sender.sendMessage(HELP_MESSAGE);
    }

    @Command(name = "create", usage = "<name>", desc = "Creates a new kit-type")
    @Require("potpvp.kit.admin")
    public void execute(@Sender Player player, String id) {
        if (KitType.byId(id) != null) {
            player.sendMessage(ChatColor.RED + "A kit-type by that name already exists.");
            return;
        }

        KitType kitType = new KitType(id);
        kitType.setDisplayName(id);
        kitType.setDisplayColor(ChatColor.GOLD);
        kitType.setIcon(new MaterialData(Material.DIAMOND_SWORD));
        kitType.setSort(50);
        kitType.saveAsync();

        KitType.getAllTypes().add(kitType);
        PotPvPRP.getInstance().getQueueHandler().addQueues(kitType);

        player.sendMessage(ChatColor.GREEN + "You've created a new kit-type by the ID \"" + kitType.getId() + "\".");
    }

    @Command(name = "delete", usage = "<kitType>", desc = "Deletes an existing kit-type")
    @Require("potpvp.kit.admin")
    public void execute(@Sender Player player, KitType kitType) {
        kitType.deleteAsync();
        KitType.getAllTypes().remove(kitType);
        PotPvPRP.getInstance().getQueueHandler().removeQueues(kitType);

        player.sendMessage(ChatColor.GREEN + "You've deleted the kit-type by the ID \"" + kitType.getId() + "\".");
    }

    @Command(name = "loadDefault", usage = "<kitType>", desc = "Load the default inventory of a KitType")
    @Require("potpvp.kit.admin")
    public void loadDefaults(@Sender Player sender, KitType kitType) {
        sender.getInventory().setArmorContents(kitType.getDefaultArmor());
        sender.getInventory().setContents(kitType.getDefaultInventory());
        sender.updateInventory();

        sender.sendMessage(ChatColor.YELLOW + "Loaded default armor/inventory for " + kitType + ".");
    }

    @Command(name = "saveDefault", usage = "<kitType>", desc = "Load the default inventory of a KitType")
    @Require("potpvp.kit.admin")
    public void saveDefaults(@Sender Player sender, KitType kitType) {
        kitType.setDefaultArmor(sender.getInventory().getArmorContents());
        kitType.setDefaultInventory(sender.getInventory().getContents());
        kitType.saveAsync();

        sender.sendMessage(ChatColor.YELLOW + "Saved default armor/inventory for " + kitType + ".");
    }

    @Command(name = "setdisplaycolor", usage = "<kitType> <color>", desc = "Set a KitType's display color")
    @Require("potpvp.kit.admin")
    public void setDisplayColor(@Sender Player sender, KitType kitType, ChatColor color) {
        kitType.setDisplayColor(color);
        kitType.saveAsync();

        sender.sendMessage(ChatColor.GREEN + "You've updated this kit-type's display color.");
    }

    @Command(name = "setdisplayname", usage = "<kitType> <name>", desc = "Set a KitType's display name")
    @Require("potpvp.kit.admin")
    public void setDisplayName(@Sender Player sender, KitType kitType, String name) {
        kitType.setDisplayName(name);
        kitType.saveAsync();

        sender.sendMessage(ChatColor.GREEN + "You've updated this kit-type's display name.");
    }

    @Command(name = "seticon", usage = "<kitType>", desc = "Set a KitType's display icon")
    @Require("potpvp.kit.admin")
    public void setIcon(@Sender Player player, KitType kitType) {
        if (player.getItemInHand() == null) {
            player.sendMessage(ChatColor.RED + "Please hold an item in your hand.");
            return;
        }

        kitType.setIcon(player.getItemInHand().getData());
        kitType.saveAsync();

        player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's icon.");
    }

    @Command(name = "setpriority", usage = "<kitType> <priority>", desc = "Set a KitType's sort priority")
    @Require("potpvp.kit.admin")
    public void setDisplayName(@Sender Player sender, KitType kitType, int sort) {
        kitType.setSort(sort);
        kitType.saveAsync();

        KitType.getAllTypes().sort(Comparator.comparing(KitType::getSort));

        sender.sendMessage(ChatColor.GREEN + "You've updated this kit-type's sort.");
    }

    @Command(name = "import", desc = "Import kitTypes from KitTypes.json")
    @Require("potpvp.kit.admin")
    public void importKitTypes(CommandSender sender) {
        File file = new File(PotPvPRP.getInstance().getDataFolder(), "kitTypes.json");

        if (file.exists()) {
            try (Reader schematicsFileReader = Files.newReader(file, Charsets.UTF_8)) {
                Type schematicListType = new TypeToken<List<KitType>>() {}.getType();
                List<KitType> kitTypes = PotPvPRP.getGson().fromJson(schematicsFileReader, schematicListType);

                for (KitType kitType : kitTypes) {
                    KitType.getAllTypes().removeIf(otherKitType -> otherKitType.getId().equals(kitType.getId()));
                    KitType.getAllTypes().add(kitType);
                    kitType.saveAsync();
                }
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "Failed to import.");
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Imported.");
    }

    @Command(name = "export", desc = "Export kitTypes to KitTypes.json")
    @Require("potpvp.kit.admin")
    public void exportKitTypes(CommandSender sender) {
        String json = PotPvPRP.getGson().toJson(KitType.getAllTypes());

        try {
            Files.write(
                    json,
                    new File(PotPvPRP.getInstance().getDataFolder(), "kitTypes.json"),
                    Charsets.UTF_8
            );

            sender.sendMessage(ChatColor.GREEN + "Exported.");
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Failed to export.");
        }
    }

    @Command(name = "wipeKits Type", usage = "<kitType>", desc = "Wipe KitTypes by Type")
    public void kitWipeKitsType(Player sender, KitType kitType) {
        int modified = PotPvPRP.getInstance().getKitHandler().wipeKitsWithType(kitType);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + modified + " " + kitType.getDisplayName() + " kits.");
        sender.sendMessage(ChatColor.GRAY + "^ We would have a proper count here if we ran recent versions of MongoDB");
    }

    @Command(name = "wipeKits Player", usage = "<kitType>", desc = "Wipe KitTypes for a player")
    public void kitWipeKitsPlayer(Player sender, UUID target) {
        PotPvPRP.getInstance().getKitHandler().wipeKitsForPlayer(target);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + PotPvPRP.getInstance().getUuidCache().name(target) + "'s kits.");
    }


    @Override
    public String getCommandName() {
        return "kit";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"kitType"};
    }
}
