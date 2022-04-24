package net.frozenorb.potpvp.util.menu.buttons;

import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BackButton extends Button {

    private final Menu back;

    @Override
    public Material getMaterial(Player player) {
        return Material.REDSTONE;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public String getName(Player player) {
        return "§cGo back";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        Button.playNeutral(player);
        Menu.getCurrentlyOpenedMenus().get(player.getUniqueId()).onClose(player);
        this.back.openMenu(player);
    }

}
