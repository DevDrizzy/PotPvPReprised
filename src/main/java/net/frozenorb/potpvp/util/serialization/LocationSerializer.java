package net.frozenorb.potpvp.util.serialization;

import com.mongodb.BasicDBObject;
import net.frozenorb.potpvp.PotPvPRP;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationSerializer {

    // Static utility class -- cannot be created.
    private LocationSerializer() {
    }

    public static BasicDBObject serialize(Location location) {
        if (location == null) {
            return (new BasicDBObject());
        }

        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("world", location.getWorld().getName());
        dbObject.put("x", location.getX());
        dbObject.put("y", location.getY());
        dbObject.put("z", location.getZ());
        dbObject.append("yaw", location.getYaw());
        dbObject.append("pitch", location.getPitch());

        return (dbObject);
    }

    public static Location deserialize(BasicDBObject dbObject) {
        if (dbObject == null || dbObject.isEmpty()) {
            return (null);
        }

        World world = PotPvPRP.getInstance().getServer().getWorld(dbObject.getString("world"));
        double x = dbObject.getDouble("x");
        double y = dbObject.getDouble("y");
        double z = dbObject.getDouble("z");
        int yaw = dbObject.getInt("yaw");
        int pitch = dbObject.getInt("pitch");

        return (new Location(world, x, y, z, yaw, pitch));
    }

}