package net.frozenorb.potpvp.packet;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public final class ScoreboardTeamPacketMod {

    private final PacketPlayOutScoreboardTeam packet;

    private final static String fieldPrefix = "c";
    private final static String fieldSuffix = "d";
    private final static String fieldPlayers = "g";
    private final static String fieldTeamName = "a";
    private final static String fieldParamInt = "f";
    private final static String fieldPackOption = "i";
    private final static String fieldDisplayName = "b";

    public ScoreboardTeamPacketMod(String name, String prefix, String suffix, Collection<String> players,int paramInt) {

        this.packet = new PacketPlayOutScoreboardTeam();

        this.setField(fieldTeamName, name);
        this.setField(fieldParamInt, paramInt);

        if (paramInt == 0 || paramInt == 2) {
            this.setField(fieldDisplayName, name);
            this.setField(fieldPrefix, prefix);
            this.setField(fieldSuffix, suffix);
            this.setField(fieldPackOption,1);
        }

        if (paramInt == 0) {
            this.addAll(players);
        }

    }

    public ScoreboardTeamPacketMod(String name, Collection<String> players, int paramInt) {

        this.packet = new PacketPlayOutScoreboardTeam();

        if (players == null) {
            players = new ArrayList<>();
        }

        this.setField(fieldTeamName, name);
        this.setField(fieldParamInt, paramInt);

        this.addAll(players);
    }

    public void sendToPlayer(Player bukkitPlayer) {
        ((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(this.packet);
    }

    public void setField(String field, Object value) {

        try {

            final Field fieldObject = this.packet.getClass().getDeclaredField(field);

            fieldObject.setAccessible(true);
            fieldObject.set(this.packet, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addAll(Collection col) {

        try {

            final Field fieldObject = this.packet.getClass().getDeclaredField(fieldPlayers);

            fieldObject.setAccessible(true);
            ((Collection) fieldObject.get(this.packet)).addAll(col);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}