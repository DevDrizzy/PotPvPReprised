package net.frozenorb.potpvp.kittype;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

// this adapter makes gson pretend that KitType is an enum (and just writes the id)
// instead of all fields
public final class KitTypeJsonAdapter extends TypeAdapter<KitType> {

    @Override
    public void write(JsonWriter writer, KitType type) throws IOException {
        writer.value(type.getId());
    }

    @Override
    public KitType read(JsonReader reader) throws IOException {
        return KitType.byId(reader.nextString());
    }

}