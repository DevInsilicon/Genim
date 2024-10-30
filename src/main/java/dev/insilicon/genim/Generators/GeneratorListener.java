package dev.insilicon.genim.Generators;

import dev.insilicon.genim.Economy.TransactionHandler;
import dev.insilicon.genim.Genim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.List;

public class GeneratorListener implements Listener {

    private GeneratorManager manager;
    private Genim plugin;
    private final NamespacedKey generatorTypeKey;
    private MiniMessage miniMessage = MiniMessage.miniMessage();

    public GeneratorListener(Genim plugin, GeneratorManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.generatorTypeKey = new NamespacedKey(plugin, "generator_type");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItemInHand();
        Block placedBlock = event.getBlockPlaced();

        if (itemInHand.getItemMeta().getPersistentDataContainer().has(CashPDTs.GENERATOR, PersistentDataType.STRING)) {
            String generatorType = itemInHand.getItemMeta().getPersistentDataContainer().get(CashPDTs.GENERATOR, PersistentDataType.STRING);

            GeneratorTypes type = GeneratorTypes.valueOf(generatorType);

            GeneratorInstance instance = new GeneratorInstance(type, placedBlock.getX(), placedBlock.getY(), placedBlock.getZ(), player.getUniqueId());

            manager.getGeneratorDisplayer().createGenerator(type, player.getUniqueId(), placedBlock.getX(), placedBlock.getY(), placedBlock.getZ());
            //event.setCancelled(true);



        }


    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Player player = event.getPlayer();

        // Check if the broken block is a generator
        for (GeneratorInstance generator : manager.getGenerators()) {
            if (generator.getX() == brokenBlock.getX() &&
                    generator.getY() == brokenBlock.getY() &&
                    generator.getZ() == brokenBlock.getZ()) {

                // Remove both the physical blocks and display entity
                manager.getGeneratorDisplayer().removeGenerator(
                        generator.getX(),
                        generator.getY(),
                        generator.getZ()
                );

                // Create the generator item
                ItemStack generatorItem = createGeneratorItem(generator.getType());

                // Add the generator item directly to the player's inventory
                player.getInventory().addItem(generatorItem);

                // Cancel the event to prevent the block from dropping its default item
                event.setDropItems(false);
                break;
            }
        }
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






}