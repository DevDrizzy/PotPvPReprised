package net.frozenorb.potpvp.kt.nametag

import com.google.common.primitives.Ints
import net.frozenorb.potpvp.PotPvPRP
import net.frozenorb.potpvp.util.packet.ScoreboardTeamPacketMod
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

class NameTagHandler {

    val teamMap = ConcurrentHashMap<String, HashMap<String, NametagInfo>>()
    private val registeredTeams = Collections.synchronizedList(ArrayList<NametagInfo>())
    private var teamCreateIndex = 1
    private var providers = ArrayList<NametagProvider>()
    var isNametagRestrictionEnabled: Boolean = false
    var nametagRestrictBypass: String = ""
    var isAsync: Boolean = true
    var updateInterval: Int = 2

    fun load() {
        isNametagRestrictionEnabled = PotPvPRP.getInstance().config.getBoolean("NametagPacketRestriction.Enabled", false)
        nametagRestrictBypass = PotPvPRP.getInstance().config.getString("NametagPacketRestriction.BypassPrefix", "").replace("&", "§")

        NametagThread().start()
        PotPvPRP.getInstance().server.pluginManager.registerEvents(NametagListener(), PotPvPRP.getInstance())
        registerProvider(NametagProvider.DefaultNametagProvider())
    }

    fun registerProvider(newProvider: NametagProvider) {
        providers.add(newProvider)
        providers.sortWith(Comparator { a, b -> Ints.compare(b.weight, a.weight) })
    }

    fun reloadPlayer(toRefresh: Player) {
        val update = NametagUpdate(toRefresh)

        if (isAsync) {
            NametagThread.pendingUpdates[update] = true
        } else {
            applyUpdate(update)
        }
    }

    fun reloadOthersFor(refreshFor: Player) {
        for (toRefresh in PotPvPRP.getInstance().server.onlinePlayers) {
            if (refreshFor === toRefresh) {
                continue
            }
            reloadPlayer(toRefresh, refreshFor)
        }
    }

    fun reloadPlayer(toRefresh: Player, refreshFor: Player) {
        val update = NametagUpdate(toRefresh, refreshFor)

        if (isAsync) {
            NametagThread.pendingUpdates[update] = true
        } else {
            applyUpdate(update)
        }
    }

    internal fun applyUpdate(nametagUpdate: NametagUpdate) {
        val toRefreshPlayer = PotPvPRP.getInstance().server.getPlayerExact(nametagUpdate.toRefresh)?: return

        if (nametagUpdate.refreshFor == null) {
            for (refreshFor in PotPvPRP.getInstance().server.onlinePlayers) {
                reloadPlayerInternal(toRefreshPlayer, refreshFor)
            }
        } else {
            val refreshForPlayer = PotPvPRP.getInstance().server.getPlayerExact(nametagUpdate.refreshFor)
            if (refreshForPlayer != null) {
                reloadPlayerInternal(toRefreshPlayer, refreshForPlayer)
            }
        }
    }

    internal fun reloadPlayerInternal(toRefresh: Player, refreshFor: Player) {
        if (!refreshFor.hasMetadata("potpvp-LoggedIn")) {
            return
        }

        var provided: NametagInfo? = null
        var providerIndex = 0

        while (provided == null) {
            provided = providers[providerIndex++].fetchNametag(toRefresh, refreshFor)
        }

        if ((refreshFor as CraftPlayer).handle.playerConnection.networkManager.protocolVersion > 5 && isNametagRestrictionEnabled) {
            val prefix = provided.prefix
            if (!prefix.equals(nametagRestrictBypass, ignoreCase = true)) {
                return
            }
        }

        var teamInfoMap = HashMap<String, NametagInfo>()
        if (teamMap.containsKey(refreshFor.getName())) {
            teamInfoMap = teamMap[refreshFor.getName()]!!
        }

        ScoreboardTeamPacketMod(provided.name, Arrays.asList(toRefresh.name), 3).sendToPlayer(refreshFor)
        teamInfoMap[toRefresh.name] = provided
        teamMap[refreshFor.getName()] = teamInfoMap
    }

    internal fun initiatePlayer(player: Player) {
        for (teamInfo in registeredTeams) {
            teamInfo.teamAddPacket.sendToPlayer(player)
        }
    }

    internal fun getOrCreate(prefix: String, suffix: String): NametagInfo {
        for (teamInfo in registeredTeams) {
            if (teamInfo.prefix == prefix && teamInfo.suffix == suffix) {
                return teamInfo
            }
        }

        val newTeam = NametagInfo(teamCreateIndex++.toString(), prefix, suffix)
        registeredTeams.add(newTeam)

        val addPacket = newTeam.teamAddPacket
        for (player in PotPvPRP.getInstance().server.onlinePlayers) {
            addPacket.sendToPlayer(player)
        }

        return newTeam
    }
}