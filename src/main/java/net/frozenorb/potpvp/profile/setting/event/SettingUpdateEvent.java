package net.frozenorb.potpvp.profile.setting.event;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.profile.setting.Setting;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;

/**
 * Called when a player updates a setting value.
 */
public final class SettingUpdateEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    /**
     * The setting that was updated
     */
    @Getter private final Setting setting;

    /**
     * The new state of the setting
     */
    @Getter private final boolean enabled;

    public SettingUpdateEvent(Player player, Setting setting, boolean enabled) {
        super(player);

        this.setting = Preconditions.checkNotNull(setting, "setting");
        this.enabled = enabled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}