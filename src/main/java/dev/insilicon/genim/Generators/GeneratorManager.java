package dev.insilicon.genim.Generators;

import dev.insilicon.genim.Genim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class GeneratorManager {

    private Genim plugin;
    private GeneratorSQL generatorSQL;
    private GeneratorDisplayer generatorDisplayer;
    private GeneratorListener generatorListener;
    private MiniMessage miniMessage = MiniMessage.miniMessage();
    private GeneratorInterface generatorInterface;

    private List<GeneratorInstance> generators = new ArrayList<>();


    public GeneratorManager(Genim plugin) {

        this.plugin = plugin;

        this.generatorSQL = new GeneratorSQL(plugin);
        this.generatorDisplayer = new GeneratorDisplayer(this, plugin);
        this.generatorListener = new GeneratorListener(plugin, this);


        // register listener
        plugin.getServer().getPluginManager().registerEvents(generatorListener, plugin);

        generatorSQL.loadGenerators(this);

        //register comamnd
        plugin.getCommand("genim").setExecutor(new GeneratorCommand(plugin, this));
        // register tab
        plugin.getCommand("genim").setTabCompleter(new GeneratorCommand(plugin, this));

        // scheduler for the 10 second timer
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                everyTenSeconds();
            }
        }, 0L, 200L);



        // register command
        //plugin.getCommand("genim").setExecutor(new GeneratorCommand(plugin, this));
        // register listener
        //plugin.getServer().getPluginManager().registerEvents(new GeneratorInterface(), plugin);

    }

    public GeneratorDisplayer getGeneratorDisplayer() {
        return generatorDisplayer;
    }

    public void removeGenerator(GeneratorInstance generator) {
        generators.remove(generator);
    }

    public void addGenerator(GeneratorInstance generator) {
        generators.add(generator);
    }

    public List<GeneratorInstance> getGenerators() {
        return generators;
    }

    public void setGeneratorList(List<GeneratorInstance> generators) {
        this.generators = generators;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public GeneratorSQL getGeneratorSQL() {
        return generatorSQL;
    }

    public void tick() {

    }

    public void everySecond() {

    }

    public void everyFiveSeconds() {

    }

    public void everyTenSeconds() {
        spawnCash();
    }


    public void spawnCash() {

        // for every generator run through a switch statement to spawn the correct amount of cash
        for (GeneratorInstance generator : generators) {
            switch (generator.getType()) {
                case COAL_GENERATOR:

                    spawnBill(5, 1, generator.getX(), generator.getY(), generator.getZ());

                    break;
                case COAL_TIER2_GENERATOR:

                        spawnBill(5, 2, generator.getX(), generator.getY(), generator.getZ());

                        break;
                case IRON_GENERATOR:

                            spawnBill(10, 1, generator.getX(), generator.getY(), generator.getZ());

                            break;
                case IRON_TIER2_GENERATOR:

                                spawnBill(10, 2, generator.getX(), generator.getY(), generator.getZ());
                    spawnBill(5, 1, generator.getX(), generator.getY(), generator.getZ());

                                break;
                case GOLD_GENERATOR:

                                    spawnBill(20, 1, generator.getX(), generator.getY(), generator.getZ());

                                    break;
                case GOLD_TIER2_GENERATOR:

                                        spawnBill(20, 1, generator.getX(), generator.getY(), generator.getZ());
                    spawnBill(5, 1, generator.getX(), generator.getY(), generator.getZ());


                                        break;
                case DIAMOND_GENERATOR:

                                            spawnBill(30, 1, generator.getX(), generator.getY(), generator.getZ());

                                            break;
                case DIAMOND_TIER2_GENERATOR:

                                                spawnBill(50, 1, generator.getX(), generator.getY(), generator.getZ());
                    spawnBill(30, 1, generator.getX(), generator.getY(), generator.getZ());


                                                    break;
                case EMERALD_GENERATOR:

                                                        spawnBill(50, 1, generator.getX(), generator.getY(), generator.getZ());
                    spawnBill(20, 1, generator.getX(), generator.getY(), generator.getZ());
                    spawnBill(5, 1, generator.getX(), generator.getY(), generator.getZ());

                                                        break;
                case EMERALD_TIER2_GENERATOR:

                                                            spawnBill(100, 1, generator.getX(), generator.getY(), generator.getZ());

                case OBSIDIAN_GENERATOR:

                                                                    spawnBill(100, 1, generator.getX(), generator.getY(), generator.getZ());
                    spawnBill(50, 1, generator.getX(), generator.getY(), generator.getZ());

                                                                    break;




            }
        }


    }

    public void spawnBill(int amount, int spawnCount, int x, int y, int z) {
        ItemStack bill = new ItemStack(Material.PAPER, spawnCount);
        ItemMeta billMeta = bill.getItemMeta();

        // Use MiniMessage for proper color formatting
        billMeta.displayName(miniMessage.deserialize("<green>$" + amount + " Bill</green>"));

        // Set the persistent data
        billMeta.getPersistentDataContainer().set(CashPDTs.DOLLAR_BILL, PersistentDataType.INTEGER, amount);
        bill.setItemMeta(billMeta);

        // Spawn the bills
        plugin.getServer().getWorlds().get(0)
                .dropItemNaturally(plugin.getServer().getWorlds().get(0).getBlockAt(x, y + 2, z).getLocation(), bill);
    }


}
