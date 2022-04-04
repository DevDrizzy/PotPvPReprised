package net.frozenorb.potpvp.kt.protocol.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ServerLaggedOutEvent(private val averagePing: Int) : Event(true) {

    private val instanceHandlers: HandlerList = handlerList

    override fun getHandlers(): HandlerList {
        return instanceHandlers
    }

    companion object {
        val handlerList: HandlerList = HandlerList()
    }

}
