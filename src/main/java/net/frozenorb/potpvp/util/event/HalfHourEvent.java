package net.frozenorb.potpvp.util.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HalfHourEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}
