package dev.insilicon.genim.Generators;

import dev.insilicon.genim.Economy.TransactionHandler;
import dev.insilicon.genim.Genim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GeneratorCommand implements CommandExecutor, TabCompleter {

    private GeneratorManager manager;
    private Genim plugin;
    private MiniMessage miniMessage = MiniMessage.miniMessage();

    public GeneratorCommand(Genim plugin, GeneratorManager manager) {
        this.manager = manager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        System.out.println("command");
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(miniMessage.deserialize("<red>Usage: /genim give <generator_type>"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give":
                System.out.println("give command");
                if (args.length < 2) {
                    player.sendMessage(miniMessage.deserialize("<red>Please specify a generator type!"));
                    return true;
                }

                try {
                    GeneratorTypes type = GeneratorTypes.valueOf(args[1].toUpperCase());
                    ItemStack generatorItem = createGeneratorItem(type);
                    player.getInventory().addItem(generatorItem);
                    player.sendMessage(miniMessage.deserialize("<green>Given " + type.toString().replace("_", " ") + "!"));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(miniMessage.deserialize("<red>Invalid generator type!"));
                }
                break;

            case "list":
                StringBuilder types = new StringBuilder("<yellow>Available generator types:</yellow>\n");
                for (GeneratorTypes type : GeneratorTypes.values()) {
                    types.append("<green>").append(type.toString()).append("</green>\n");
                }
                player.sendMessage(miniMessage.deserialize(types.toString()));
                break;

            case "economy":
                // check if theres second argument
                if (args.length < 2) {
                    player.sendMessage(miniMessage.deserialize("<red>Please specify a generator type!"));
                    return true;
                }

                TransactionHandler transactionHandler = plugin.getEconomyManager().getTransactionHandler();

                switch (args[1]) {
                    case "print":
                        if (args.length < 3) {

                            player.sendMessage(miniMessage.deserialize("<red>Please specify a player!"));
                            return true;

                        }
                        Player target = plugin.getServer().getPlayer(args[2]);

                        if (target == null) {
                            player.sendMessage(miniMessage.deserialize("<red>Player not found!"));
                            return true;
                        }

                        if (args.length < 4) {
                            player.sendMessage(miniMessage.deserialize("<red>Please specify a amount!"));
                            return true;
                        }

                        try {
                            double amount = Double.parseDouble(args[3]);
                            transactionHandler.printCash(target, amount);
                            player.sendMessage(miniMessage.deserialize("<green>Printed " + amount + " to " + target.getName()));
                        } catch (NumberFormatException e) {
                            player.sendMessage(miniMessage.deserialize("<red>Invalid amount!"));
                        }


                        break;

                    case "remove":
                        if (args.length < 3) {
                            player.sendMessage(miniMessage.deserialize("<red>Please specify a player!"));
                            return true;
                        }
                        Player target2 = plugin.getServer().getPlayer(args[2]);

                        if (target2 == null) {
                            player.sendMessage(miniMessage.deserialize("<red>Player not found!"));
                            return true;
                        }

                        if (args.length < 4) {
                            player.sendMessage(miniMessage.deserialize("<red>Please specify a amount!"));
                            return true;
                        }

                        try {
                            double amount = Double.parseDouble(args[3]);
                            transactionHandler.removeCash(target2, amount);
                            player.sendMessage(miniMessage.deserialize("<green>Removed " + amount + " from " + target2.getName()));
                        } catch (NumberFormatException e) {
                            player.sendMessage(miniMessage.deserialize("<red>Invalid amount!"));
                        }
                        break;

                    case "bal":
                        if (args.length < 3) {
                            player.sendMessage(miniMessage.deserialize("<red>Please specify a player!"));
                            return true;
                        }
                        Player target3 = plugin.getServer().getPlayer(args[2]);

                        if (target3 == null) {
                            player.sendMessage(miniMessage.deserialize("<red>Player not found!"));
                            return true;
                        }

                        player.sendMessage(miniMessage.deserialize("<green>Balance of " + target3.getName() + ": " + transactionHandler.balance(target3)));
                        break;
                }


            default:
                player.sendMessage(miniMessage.deserialize("<red>Unknown command! Use /genim give <type> or /genim list"));
        }

        return true;
    }

    private ItemStack createGeneratorItem(GeneratorTypes type) {
        // Choose appropriate material based on type
        Material material;
        switch(type) {
            case COAL_GENERATOR:
                material = Material.COAL_ORE;
                break;
            case COAL_TIER2_GENERATOR:
                material = Material.DEEPSLATE_COAL_ORE;
                break;
            case IRON_GENERATOR:
                material = Material.IRON_ORE;
                break;
            case IRON_TIER2_GENERATOR:
                material = Material.DEEPSLATE_IRON_ORE;
                break;
            case GOLD_GENERATOR:
                material = Material.GOLD_ORE;
                break;
            case GOLD_TIER2_GENERATOR:
                material = Material.DEEPSLATE_GOLD_ORE;
                break;
            case DIAMOND_GENERATOR:
                material = Material.DIAMOND_ORE;
                break;
            case DIAMOND_TIER2_GENERATOR:
                material = Material.DEEPSLATE_DIAMOND_ORE;
                break;
            case EMERALD_GENERATOR:
                material = Material.EMERALD_ORE;
                break;
            case EMERALD_TIER2_GENERATOR:
                material = Material.DEEPSLATE_EMERALD_ORE;
                break;
            case OBSIDIAN_GENERATOR:
                material = Material.OBSIDIAN;
                break;
            default:
                material = Material.STONE;
        }

        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        // Set name and lore based on type
        switch(type) {
            case COAL_GENERATOR:
                meta.displayName(miniMessage.deserialize("<grey>Coal Generator</grey>"));
                meta.lore(List.of(
                        miniMessage.deserialize("Place down the generator to start generating cash"),
                        miniMessage.deserialize("<green>Generates <bold>$5 every 10</bold> seconds</green>")
                ));
                break;
            case COAL_TIER2_GENERATOR:
                meta.displayName(miniMessage.deserialize("<grey><gold>Tier 2</gold> Coal Generator</grey>"));
                meta.lore(List.of(
                        miniMessage.deserialize("Place down the generator to start generating cash"),
                        miniMessage.deserialize("<green>Generates <bold>$10 every 10</bold> seconds</green>")
                ));
                break;
            case IRON_GENERATOR:
                meta.displayName(miniMessage.deserialize("<white>Iron Generator</white>"));
                meta.lore(List.of(
                        miniMessage.deserialize("Place down the generator to start generating cash"),
                        miniMessage.deserialize("<green>Generates <bold>$10 every 10</bold> seconds</green>")
                ));
                break;
            case IRON_TIER2_GENERATOR:
                meta.displayName(miniMessage.deserialize("<white><gold>Tier 2</gold> Iron Generator</white>"));
                meta.lore(List.of(
                        miniMessage.deserialize("Place down the generator to start generating cash"),
                        miniMessage.deserialize("<green>Generates <bold>$15 every 10</bold> seconds</green>")
                ));
                break;
            case GOLD_GENERATOR:
                meta.displayName(miniMessage.deserialize("<yellow>Gold Generator</yellow>"));
                meta.lore(List.of(
                        miniMessage.deserialize("Place down the generator to start generating cash"),
                        miniMessage.deserialize("<green>Generates <bold>$20 every 10</bold> seconds</green>")
                ));
                break;
            case GOLD_TIER2_GENERATOR:
                meta.displayName(miniMessage.deserialize("<yellow><gold>Tier 2</gold> Gold Generator</yellow>"));
                meta.lore(List.of(
                        miniMessage.deserialize("Place down the generator to start generating cash"),
                        miniMessage.deserialize("<green>Generates <bold>$25 every 10</bold> seconds</green>")
                ));
                break;
            case DIAMOND_GENERATOR:
                meta.displayName(miniMessage.deserialize("<aqua>Diamond Generator</aqua>"));
                meta.lore(List.of(
                        miniMessage.deserialize("Place down the generator to start generating cash"),
                        miniMessage.deserialize("<green>Generates <bold>$30 every 10</bold> seconds</green>")
                ));
                break;
            case DIAMOND_TIER2_GENERATOR:
                meta.displayName(miniMessage.deserialize("<aqua><gold>Tier 2</gold> Diamond Generator</aqua>"));
                meta.lore(List.of(
                        miniMessage.deserialize("Place down the generator to start generating cash"),
                        miniMessage.deserialize("<green>Generates <bold>$80 every 10</bold> seconds</green>")
                ));
                break;
            case EMERALD_GENERATOR:
                meta.displayName(miniMessage.deserialize("<green>Emerald Generator</green>"));
                meta.lore(List.of(
                        miniMessage.deserialize("Place down the generator to start generating cash"),
                        miniMessage.deserialize("<green>Generates <bold>$75 every 10</bold> seconds</green>")
                ));
                break;
            case EMERALD_TIER2_GENERATOR:
                meta.displayName(miniMessage.deserialize("<green><gold>Tier 2</gold> Emerald Generator</green>"));
                meta.lore(List.of(
                        miniMessage.deserialize("Place down the generator to start generating cash"),
                        miniMessage.deserialize("<green>Generates <bold>$100 every 10</bold> seconds</green>")
                ));
                break;
            case OBSIDIAN_GENERATOR:
                meta.displayName(miniMessage.deserialize("<dark_purple>Obsidian Generator</dark_purple>"));
                meta.lore(List.of(
                        miniMessage.deserialize("Place down the generator to start generating cash"),
                        miniMessage.deserialize("<green>Generates <bold>$150 every 10</bold> seconds</green>")
                ));
                break;
        }

        // Store the generator type in NBT
        meta.getPersistentDataContainer().set(
                CashPDTs.GENERATOR,
                PersistentDataType.STRING,
                type.toString()
        );

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("give", "list", "economy");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return List.of("COAL_GENERATOR", "COAL_TIER2_GENERATOR", "IRON_GENERATOR", "IRON_TIER2_GENERATOR", "GOLD_GENERATOR", "GOLD_TIER2_GENERATOR", "DIAMOND_GENERATOR", "DIAMOND_TIER2_GENERATOR", "EMERALD_GENERATOR", "EMERALD_TIER2_GENERATOR", "OBSIDIAN_GENERATOR");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("economy")) {
            return List.of("print", "remove", "bal");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("economy") && (args[1].equalsIgnoreCase("print") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("bal"))) {
            return null; // Return null to let the server handle player name completion
        } else if (args.length == 4 && args[0].equalsIgnoreCase("economy") && (args[1].equalsIgnoreCase("print") || args[1].equalsIgnoreCase("remove"))) {
            return List.of("amount"); // Placeholder for amount, can be customized
        }
        return List.of();
    }
}