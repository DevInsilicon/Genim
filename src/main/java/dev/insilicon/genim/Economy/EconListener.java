package dev.insilicon.genim.Economy;

import dev.insilicon.genim.Generators.CashPDTs;
import dev.insilicon.genim.Genim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class EconListener implements Listener {

    private EconomyManager economyManager;
    private Genim plugin;

    public EconListener(EconomyManager economyManager, Genim plugin) {
        this.economyManager = economyManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if item exists and economy manager exists
        if (item == null || economyManager == null || item.getItemMeta() == null) {
            return;
        }

        TransactionHandler transactionHandler = economyManager.getTransactionHandler();
        if (transactionHandler == null) {
            return;
        }

        // Check if item has the dollar bill tag
        if (item.getItemMeta().getPersistentDataContainer().has(CashPDTs.DOLLAR_BILL, PersistentDataType.INTEGER)) {
            int amountTotal = 0;

            // Loop through inventory contents
            for (ItemStack i : player.getInventory().getContents()) {
                // Skip null items or items without metadata
                if (i == null || i.getItemMeta() == null) {
                    continue;
                }

                // Check if item has dollar bill tag
                if (i.getItemMeta().getPersistentDataContainer().has(CashPDTs.DOLLAR_BILL, PersistentDataType.INTEGER)) {
                    // Get the amount of money
                    int amount = i.getItemMeta().getPersistentDataContainer().get(CashPDTs.DOLLAR_BILL, PersistentDataType.INTEGER) * i.getAmount();
                    amountTotal += amount;

                    // give the cash and remove the item
                    transactionHandler.printCash(player, amount);
                    i.setAmount(0);
                }
            }

            // Only process rewards if there was actually money redeemed
            if (amountTotal > 0) {
                // Calculate EXP reward
                // For reference: ~7 XP points = ~1 level at the very start
                // We want roughly 3000$ to give about 1.4 levels
                // So let's calculate base XP as: cash amount * 0.00007
                double baseExpReward = amountTotal * 0.00007; // 3000 cash = ~0.21 base reward

                // Add small variance (±10%)
                double variance = baseExpReward * 0.10;
                double randomVariance = (Math.random() * variance * 2) - variance;
                double finalExpReward = Math.max(0, baseExpReward + randomVariance);

                // Round to 2 decimal places for display
                double displayExp = Math.round(finalExpReward * 1000.0) / 10.0;

                // Convert to actual exp points (multiply by 100 to convert from levels to points)
                // Each level needs 7 points at the start, so multiply by 7
                int actualExpPoints = (int)(finalExpReward * 100 * 7);

                // Give the exp
                if (actualExpPoints > 0) {
                    player.giveExp(actualExpPoints);
                }

                // Send messages
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>$ " + amountTotal + " has been added to your account!"));
                player.sendActionBar(MiniMessage.miniMessage().deserialize("<green>+" + amountTotal + " Cash   <blue>+0 Sapphire   <color:#d2eb34>+" + displayExp + " Exp"));
            }
        }
    }
}