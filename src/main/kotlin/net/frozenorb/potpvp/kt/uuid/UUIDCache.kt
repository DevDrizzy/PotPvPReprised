package net.frozenorb.potpvp.kt.uuid

import net.frozenorb.potpvp.PotPvPRP
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.ArrayList
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class UUIDCache : Listener {

    internal var uuidToName = ConcurrentHashMap<UUID, String>()
    private var nameToUuid = ConcurrentHashMap<String, UUID>()

    fun load() {
        val newUuidToName = ConcurrentHashMap<UUID, String>()
        val newNameToUuid = ConcurrentHashMap<String, UUID>()

        val cache = fetchCache()

        cache.forEach { (uuid, name) ->
            newUuidToName[uuid] = name
            newNameToUuid[name.toLowerCase()] = uuid
        }

        uuidToName = newUuidToName
        nameToUuid = newNameToUuid
    }

    fun uuid(name: String): UUID? {
        return nameToUuid[name.toLowerCase()]
    }

    fun name(uuid: UUID): String {
        return uuidToName[uuid]?: "Unknown"
    }

    fun update(uuid: UUID, name: String) {
        val toRemove = ArrayList<String>()

        nameToUuid.forEach { (key, value) ->
            if (value === uuid) {
                toRemove.add(key)
            }
        }

        for (remove in toRemove) {
            nameToUuid.remove(remove)
        }

        uuidToName[uuid] = name
        nameToUuid[name.toLowerCase()] = uuid

        updateCache(uuid, name)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PotPvPRP.getInstance().server.scheduler.runTaskAsynchronously(
            PotPvPRP.getInstance()) {
            update(event.player.uniqueId, event.player.name)
        }
    }

    /**
     * Fetches the cache from this implementation's cache storage.
     */
    abstract fun fetchCache(): Map<UUID, String>

    /**
     * Writes an update to this implementation's cache storage.
     * This function should always be called asynchronously.
     */
    abstract fun updateCache(uuid: UUID, name: String)

}