package net.frozenorb.potpvp.kittype.menu.select;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.util.Callback;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class SelectKitTypeMenu extends Menu {

    private final boolean reset;
    private final Callback<KitType> callback;

    public SelectKitTypeMenu(Callback<KitType> callback, String title) {
        this(callback, true, title);
    }

    public SelectKitTypeMenu(Callback<KitType> callback, boolean reset, String title) {
        super(ChatColor.BLUE.toString() + ChatColor.BOLD + title);

        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.reset = reset;
    }

    @Override
    public void onClose(Player player, boolean manualClose) {
        if (reset) {
            InventoryUtils.resetInventoryDelayed(player);
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (KitType kitType : KitType.getAllTypes()) {
            if (!player.isOp() && kitType.isHidden()) {
                continue;
            }

            buttons.put(index++, new KitTypeButton(kitType, callback));
        }

        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(player);
        if (party != null) {
            buttons.put(8, new KitTypeButton(KitType.teamFight, callback));
        }

        return buttons;
    }

}