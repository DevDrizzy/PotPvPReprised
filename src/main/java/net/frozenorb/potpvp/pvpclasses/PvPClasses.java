package net.frozenorb.potpvp.pvpclasses;

import lombok.Getter;
import net.frozenorb.potpvp.party.Party;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;

public enum PvPClasses {
    DIAMOND(Material.DIAMOND_CHESTPLATE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
    BARD(Material.GOLD_CHESTPLATE, 1, 2, 2),
    ARCHER(Material.LEATHER_CHESTPLATE, 1, 2, 2);

    @Getter private final Material icon;
    @Getter private final int maxForFive;
    @Getter private final int maxForTen;
    @Getter private final int maxForTwenty;

    PvPClasses(Material icon, int maxForFive, int maxForTen, int maxForTwenty) {
        this.icon = icon;
        this.maxForFive = maxForFive;
        this.maxForTen = maxForTen;
        this.maxForTwenty = maxForTwenty;
    }

    public boolean allowed(Party party) {
        int current = (int) party.getKits().values().stream().filter(pvPClasses -> pvPClasses == this).count();
        int size = party.getMembers().size();

        if (size < 10 && current >= maxForFive) {
            return false;
        }

        if (size < 20 && current >= maxForTen) {
            return false;
        }

        return current < maxForTwenty;
    }

    public String getName() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }

}
