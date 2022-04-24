package net.frozenorb.potpvp.profile.setting.menu;

import net.frozenorb.potpvp.profile.setting.Setting;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Menu used by /settings to let players toggle settings
 */
public final class SettingsMenu extends Menu {

    public SettingsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Edit settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (Setting setting : Setting.values()) {
            if (setting.canUpdate(player)) {
                buttons.put(index++, new SettingButton(setting));
            }
        }

        return buttons;
    }

}