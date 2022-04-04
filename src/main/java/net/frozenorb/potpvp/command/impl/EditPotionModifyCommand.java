package net.frozenorb.potpvp.command.impl;

import net.frozenorb.potpvp.command.PotPvPCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.refinedev.api.annotation.Command;

public class EditPotionModifyCommand implements PotPvPCommand {

    @Command(name = "modify", usage = "<effect> <seconds> <amplifier>", desc = "Edit a potion's effects")
    public void editPotionModify(Player sender, String effect, int seconds, int amplifier) {
        PotionEffectType effectType = PotionEffectType.getByName(effect.toUpperCase());
        ItemStack hand = sender.getItemInHand();

        if (hand == null || hand.getType() != Material.POTION) {
            sender.sendMessage(ChatColor.RED + "Please hold a potion.");
            return;
        }

        if (effectType == null) {
            sender.sendMessage(ChatColor.RED + "Could not parse " + effect);
            return;
        }

        PotionMeta meta = (PotionMeta) hand.getItemMeta();
        meta.addCustomEffect(new PotionEffect(effectType, seconds * 20, amplifier), true);
        hand.setItemMeta(meta);

        sender.sendMessage(ChatColor.RED + "Modified effect " + effectType.getName() + ": Level " + amplifier + " for " + seconds + " seconds.");
        sender.updateInventory();
    }

    @Override
    public String getCommandName() {
        return "editpotion";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}