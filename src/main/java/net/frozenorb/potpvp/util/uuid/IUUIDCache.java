package net.frozenorb.potpvp.util.uuid;

import java.util.UUID;

public interface IUUIDCache {

    UUID uuid(String name);

    String name(UUID uuid);

    boolean cached(UUID uuid);

    boolean cached(String name);

    void ensure(UUID uuid);

    void update(UUID uuid,String name);

    void updateAll(UUID uuid,String name);

}