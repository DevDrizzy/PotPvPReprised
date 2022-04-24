package net.frozenorb.potpvp.util.menu.menus;

import net.frozenorb.potpvp.util.Callback;
import net.frozenorb.potpvp.util.menu.Menu;
import net.frozenorb.potpvp.util.menu.buttons.BooleanButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ConfirmMenu extends Menu {

    private String title;
    @Getter private final Callback<Boolean> response;

    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for(int i = 0; i < 9; ++i) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, this.response));
            } else if (i == 5) {
                buttons.put(i, new BooleanButton(false, this.response));
            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, " "));
            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return this.title;
    }

}
