package net.frozenorb.potpvp.util.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class AbstractConfigurationFile {

    public static final String FILE_EXTENSION = ".yml";
    private final JavaPlugin plugin;
    private final String name;

    public AbstractConfigurationFile(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public abstract String getString(String var1);

    public abstract String getStringOrDefault(String var1, String var2);

    public abstract int getInteger(String var1);

    public abstract double getDouble(String var1);

    public abstract Object get(String var1);

    public abstract List<String> getStringList(String var1);

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public String getName() {
        return this.name;
    }
}
