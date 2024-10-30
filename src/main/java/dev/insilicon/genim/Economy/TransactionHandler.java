package dev.insilicon.genim.Economy;

import dev.insilicon.genim.Genim;
import org.bukkit.entity.Player;

public class TransactionHandler {

    private EconomyManager economyManager;
    private Genim plugin;
    private EconomyDB economyDB;

    public TransactionHandler(EconomyManager economyManager, Genim plugin) {
        this.economyManager = economyManager;
        this.plugin = plugin;
        this.economyDB = economyManager.getEconomyDB();
    }

    public boolean printCash(Player player, double amount) {
        try {
            // Ensure player exists in database first
            economyDB.createPlayer(player);
            economyDB.addCash(player.getUniqueId(), amount);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean attemptTransaction(Player plr1, Player plr2, double amount) {
        try {
            // Ensure both players exist in database
            economyDB.createPlayer(plr1);
            economyDB.createPlayer(plr2);

            if (economyDB.getCash(plr1.getUniqueId()) >= amount) {
                economyDB.addCash(plr2.getUniqueId(), amount);
                economyDB.removeCash(plr1.getUniqueId(), amount);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean attemptRemoveCash(Player player, double amount) {
        try {
            // Ensure player exists in database
            economyDB.createPlayer(player);

            if (economyDB.getCash(player.getUniqueId()) >= amount) {
                economyDB.removeCash(player.getUniqueId(), amount);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeCash(Player player, double amount) {
        try {
            // Ensure player exists in database
            economyDB.createPlayer(player);

            economyDB.removeCash(player.getUniqueId(), amount);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public double balance(Player player) {
        // Ensure player exists in database before checking balance
        economyDB.createPlayer(player);
        return economyDB.getCash(player.getUniqueId());
    }
}