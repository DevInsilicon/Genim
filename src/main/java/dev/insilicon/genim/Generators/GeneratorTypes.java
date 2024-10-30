package dev.insilicon.genim.Generators;

public enum GeneratorTypes {
    COBBLESTONE_GENERATOR("cobblestone_gen"), // tier 1
    STONE_GENERATOR("stone_gen"), // tier 2
    COAL_GENERATOR("coal_gen"), // Tier 3
    COAL_TIER2_GENERATOR("coal_tier2_gen"), // Tier 4
    IRON_GENERATOR("iron_gen"), // Tier 5
    IRON_TIER2_GENERATOR("iron_tier2_gen"), // Tier 6A
    GOLD_GENERATOR("gold_gen"), // Tier 7
    GOLD_TIER2_GENERATOR("gold_tier2_gen"), // Tier 8
    DIAMOND_GENERATOR("diamond_gen"), // Tier 9
    DIAMOND_TIER2_GENERATOR("diamond_tier2_gen"), // Tier 10
    EMERALD_GENERATOR("emerald_gen"), // Tier 11
    EMERALD_TIER2_GENERATOR("emerald_tier2_gen"), // Tier 12
    OBSIDIAN_GENERATOR("obsidian_gen"); // tier 13




    GeneratorTypes(String internalName) {

    }
}
