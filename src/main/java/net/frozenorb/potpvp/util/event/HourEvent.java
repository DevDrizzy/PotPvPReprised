package net.frozenorb.potpvp.util.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HourEvent extends Event {

    public HourEvent(int hour) {
        this.hour = hour;
    }

    private int hour;
    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public int getHour() {
        return this.hour;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}
