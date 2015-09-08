package com.hexicraft.games;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Ollie
 * @version 1.0
 */
public class YamlFile extends YamlConfiguration {

    private String fileName;
    private JavaPlugin plugin;
    private boolean loadResource;
    private File configFile;

    YamlFile(JavaPlugin plugin, String fileName, boolean loadResource) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.loadResource = loadResource;
        configFile = new File(plugin.getDataFolder(), fileName);
        loadFile();
    }

    /**
     * Loads the file, if it doesn't exist then it creates a new one
     */
    public boolean loadFile() {
        try {
            if (!configFile.exists()) {
                if (loadResource) {
                    load(new InputStreamReader(plugin.getResource(fileName)));
                }
                saveFile();
            } else {
                load(configFile);
            }
            return true;
        } catch (InvalidConfigurationException | IOException e) {
            plugin.getLogger().warning("Error loading YAML file.\n" + e.getMessage());
            return false;
        }
    }

    /**
     * Saves the file to disk
     */
    public void saveFile() {
        try {
            save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Error saving YAML file.\n" + e.getMessage());
        }
    }

    public void set(String path, Object value, boolean save) {
        super.set(path, value);
        if (save) {
            saveFile();
        }
    }
}
