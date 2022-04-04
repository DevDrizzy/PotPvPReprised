package net.frozenorb.potpvp.util.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HalfHourEvent extends Event {

    public HalfHourEvent(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    private int hour;
    private int minute;
    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}
