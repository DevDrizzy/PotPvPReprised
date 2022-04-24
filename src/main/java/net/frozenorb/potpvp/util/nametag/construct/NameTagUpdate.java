package net.frozenorb.potpvp.util.nametag.construct;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class NameTagUpdate {

    private String toRefresh;
    private String refreshFor;

    public NameTagUpdate(Player toRefresh) {
        if(toRefresh == null) return;

        this.toRefresh = toRefresh.getName();
    }

    public NameTagUpdate(Player toRefresh, Player refreshFor) {
        this.toRefresh = toRefresh.getName();
        this.refreshFor = refreshFor.getName();
    }
}