package net.frozenorb.potpvp.arena;

import com.google.common.base.Preconditions;

import com.qrakn.morpheus.game.event.GameEvent;
import com.sk89q.worldedit.Vector;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents an arena schematic. See {@link net.frozenorb.potpvp.arena}
 * for a comparision of {@link Arena}s and {@link ArenaSchematic}s.
 */
public final class ArenaSchematic {

    /**
     * Name of this schematic (ex "Candyland")
     */
    @Getter private String name;

    /**
     * If matches can be scheduled on an instance of this arena.
     * Only impacts match scheduling, admin commands are (ignoring visual differences) nonchanged
     */
    @Setter private boolean enabled = false;

    /**
     * Maximum number of players that can occupy an instance of this arena.
     * Some small schematics should only be used for smaller fights
     */
    @Getter @Setter private int maxPlayerCount = 256;

    /**
     * Minimum number of players that can occupy an instance of this arena.
     * Some large schematics should only be used for larger fights
     */
    @Getter @Setter private int minPlayerCount = 2;

    /**
     * If this schematic can be used for ranked matches
     * Some "joke" schematics cannot be used for ranked (due to their nature)
     */
    @Getter @Setter private boolean supportsRanked = false;

    /**
     * If this schematic can be only be used for archer matches
     * Some schematics are built for specifically archer fights
     */
    @Getter @Setter private boolean archerOnly = false;

    /**
     * If this schematic can be only be used for archer matches
     * Some schematics are built for specifically archer fights
     */
    @Getter @Setter private boolean teamFightsOnly = false;


    /**
     * If this schematic can be only be used for Sumo matches
     * Some schematics are built for specifically Sumo fights
     */
    @Getter @Setter private boolean sumoOnly = false;

    /**
     * If this schematic can be only be used for Spleef matches
     * Some schematics are built for specifically Spleef fights
     */
    @Getter @Setter private boolean spleefOnly = false;

    /**
     * If this schematic can be only be used for BuildUHC matches
     * Some schematics are built for specifically BuildUHC fights
     */
    @Getter @Setter private boolean buildUHCOnly = false;

    @Getter @Setter private boolean HCFOnly = false;

    @Getter @Setter private String eventName = null;

    /**
     * Index on the X axis on the grid (and in calculations regarding model arenas)
     * @see ArenaGrid
     */
    @Getter @Setter private int gridIndex;

    public ArenaSchematic() {} // for gson

    public ArenaSchematic(String name) {
        this.name = Preconditions.checkNotNull(name, "name");
    }

    public File getSchematicFile() {
        return new File(ArenaHandler.WORLD_EDIT_SCHEMATICS_FOLDER, name + ".schematic");
    }

    public Vector getModelArenaLocation() {
        int xModifier = ArenaGrid.GRID_SPACING_X * gridIndex;

        return new Vector(
            ArenaGrid.STARTING_POINT.getBlockX() - xModifier,
            ArenaGrid.STARTING_POINT.getBlockY(),
            ArenaGrid.STARTING_POINT.getBlockZ()
        );
    }

    public void pasteModelArena() throws Exception {
        Vector start = getModelArenaLocation();
        WorldEditUtils.paste(this, start);
    }

    public void removeModelArena() throws Exception {
        Vector start = getModelArenaLocation();
        Vector size = WorldEditUtils.readSchematicSize(this);

        WorldEditUtils.clear(
            start,
            start.add(size)
        );
    }

    public GameEvent getEvent() {
        if (eventName != null) {
            for (GameEvent event : GameEvent.getEvents()) {
                if (event.getName().equalsIgnoreCase(eventName)) {
                    return event;
                }
            }

            eventName = null;
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ArenaSchematic && ((ArenaSchematic) o).name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean isEnabled() {
        return enabled;
    }
}