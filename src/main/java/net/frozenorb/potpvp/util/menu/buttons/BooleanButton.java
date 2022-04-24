package net.frozenorb.potpvp.util.menu.buttons;

import net.frozenorb.potpvp.util.Callback;
import net.frozenorb.potpvp.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BooleanButton extends Button {

    private boolean confirm;
    private Callback<Boolean> callback;

    public void clicked(Player player, int i, ClickType clickType) {

        if (this.confirm) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0F, 0.1F);
        } else {
            player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20.0F, 0.1F);
        }

        player.closeInventory();

        this.callback.callback(this.confirm);
    }

    @Override
    public String getName(Player player) {
        return this.confirm ? "§aConfirm" : "§cCancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte)(this.confirm ? 5 : 14);
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }}
