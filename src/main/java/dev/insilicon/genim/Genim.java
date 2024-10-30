package dev.insilicon.genim;

import dev.insilicon.genim.Economy.EconomyManager;
import dev.insilicon.genim.Generators.GeneratorManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Genim extends JavaPlugin {

    private GeneratorManager generatorManager;
    private EconomyManager economyManager;

    @Override
    public void onEnable() {
        // Plugin startup logic


        generatorManager = new GeneratorManager(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("PlaceholderAPI found! Registering expansion...");
            economyManager = new EconomyManager(this);
        } else {
            getLogger().severe("PlaceholderAPI not found! PlaceholderAPI expansion will not be registered.");
            Bukkit.getPluginManager().disablePlugin(this);
        }


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic


    }

    public GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}
