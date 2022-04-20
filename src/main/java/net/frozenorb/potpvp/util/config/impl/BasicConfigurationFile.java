package net.frozenorb.potpvp.util.config.impl;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import net.frozenorb.potpvp.util.config.AbstractConfigurationFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BasicConfigurationFile extends AbstractConfigurationFile {

    private final File file;
    private final YamlConfiguration configuration;

    public BasicConfigurationFile(JavaPlugin plugin, String name, boolean overwrite, boolean saveResource) {
        super(plugin, name);

        this.file = new File(plugin.getDataFolder(), name + ".yml");
        if (saveResource && !file.exists()) plugin.saveResource(name + ".yml", overwrite);
        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        configuration.options().copyDefaults(true);

        this.applyHeader();
    }

    public BasicConfigurationFile(JavaPlugin plugin, String name, boolean overwrite) {
        this(plugin, name, overwrite, true);
    }

    public BasicConfigurationFile(JavaPlugin plugin, String name) {
        this(plugin, name, false);
    }

    public void applyHeader() {
        configuration.options().header(
                "#####################################################################\n" +
                "                                                                     #\n" +
                "          Array Practice Core - Developed By Drizzy#0278             #\n" +
                "       Bought at Refine Development - https://dsc.gg/refine          #\n" +
                "                                                                     #\n" +
                "#####################################################################");
        save();
    }

    public String getString(String path) {
        return this.configuration.contains(path) ? ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path)) : null;
    }

    public boolean contains(String path) {
        return this.configuration.contains(path);
    }

    public String getStringOrDefault(String path, String or) {
        String toReturn = this.getString(path);
        if (toReturn == null) {
            this.set(path, or);
            this.save();
            return or;
        }
        return toReturn;
    }

    public int getInteger(String path) {
        return this.configuration.contains(path) ? this.configuration.getInt(path) : 0;
    }

    public int getInteger(String path, int or) {
        int toReturn = this.getInteger(path);
        return this.configuration.contains(path) ? or : toReturn;
    }

    public void set(String path, Object value) {
        configuration.set(path, value);
    }

    public boolean getBoolean(String path) {
        return this.configuration.contains(path) && this.configuration.getBoolean(path);
    }

    public double getDouble(String path) {
        return this.configuration.contains(path) ? this.configuration.getDouble(path) : 0.0D;
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return this.getConfiguration().getConfigurationSection(path);
    }

    public Object get(String path) {
        return this.configuration.contains(path) ? this.configuration.get(path) : null;
    }

    public List<String> getStringList(String path) {
        return this.configuration.contains(path) ? this.configuration.getStringList(path) : null;
    }

    public void reload(){
        try {
            getConfiguration().load(file);
            getConfiguration().save(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            getConfiguration().save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }
}
