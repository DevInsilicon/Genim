package dev.insilicon.genim.Economy;

import dev.insilicon.genim.Genim;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EconomyPAPI extends PlaceholderExpansion {

    private final Genim plugin;
    private final EconomyDB economyDB;

    public EconomyPAPI(Genim plugin, EconomyDB economyDB) {
        this.plugin = plugin;
        this.economyDB = economyDB;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "genim"; // This will be the prefix for all placeholders
    }

    @Override
    public @NotNull String getAuthor() {
        return "InSilicon";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required to keep the expansion registered until the server stops
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        // Handle cash placeholder: %genim_cash%
        if (params.equals("cash")) {
            double cash = economyDB.getCash(player.getUniqueId());
            return String.format("%.2f", cash); // Returns cash with 2 decimal places
        }

        // Handle crystals placeholder: %genim_crystals%
        if (params.equals("crystals")) {
            double crystals = economyDB.getCrystals(player.getUniqueId());
            return String.format("%.2f", crystals); // Returns crystals with 2 decimal places
        }

        return null; // Placeholder is not recognized
    }
}