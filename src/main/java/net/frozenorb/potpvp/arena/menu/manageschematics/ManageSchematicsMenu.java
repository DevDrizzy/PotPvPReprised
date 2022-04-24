package net.frozenorb.potpvp.arena.menu.manageschematics;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.arena.menu.manage.ManageMenu;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;

import net.frozenorb.potpvp.util.menu.buttons.BackButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class ManageSchematicsMenu extends Menu {

    public ManageSchematicsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Manage schematics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        ArenaHandler arenaHandler = PotPvPRP.getInstance().getArenaHandler();
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        buttons.put(index++, new BackButton(new ManageMenu()));

        for (ArenaSchematic schematic : arenaHandler.getSchematics()) {
            buttons.put(index++, new ManageSchematicButton(schematic));
        }

        return buttons;
    }

}