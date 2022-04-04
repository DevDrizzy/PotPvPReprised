package net.frozenorb.potpvp.nametag;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.packet.ScoreboardTeamPacketMod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.frozenorb.potpvp.nametag.construct.NameTagComparator;
import net.frozenorb.potpvp.nametag.construct.NameTagInfo;
import net.frozenorb.potpvp.nametag.construct.NametagUpdate;
import net.frozenorb.potpvp.nametag.listener.NameTagListener;
import net.frozenorb.potpvp.nametag.provider.NameTagProvider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class NameTagHandler {

    private final PotPvPSI plugin;

    private final Map<String, Map<String, NameTagInfo>> teamMap = new ConcurrentHashMap<>();
    private final List<NameTagProvider> providers = new ArrayList<>();
    private final List<NameTagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());

    private NametagThread thread;

    private boolean initiated = false;
    private boolean async = true;
    private static int teamCreateIndex = 1;

    public NameTagHandler(PotPvPSI plugin) {
        this.plugin = plugin;
        this.initiated = true;

        this.thread = new NametagThread();
        this.thread.start();

        this.plugin.getServer().getPluginManager().registerEvents(new NameTagListener(), this.plugin);
    }

    public void registerAdapter(NameTagProvider newProvider) {
        this.providers.add(newProvider);
        this.providers.sort(new NameTagComparator());
    }

    public void reloadPlayer(Player toRefresh) {
        NametagUpdate update = new NametagUpdate(toRefresh);

        if (async) {
            thread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }
    }

    public void reloadOthersFor(Player refreshFor) {
        this.plugin.getServer().getOnlinePlayers().forEach(toRefresh -> {
            if (refreshFor != toRefresh) {
                reloadPlayer(toRefresh, refreshFor);
            }
        });
    }

    public void reloadPlayer(Player toRefresh, Player refreshFor) {
        NametagUpdate update = new NametagUpdate(toRefresh, refreshFor);

        if (async) {
            thread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }
    }

    public void applyUpdate(NametagUpdate nametagUpdate) {
        if (nametagUpdate.getToRefresh() != null){
            Player toRefreshPlayer = Bukkit.getPlayerExact(nametagUpdate.getToRefresh());

            if (toRefreshPlayer == null) return;

            if (nametagUpdate.getRefreshFor() == null) {
                Bukkit.getOnlinePlayers().forEach(refreshFor -> reloadPlayerInternal(toRefreshPlayer, refreshFor));
            } else {
                Player refreshForPlayer = Bukkit.getPlayerExact(nametagUpdate.getRefreshFor());

                if(refreshForPlayer != null) {
                    reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
                }
            }
        }
    }

    public void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if (!refreshFor.hasMetadata("Test-LoggedIn")) return;

        NameTagInfo provided = null;

        for ( NameTagProvider nametagProvider : providers ) {
            provided =  nametagProvider.fetchNameTag(toRefresh, refreshFor);
            if (provided != null){
                break;
            }
        }

        if (provided == null) return;

        Map<String, NameTagInfo> teamInfoMap = new HashMap<>();
        
        if (teamMap.containsKey(refreshFor.getName())) {
            teamInfoMap = teamMap.get(refreshFor.getName());
        }

        new ScoreboardTeamPacketMod(provided.getName(), Collections.singletonList(toRefresh.getName()), 3).sendToPlayer(refreshFor);
        teamInfoMap.put(toRefresh.getName(), provided);
        teamMap.put(refreshFor.getName(), teamInfoMap);        
    }

    public void initiatePlayer(Player player) {
        registeredTeams.forEach(teamInfo -> teamInfo.getTeamAddPacket().sendToPlayer(player));
    }

    public NameTagInfo getOrCreate(String prefix, String suffix) {
        for( NameTagInfo teamInfo : registeredTeams ) {
            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return (teamInfo);
            }
        }

        NameTagInfo newTeam = new NameTagInfo(String.valueOf(teamCreateIndex++), prefix, suffix);
        registeredTeams.add(newTeam);

        ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();
        this.plugin.getServer().getOnlinePlayers().forEach(addPacket::sendToPlayer);

        return (newTeam);
    }
}