package net.frozenorb.potpvp.util.menu.pagination;

import net.frozenorb.potpvp.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PageButton extends Button {
    private int mod;
    private PaginatedMenu menu;

    @Override
    public void clicked(Player player,int i,ClickType clickType) {

        if (clickType == ClickType.RIGHT) {
            (new ViewAllPagesMenu(this.menu)).openMenu(player);
            playNeutral(player);
        } else if (this.hasNext(player)) {
            this.menu.modPage(player, this.mod);
            Button.playNeutral(player);
        } else {
            Button.playFail(player);
        }

    }

    private boolean hasNext(Player player) {

        final int pg = this.menu.getPage() + this.mod;

        return pg > 0 && this.menu.getPages(player) >= pg;
    }

    @Override
    public String getName(Player player) {

        if (!this.hasNext(player)) {
            return this.mod > 0 ? "§7Last page" : "§7First page";
        } else {
            return this.mod > 0 ? "§a⟶" : "§c⟵";
        }

    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte)(this.hasNext(player) ? 11 : 7);
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.CARPET;
    }
}
