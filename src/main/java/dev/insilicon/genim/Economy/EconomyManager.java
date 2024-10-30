package dev.insilicon.genim.Economy;

import dev.insilicon.genim.Genim;
import org.bukkit.entity.Player;

public class EconomyManager {

    private Genim plugin;
    private EconomyManager economyManager;
    private TransactionHandler transactionHandler;
    private EconomyDB economyDB;
    private EconomyPAPI economyPAPI;

    public EconomyManager(Genim plugin) {
        this.plugin = plugin;

        this.economyDB = new EconomyDB(plugin);
        this.transactionHandler = new TransactionHandler(this, plugin);

        // Initialize and register the PlaceholderAPI expansion
        this.economyPAPI = new EconomyPAPI(plugin, economyDB);
        if (!this.economyPAPI.register()) {
            plugin.getLogger().severe("Failed to register PlaceholderAPI expansion!");
        } else {
            plugin.getLogger().info("Successfully registered PlaceholderAPI expansion!");
        }

        // Register the listener
        plugin.getServer().getPluginManager().registerEvents(new EconListener(this, plugin), plugin);
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public TransactionHandler getTransactionHandler() {
        return transactionHandler;
    }

    public EconomyDB getEconomyDB() {
        return economyDB;
    }
}