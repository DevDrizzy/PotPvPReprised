package net.frozenorb.potpvp.packet;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class PlayerInfoPacketMod {

    private PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

    public PlayerInfoPacketMod(String name, int ping, GameProfile profile, int action) {
        this.setField("username", name);
        this.setField("ping", ping);
        this.setField("action", action);
        this.setField("player", profile);
    }

    public void setField(String field, Object value) {

        try {

            final Field fieldObject = this.packet.getClass().getDeclaredField(field);

            fieldObject.setAccessible(true);
            fieldObject.set(this.packet, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void sendToPlayer(Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(this.packet);
    }


}
