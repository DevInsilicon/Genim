package dev.insilicon.genim.Generators;

import dev.insilicon.genim.Genim;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class GeneratorDisplayer {

    private GeneratorManager manager;
    private Genim plugin;

    public GeneratorDisplayer(GeneratorManager manager, Genim plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    public void createGenerator(GeneratorTypes generatorType, UUID owner, int x, int y, int z) {
        storeInDB(generatorType, owner, x, y, z);
        displayGenerator(generatorType, x, y, z);
    }

    private void storeInDB(GeneratorTypes generatorType, UUID owner, int x, int y, int z) {
        GeneratorSQL generatorSQL = manager.getGeneratorSQL();
        generatorSQL.storeGenerator(generatorType, owner.toString(), x, y, z);
        manager.addGenerator(new GeneratorInstance(generatorType, x, y, z, owner));
    }

    private void displayGenerator(GeneratorTypes generatorType, int x, int y, int z) {
        switch (generatorType) {
            case COBBLESTONE_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.COBBLESTONE,
                        generatorType,
                        x, y, z
                );
                break;
            case STONE_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.STONE,
                        generatorType,
                        x, y, z
                );
                break;
            case COAL_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.COAL_BLOCK,
                        generatorType,
                        x, y, z
                );
                break;
            case COAL_TIER2_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.DEEPSLATE_COAL_ORE,
                        generatorType,
                        x, y, z
                );
                break;
            case IRON_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.IRON_BLOCK,
                        generatorType,
                        x, y, z
                );
                break;
            case IRON_TIER2_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.DEEPSLATE_IRON_ORE,
                        generatorType,
                        x, y, z
                );
                break;
            case GOLD_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.GOLD_BLOCK,
                        generatorType,
                        x, y, z
                );
                break;
            case GOLD_TIER2_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.DEEPSLATE_GOLD_ORE,
                        generatorType,
                        x, y, z
                );
                break;
            case DIAMOND_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.DIAMOND_BLOCK,
                        generatorType,
                        x, y, z
                );
                break;
            case DIAMOND_TIER2_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.DEEPSLATE_DIAMOND_ORE,
                        generatorType,
                        x, y, z
                );
                break;
            case EMERALD_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.EMERALD_BLOCK,
                        generatorType,
                        x, y, z
                );
                break;
            case EMERALD_TIER2_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.DEEPSLATE_EMERALD_ORE,
                        generatorType,
                        x, y, z
                );
                break;
            case OBSIDIAN_GENERATOR:
                createGeneratorDisplay(
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.OBSIDIAN,
                        generatorType,
                        x, y, z
                );
                break;

            default:
                plugin.getLogger().warning("Unknown generator type at " + x + ", " + y + ", " + z + " : " + generatorType);
        }
    }

    /**
     * Creates the visual display for a generator
     * @param containerMaterial The material of the containing block (usually glass)
     * @param displayMaterial The material to show inside the container
     * @param generatorType The type of generator being created
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return The created BlockDisplay entity
     */
    private BlockDisplay createGeneratorDisplay(Material containerMaterial, Material displayMaterial,
                                                GeneratorTypes generatorType, int x, int y, int z) {
        World world = plugin.getServer().getWorld("world");
        Block block = world.getBlockAt(x, y, z);

        // FIRST: Place the actual glass block in the world
        block.setType(containerMaterial);

        // THEN: Create the block display entity AFTER the glass is placed
        BlockDisplay display = (BlockDisplay) world.spawnEntity(
                block.getLocation().add(0.2, 0.2, 0.2),
                EntityType.BLOCK_DISPLAY
        );

        // Set the display block material
        display.setBlock(displayMaterial.createBlockData());

        // Set up the transformation
        Transformation transformation = display.getTransformation();
        transformation.getScale().set(0.6f, 0.6f, 0.6f);
        display.setTransformation(transformation);

        // Add custom NBT data
        PersistentDataContainer dataContainer = display.getPersistentDataContainer();

        // Create keys for our custom data
        NamespacedKey typeKey = new NamespacedKey(plugin, "generator_type");
        NamespacedKey posXKey = new NamespacedKey(plugin, "pos_x");
        NamespacedKey posYKey = new NamespacedKey(plugin, "pos_y");
        NamespacedKey posZKey = new NamespacedKey(plugin, "pos_z");

        // Store the data
        dataContainer.set(typeKey, PersistentDataType.STRING, generatorType.toString());
        dataContainer.set(posXKey, PersistentDataType.INTEGER, x);
        dataContainer.set(posYKey, PersistentDataType.INTEGER, y);
        dataContainer.set(posZKey, PersistentDataType.INTEGER, z);

        // Make the display entity glow and protect it
        display.setGlowing(false);
        display.setPersistent(true);
        display.setInvulnerable(true);

        return display;
    }

    /**
     * Removes a generator at the specified location
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public void removeGenerator(int x, int y, int z) {
        World world = plugin.getServer().getWorld("world");
        Block block = world.getBlockAt(x, y, z);

        // Remove the block
        block.setType(Material.AIR);

        // Remove ALL display entities in this block (both container and material displays)
        world.getNearbyEntities(block.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5).forEach(entity -> {
            if (entity instanceof BlockDisplay) {
                entity.remove();
            }
        });

        // Remove from database
        GeneratorSQL generatorSQL = manager.getGeneratorSQL();
        generatorSQL.removeGenerator(x, y, z);

        // Get list of generators
        List<GeneratorInstance> generators = manager.getGenerators();

        // Find generator with same coords
        for (GeneratorInstance generator : generators) {
            if (generator.getX() == x && generator.getY() == y && generator.getZ() == z) {
                manager.removeGenerator(generator);
                break;
            }
        }

        // Set new list of generators
        manager.setGeneratorList(generators);
    }
}