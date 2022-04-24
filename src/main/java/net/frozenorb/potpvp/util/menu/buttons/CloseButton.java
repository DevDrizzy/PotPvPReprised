package net.frozenorb.potpvp.util.menu.buttons;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class CloseButton extends Button {

    private String name;
    private List<String> lore;

    @Override
    public Material getMaterial(Player player) {
        return Material.REDSTONE_BLOCK;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public String getName(Player player) {
        return this.name == null ? "§cClose Menu":this.name;
    }

    @Override
    public List<String> getDescription(Player player) {
        return this.lore == null ? new ArrayList<>():this.lore;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        player.closeInventory();
    }

}
