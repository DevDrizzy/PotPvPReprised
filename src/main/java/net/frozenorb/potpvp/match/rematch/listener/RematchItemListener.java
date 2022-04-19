package net.frozenorb.potpvp.match.rematch.listener;

import net.frozenorb.potpvp.command.impl.duel.AcceptCommand;
import net.frozenorb.potpvp.command.impl.duel.DuelCommand;
import net.frozenorb.potpvp.match.rematch.RematchData;
import net.frozenorb.potpvp.match.rematch.RematchHandler;
import net.frozenorb.potpvp.match.rematch.RematchItems;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.ItemListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class RematchItemListener extends ItemListener {

    public RematchItemListener(RematchHandler rematchHandler) {
        addHandler(RematchItems.REQUEST_REMATCH_ITEM, player -> {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                new DuelCommand().duel(player, target, rematchData.getKitType());

                InventoryUtils.resetInventoryDelayed(player);
                InventoryUtils.resetInventoryDelayed(target);
            }
        });

        addHandler(RematchItems.SENT_REMATCH_ITEM, p -> p.sendMessage(ChatColor.RED + "You have already sent a rematch request."));

        addHandler(RematchItems.ACCEPT_REMATCH_ITEM, player -> {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                new AcceptCommand().accept(player, target);
            }
        });
    }

}