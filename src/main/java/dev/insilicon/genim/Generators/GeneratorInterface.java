package dev.insilicon.genim.Generators;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class GeneratorInterface implements Listener, CommandExecutor {



    // /shop
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player p = (Player)commandSender;
        if (!(commandSender instanceof Player))
        {
            return false;
        }

        //

        Inventory generators = Bukkit.getServer().createInventory(null, 27, "&bGenerators");

        ItemStack item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        for (int i =0; i < 27; i++) {
            generators.setItem(i, item);
        }

        ItemStack item2 = item;
        ItemMeta meta2 = item2.getItemMeta();
        meta2.getPersistentDataContainer().set(CashPDTs.GUI_KEY, PersistentDataType.STRING, "shop_gui");
        item2.setItemMeta(meta2);
        generators.setItem(1, item2);

            ItemStack ref1 = new ItemStack(Material.COBBLESTONE);


        return true;
    }


    // on click gui
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getInventory().getItem(1).getItemMeta().getPersistentDataContainer().has(CashPDTs.GUI_KEY, PersistentDataType.STRING)) {
            String keyType = e.getInventory().getItem(1).getItemMeta().getPersistentDataContainer().get(CashPDTs.GUI_KEY, PersistentDataType.STRING);

            if (!keyType.equals("shop_gui")) {
                return;
            }
        }

        // e



    }
}
