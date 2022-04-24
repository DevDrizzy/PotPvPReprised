package net.frozenorb.potpvp.party.menu.oddmanout;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import net.frozenorb.potpvp.util.Callback;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class OddManOutMenu extends Menu {

    private final Callback<Boolean> callback;

    public OddManOutMenu(Callback<Boolean> callback) {
        this.callback = Preconditions.checkNotNull(callback, "callback");
    }

    @Override
    public String getTitle(Player player) {
        return "Continue with unbalanced teams?";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(getSlot(2, 0), new OddManOutButton(true, callback));
        buttons.put(getSlot(6, 0), new OddManOutButton(false, callback));

        return buttons;
    }

}