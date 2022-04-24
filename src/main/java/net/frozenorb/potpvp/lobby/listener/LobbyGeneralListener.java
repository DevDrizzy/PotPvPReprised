package net.frozenorb.potpvp.lobby.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.lobby.listener.LobbyParkourListener.Parkour;
import net.frozenorb.potpvp.util.menu.Menu;

public final class LobbyGeneralListener implements Listener {

    private final LobbyHandler lobbyHandler;

    public LobbyGeneralListener(LobbyHandler lobbyHandler) {
        this.lobbyHandler = lobbyHandler;
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        Parkour parkour = LobbyParkourListener.getParkourMap().get(event.getPlayer().getUniqueId());
        if (parkour != null && parkour.getCheckpoint() != null) {
            event.setSpawnLocation(parkour.getCheckpoint().getLocation());
            return;
        }

        event.setSpawnLocation(lobbyHandler.getLobbyLocation());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        lobbyHandler.returnToLobby(event.getPlayer());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (lobbyHandler.isInLobby(player)) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                lobbyHandler.returnToLobby(player);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (lobbyHandler.isInLobby((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (lobbyHandler.isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!lobbyHandler.isInLobby(player)) {
            return;
        }

        Menu openMenu = Menu.getCurrentlyOpenedMenus().get(player.getUniqueId());

        // just remove the item for players in these menus, so they can 'drop' items to remove them
        // same thing for admins in build mode, just pretend to drop the item
        if (player.hasMetadata("Build") || (openMenu != null && openMenu.isNoncancellingInventory())) {
            event.getItemDrop().remove();
        } else {
            event.setCancelled(true);
        }
    }

    // cancel inventory interaction in the lobby except for menus
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player clicked = (Player) event.getWhoClicked();

        if (!lobbyHandler.isInLobby(clicked) || clicked.hasMetadata("Build") || Menu.getCurrentlyOpenedMenus().containsKey(clicked.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player clicked = (Player) event.getWhoClicked();

        if (!lobbyHandler.isInLobby(clicked) || clicked.hasMetadata("Build") || Menu.getCurrentlyOpenedMenus().containsKey(clicked.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (lobbyHandler.isInLobby(event.getEntity())) {
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        InventoryHolder inventoryHolder = event.getSource().getHolder();

        if (inventoryHolder instanceof Player) {
            Player player = (Player) inventoryHolder;

            if (!lobbyHandler.isInLobby(player) || Menu.getCurrentlyOpenedMenus().containsKey(player.getUniqueId())) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        GameMode gameMode = event.getPlayer().getGameMode();

        if (lobbyHandler.isInLobby(event.getPlayer()) && gameMode != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        if (lobbyHandler.isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

}