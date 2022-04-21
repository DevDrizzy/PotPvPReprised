package net.frozenorb.potpvp.kt.nametag

import net.frozenorb.potpvp.util.packet.ScoreboardTeamPacketMod
import java.util.ArrayList

class NametagInfo constructor(val name: String, val prefix: String, val suffix: String) {

    val teamAddPacket = ScoreboardTeamPacketMod(name, prefix, suffix, ArrayList(), 0)

    override fun equals(other: Any?): Boolean {
        if (other is NametagInfo) {
            val otherNametag = other as NametagInfo?
            return this.name == otherNametag!!.name && this.prefix == otherNametag.prefix && this.suffix == otherNametag.suffix
        }
        return false
    }

}
