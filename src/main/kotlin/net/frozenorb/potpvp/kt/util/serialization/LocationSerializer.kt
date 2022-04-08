package net.frozenorb.potpvp.kt.util.serialization

import com.mongodb.BasicDBObject
import net.frozenorb.potpvp.PotPvPRP
import org.bukkit.Location

object LocationSerializer {

    @JvmStatic
    fun serialize(location: Location?): BasicDBObject {
        if (location == null) {
            return BasicDBObject()
        }

        val dbObject = BasicDBObject()
        dbObject["world"] = location.world.name
        dbObject["x"] = location.x
        dbObject["y"] = location.y
        dbObject["z"] = location.z
        dbObject.append("yaw", location.yaw)
        dbObject.append("pitch", location.pitch)
        return dbObject
    }

    @JvmStatic
    fun deserialize(dbObject: BasicDBObject?): Location? {
        if (dbObject == null || dbObject.isEmpty()) {
            return null
        }

        val world = PotPvPRP.getInstance().server.getWorld(dbObject.getString("world"))
        val x = dbObject.getDouble("x")
        val y = dbObject.getDouble("y")
        val z = dbObject.getDouble("z")
        val yaw = dbObject.getInt("yaw")
        val pitch = dbObject.getInt("pitch")
        return Location(world, x, y, z, yaw.toFloat(), pitch.toFloat())
    }

}