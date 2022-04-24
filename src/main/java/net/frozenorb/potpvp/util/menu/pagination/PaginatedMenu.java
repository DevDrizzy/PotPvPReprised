package net.frozenorb.potpvp.util.menu.pagination;

import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class PaginatedMenu extends Menu {

    @Getter private int page = 1;

    @Override
    public String getTitle(Player player) {
        return getPrePaginatedTitle(player) + " - " + page + "/" + getPages(player);
    }

    /**
     * Changes the page number
     *
     * @param player player viewing the inventory
     * @param mod    delta to modify the page number by
     */
    public final void modPage(Player player, int mod) {
        this.page += mod;

        this.getButtons().clear();
        this.openMenu(player);
    }

    /**
     * @param player player viewing the inventory
     * @return
     */
    public final int getPages(Player player) {

        final int buttonAmount = this.getAllPagesButtons(player).size();

        return buttonAmount == 0 ? 1 : (int)Math.ceil((double)buttonAmount / (double)this.getMaxItemsPerPage(player));
    }

    @Override
    public final Map<Integer, Button> getButtons(Player player) {

        final int minIndex = (int) ((double) (page - 1) * getMaxItemsPerPage(player));
        final int maxIndex = (int) ((double) (page) * getMaxItemsPerPage(player));

        final HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));

        for (Map.Entry<Integer, Button> entry : getAllPagesButtons(player).entrySet()) {

            int ind = entry.getKey();

            if (ind >= minIndex && ind < maxIndex) {

                ind -= (int) ((double) (getMaxItemsPerPage(player)) * (page - 1)) - 9;
                buttons.put(ind, entry.getValue());
            }

        }

        final Map<Integer, Button> global = getGlobalButtons(player);

        if (global != null) {

            for (Map.Entry<Integer, Button> gent : global.entrySet()) {
                buttons.put(gent.getKey(), gent.getValue());
            }

        }


        return buttons;
    }

    public int getMaxItemsPerPage(Player player) {
        return 18;
    }

    /**
     * @param player player viewing the inventory
     * @return a Map of buttons that returns items which will be present on all pages
     */
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    /**
     * @param player player viewing the inventory
     * @return title of the inventory before the page number is added
     */
    public abstract String getPrePaginatedTitle(Player player);

    /**
     * @param player player viewing the inventory
     * @return a map of buttons that will be paginated and spread across pages
     */
    public abstract Map<Integer, Button> getAllPagesButtons(Player player);
}
