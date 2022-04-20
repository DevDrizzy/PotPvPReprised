package net.frozenorb.potpvp.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import xyz.refinedev.spigot.chunk.ChunkSnapshot;

import java.io.IOException;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/20/2022
 * Project: potpvp-reprised
 */

public class ChunkSnapshotAdapter extends TypeAdapter<ChunkSnapshot> {

    @Override
    public ChunkSnapshot read(JsonReader arg0) {
        return null;
    }

    @Override
    public void write(JsonWriter arg0, ChunkSnapshot arg1) throws IOException {

    }
}
