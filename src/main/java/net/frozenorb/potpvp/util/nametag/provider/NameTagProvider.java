package net.frozenorb.potpvp.util.nametag.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.potpvp.PotPvPRP;
import org.bukkit.entity.Player;
import net.frozenorb.potpvp.util.nametag.construct.NameTagInfo;
import xyz.refinedev.spigot.utils.CC;

@Getter
@AllArgsConstructor
public abstract class NameTagProvider {

    private final PotPvPRP plugin = PotPvPRP.getInstance();

    private final String name;
    private final int weight;

    public abstract NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor);

    public NameTagInfo createNameTag(String prefix, String suffix) {
        return (plugin.getNameTagHandler().getOrCreate(CC.translate(prefix), CC.translate(suffix)));
    }
}