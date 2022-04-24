package net.frozenorb.potpvp.lobby;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.match.duel.DuelHandler;
import net.frozenorb.potpvp.profile.follow.FollowHandler;
import net.frozenorb.potpvp.kit.KitItems;
import net.frozenorb.potpvp.kit.menu.editkit.EditKitMenu;
import net.frozenorb.potpvp.events.EventItems;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.PartyItems;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.queue.QueueItems;
import net.frozenorb.potpvp.match.rematch.RematchData;
import net.frozenorb.potpvp.match.rematch.RematchHandler;
import net.frozenorb.potpvp.match.rematch.RematchItems;
import net.frozenorb.potpvp.util.menu.Menu;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class LobbyUtils {

    public static void resetInventory(Player player) {
        // prevents players with the kit editor from having their
        // inventory updated (kit items go into their inventory)
        // also, admins in GM don't get invs updated (to prevent annoying those editing kits)
        if (Menu.getCurrentlyOpenedMenus().get(player.getUniqueId()) instanceof EditKitMenu || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        if (partyHandler.hasParty(player)) {
            renderPartyItems(player, inventory, partyHandler.getParty(player));
        } else {
            renderSoloItems(player, inventory);
        }

        Bukkit.getScheduler().runTaskLater(PotPvPRP.getInstance(), player::updateInventory, 1L);
    }

    private void renderPartyItems(Player player, PlayerInventory inventory, Party party) {
        QueueHandler queueHandler = PotPvPRP.getInstance().getQueueHandler();

        if (party.isLeader(player.getUniqueId())) {
            int partySize = party.getMembers().size();

            if (partySize == 2) {
                if (!queueHandler.isQueuedUnranked(party)) {
                    inventory.setItem(1, QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM);
                    inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
                } else {
                    inventory.setItem(1, QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM);
                }

                if (!queueHandler.isQueuedRanked(party)) {
                    inventory.setItem(2, QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM);
                    inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
                } else {
                    inventory.setItem(2, QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM);
                }
            } else if (partySize > 2 && !queueHandler.isQueued(party)) {
                inventory.setItem(1, PartyItems.START_TEAM_SPLIT_ITEM);
                inventory.setItem(2, PartyItems.START_FFA_ITEM);
                inventory.setItem(3, PartyItems.ASSIGN_CLASSES);
            }

        } else {
            int partySize = party.getMembers().size();
            if (partySize >= 2) {
                inventory.setItem(1, PartyItems.ASSIGN_CLASSES);
            }
        }

        inventory.setItem(0, PartyItems.icon(party));
        inventory.setItem(6, PartyItems.OTHER_PARTIES_ITEM);
        inventory.setItem(7, KitItems.OPEN_EDITOR_ITEM);
        inventory.setItem(8, PartyItems.LEAVE_PARTY_ITEM);
    }

    private void renderSoloItems(Player player, PlayerInventory inventory) {
        RematchHandler rematchHandler = PotPvPRP.getInstance().getRematchHandler();
        QueueHandler queueHandler = PotPvPRP.getInstance().getQueueHandler();
        DuelHandler duelHandler = PotPvPRP.getInstance().getDuelHandler();
        FollowHandler followHandler = PotPvPRP.getInstance().getFollowHandler();
        LobbyHandler lobbyHandler = PotPvPRP.getInstance().getLobbyHandler();

        boolean specMode = lobbyHandler.isInSpectatorMode(player);
        boolean followingSomeone = followHandler.getFollowing(player).isPresent();

        player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE || specMode);

        if (specMode || followingSomeone) {
            inventory.setItem(5, LobbyItems.SPECTATE_MENU_ITEM);
            inventory.setItem(3, LobbyItems.SPECTATE_RANDOM_ITEM);
            inventory.setItem(4, LobbyItems.DISABLE_SPEC_MODE_ITEM);

            if (followingSomeone) {
                inventory.setItem(8, LobbyItems.UNFOLLOW_ITEM);
            }
        } else {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());

                if (target != null) {
                    if (duelHandler.findInvite(player, target) != null) {
                        // if we've sent an invite to them
                        inventory.setItem(2, RematchItems.SENT_REMATCH_ITEM);
                    } else if (duelHandler.findInvite(target, player) != null) {
                        // if they've sent us an invite
                        inventory.setItem(2, RematchItems.ACCEPT_REMATCH_ITEM);
                    } else {
                        // if no one has sent an invite
                        inventory.setItem(2, RematchItems.REQUEST_REMATCH_ITEM);
                    }
                }
            }

            if (queueHandler.isQueuedRanked(player.getUniqueId())) {
                inventory.setItem(0, QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM);
            } else if (queueHandler.isQueuedUnranked(player.getUniqueId())) {
                inventory.setItem(0, QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM);
            } else {
                inventory.setItem(0, QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM);
                inventory.setItem(1, QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM);
                inventory.setItem(4, LobbyItems.ENABLE_SPEC_MODE_ITEM);
                //inventory.setItem(6, EventItems.EVENTS_ITEM);
                //inventory.setItem(7, LobbyItems.PLAYER_STATISTICS);
                inventory.setItem(8, KitItems.OPEN_EDITOR_ITEM);

                ItemStack eventItem = EventItems.getEventItem();

                if (player.hasPermission("potpvp.admin")) {
                    if (eventItem != null) {
                        inventory.setItem(6, eventItem);
                    }
                    inventory.setItem(7, LobbyItems.MANAGE_ITEM);
                } else {
                    if (eventItem != null) {
                        inventory.setItem(7, eventItem);
                    }
                }
            }
        }
    }

}