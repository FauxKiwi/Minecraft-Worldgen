package deepslate.server;

import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeEffects;

import java.util.HashMap;
import java.util.Map;

import static net.minestom.server.world.biomes.Biome.Category.*;

public class Biomes {
    public static boolean isColdWater(String biome) {
        return switch (biome) {
            case "cold_ocean", "deep_cold_ocean", "frozen_ocean", "deep_frozen_ocean",
                    "ocean", "deep_ocean", "river", "frozen_river" -> true;
            default -> false;
        };
    }

    public static boolean isWarmWater(String biome) {
        return switch (biome) {
            case "lukewarm_ocean", "deep_lukewarm_ocean", "warm_ocean" -> true;
            default -> false;
        };
    }

    public static boolean isSandy(String biome) {
        return switch (biome) {
            case "desert", "beach", "snowy_beach" -> true;
            default -> false;
        };
    }

    public static boolean isSnowy(String biome) {
        return switch (biome) {
            case "grove", "ice_spikes", "jagged_peaks", "snowy_beach",
                    "snowy_plains", "snowy_slopes", "snowy_taiga" -> true;
            default -> false;
        };
    }

    public static boolean hasDeepSnow(String biome) {
        return switch (biome) {
            case "grove", "jagged_peaks", "snowy_slopes" -> true;
            default -> false;
        };
    }

    public static boolean isFrozen(String biome) {
        return switch (biome) {
            case "frozen_ocean", "deep_frozen_ocean", "frozen_river" -> true;
            default -> false;
        };
    }

    public static boolean isStony(String biome) {
        return switch (biome) {
            case "stony_peaks", "jagged_peaks", "snowy_slopes" -> true;
            default -> false;
        };
    }

    public static boolean isBadlands(String biome) {
        return switch (biome) {
            case "badlands", "wooded_badlands", "eroded_badlands" -> true;
            default -> false;
        };
    }

    private static final BiomeEffects DEFAULT_EFFECTS = BiomeEffects.builder()
            .skyColor(7907327)
            .fogColor(12638463)
            .waterColor(4159204)
            .waterFogColor(329011)
            .build();

    private static final BiomeEffects BADLANDS_EFFECTS = BiomeEffects.builder()
            .skyColor(7254527)
            .fogColor(12638463)
            .waterColor(4159204)
            .waterFogColor(329011)
            .foliageColor(10387789)
            .grassColor(9470285)
            .build();

    private static final BiomeEffects DARK_FOREST_EFFECTS = BiomeEffects.builder() //TODO
            .skyColor(7972607)
            .fogColor(12638463)
            .waterColor(4159204)
            .waterFogColor(329011)
            .grassColorModifier(BiomeEffects.GrassColorModifier.DARK_FOREST)
            .build();

    private static final BiomeEffects SWAMP_EFFECTS = BiomeEffects.builder() //TODO
            .skyColor(7907327)
            .foliageColor(6975545)
            .fogColor(12638463)
            .waterColor(6388580)
            .waterFogColor(2302743)
            .grassColorModifier(BiomeEffects.GrassColorModifier.SWAMP)
            .build();


    private static final Map<String, Biome> map = new HashMap<>();

    static {
        String[] biomeIds = {
                "badlands",
                "bamboo_jungle",
                "basalt_deltas",
                "beach",
                "birch_forest",
                "cold_ocean",
                "crimson_forest",
                "dark_forest",
                "deep_cold_ocean",
                "deep_frozen_ocean",
                "deep_lukewarm_ocean",
                "deep_ocean",
                "desert",
                "dripstone_caves",
                "end_barrens",
                "end_highlands",
                "end_midlands",
                "eroded_badlands",
                "flower_forest",
                "forest",
                "frozen_ocean",
                "frozen_peaks",
                "frozen_river",
                "grove",
                "ice_spikes",
                "jagged_peaks",
                "jungle",
                "lukewarm_ocean",
                "lush_caves",
                "meadow",
                "mushroom_fields",
                "nether_wastes",
                "ocean",
                "old_growth_birch_forest",
                "old_growth_pine_taiga",
                "old_growth_spruce_taiga",
                "plains",
                "river",
                "savanna",
                "savanna_plateau",
                "small_end_islands",
                "snowy_beach",
                "snowy_plains",
                "snowy_slopes",
                "snowy_taiga",
                "soul_sand_valley",
                "sparse_jungle",
                "stony_peaks",
                "stony_shore",
                "sunflower_plains",
                "swamp",
                "taiga",
                "the_end",
                "the_void",
                "warm_ocean",
                "warped_forest",
                "windswept_forest",
                "windswept_gravelly_hills",
                "windswept_hills",
                "windswept_savanna",
                "wooded_badlands"
        };
        Biome.Category[] categories = {
                MESA, //"badlands",
                JUNGLE, //"bamboo_jungle",
                NETHER, //"basalt_deltas",
                BEACH, //"beach",
                FOREST, //"birch_forest",
                OCEAN, //"cold_ocean",
                NETHER, //"crimson_forest",
                FOREST, //"dark_forest",
                OCEAN, //"deep_cold_ocean",
                OCEAN, //"deep_frozen_ocean",
                OCEAN, //"deep_lukewarm_ocean",
                OCEAN, //"deep_ocean",
                DESERT, //"desert",
                /*UNDERGROUND*/ Biome.Category.NONE, //"dripstone_caves",
                THE_END, //"end_barrens",
                THE_END, //"end_highlands",
                THE_END, //"end_midlands",
                MESA, //"eroded_badlands",
                FOREST, //"flower_forest",
                FOREST, //"forest",
                OCEAN, //"frozen_ocean",
                /*MOUNTAINS*/ EXTREME_HILLS, //"frozen_peaks",
                RIVER, //"frozen_river",
                FOREST, //"grove",
                ICY, //"ice_spikes",
                /*MOUNTAINS*/ EXTREME_HILLS, //"jagged_peaks",
                JUNGLE, //"jungle",
                OCEAN, //"lukewarm_ocean",
                /*UNDERGROUND*/ Biome.Category.NONE, //"lush_caves",
                /*MOUNTAINS*/ EXTREME_HILLS, //"meadow",
                MUSHROOM, //"mushroom_fields",
                NETHER, //"nether_wastes",
                OCEAN, //"ocean",
                FOREST, //"old_growth_birch_forest",
                TAIGA, //"old_growth_pine_taiga",
                TAIGA, //"old_growth_spruce_taiga",
                PLAINS, //"plains",
                RIVER, //"river",
                SAVANNA, //"savanna",
                SAVANNA, //"savanna_plateau",
                THE_END, //"small_end_islands",
                BEACH, //"snowy_beach",
                ICY, //"snowy_plains",
                /*MOUNTAINS*/ EXTREME_HILLS, //"snowy_slopes",
                TAIGA, //"snowy_taiga",
                NETHER, //"soul_sand_valley",
                JUNGLE, //"sparse_jungle",
                /*MOUNTAINS*/ EXTREME_HILLS, //"stony_peaks",
                BEACH, //"stony_shore",
                PLAINS, //"sunflower_plains",
                SWAMP, //"swamp",
                TAIGA, //"taiga",
                THE_END, //"the_end",
                THE_END, //"the_void",
                OCEAN, //"warm_ocean",
                NETHER, //"warped_forest",
                EXTREME_HILLS, //"windswept_forest",
                EXTREME_HILLS, //"windswept_gravelly_hills",
                EXTREME_HILLS, //"windswept_hills",
                SAVANNA, //"windswept_savanna",
                MESA, //"wooded_badlands"
        };
        float[] temperatures = {
                2f,//"badlands",
                0.95f,//"bamboo_jungle",
                2f,//"basalt_deltas",
                0.8f,//"beach",
                0.6f,//"birch_forest",
                0.5f,//"cold_ocean",
                2f,//"crimson_forest",
                0.7f,//"dark_forest",
                0.5f,//"deep_cold_ocean",
                0.5f,//"deep_frozen_ocean",
                0.5f,//"deep_lukewarm_ocean",
                0.5f,//"deep_ocean",
                2f,//"desert",
                0.8f,//"dripstone_caves",
                0.5f,//"end_barrens",
                0.5f,//"end_highlands",
                0.5f,//"end_midlands",
                2f,//"eroded_badlands",
                0.7f,//"flower_forest",
                0.7f,//"forest",
                0f,//"frozen_ocean",
                -0.7f,//"frozen_peaks",
                0f,//"frozen_river",
                -0.2f,//"grove",
                0f,//"ice_spikes",
                -0.7f,//"jagged_peaks",
                0.95f,//"jungle",
                0.5f,//"lukewarm_ocean",
                0.5f,//"lush_caves",
                0.5f,//"meadow",
                0.9f,//"mushroom_fields",
                2f,//"nether_wastes",
                0.5f,//"ocean",
                0.6f,//"old_growth_birch_forest",
                0.3f,//"old_growth_pine_taiga",
                0.25f,//"old_growth_spruce_taiga",
                0.8f,//"plains",
                0.5f,//"river",
                1.2f,//"savanna",
                1f,//"savanna_plateau",
                0.5f,//"small_end_islands",
                0.05f,//"snowy_beach",
                0f,//"snowy_plains",
                -0.3f,//"snowy_slopes",
                -0.5f,//"snowy_taiga",
                2f,//"soul_sand_valley",
                0.95f,//"sparse_jungle",
                1f,//"stony_peaks",
                0.2f,//"stony_shore",
                0.8f,//"sunflower_plains",
                0.8f,//"swamp",
                0.25f,//"taiga",
                0.5f,//"the_end",
                0.5f,//"the_void",
                0.5f,//"warm_ocean",
                2f,//"warped_forest",
                0.2f,//"windswept_forest",
                0.2f,//"windswept_gravelly_hills",
                0.2f,//"windswept_hills",
                1.1f,//"windswept_savanna",
                2f,//"wooded_badlands"
        };
        float[] downfalls = {
                0f,//"badlands",
                0.9f,//"bamboo_jungle",
                0f,//"basalt_deltas",
                0.4f,//"beach",
                0.6f,//"birch_forest",
                0.5f,//"cold_ocean",
                0f,//"crimson_forest",
                0.8f,//"dark_forest",
                0.5f,//"deep_cold_ocean",
                0.5f,//"deep_frozen_ocean",
                0.5f,//"deep_lukewarm_ocean",
                0.5f,//"deep_ocean",
                0f,//"desert",
                0.4f,//"dripstone_caves",
                0.5f,//"end_barrens",
                0.5f,//"end_highlands",
                0.5f,//"end_midlands",
                0f,//"eroded_badlands",
                0.8f,//"flower_forest",
                0.8f,//"forest",
                0.5f,//"frozen_ocean",
                0.9f,//"frozen_peaks",
                0.5f,//"frozen_river",
                0.8f,//"grove",
                0.5f,//"ice_spikes",
                0.9f,//"jagged_peaks",
                0.9f,//"jungle",
                0.5f,//"lukewarm_ocean",
                0.5f,//"lush_caves",
                0.8f,//"meadow",
                1f,//"mushroom_fields",
                0f,//"nether_wastes",
                0.5f,//"ocean",
                0.6f,//"old_growth_birch_forest",
                0.8f,//"old_growth_pine_taiga",
                0.8f,//"old_growth_spruce_taiga",
                0.4f,//"plains",
                0.5f,//"river",
                0f,//"savanna",
                0f,//"savanna_plateau",
                0.5f,//"small_end_islands",
                0.3f,//"snowy_beach",
                0.5f,//"snowy_plains",
                0.9f,//"snowy_slopes",
                0.4f,//"snowy_taiga",
                0f,//"soul_sand_valley",
                0.8f,//"sparse_jungle",
                0.3f,//"stony_peaks",
                0.3f,//"stony_shore",
                0.4f,//"sunflower_plains",
                0.9f,//"swamp",
                0.8f,//"taiga",
                0.5f,//"the_end",
                0.5f,//"the_void",
                0.5f,//"warm_ocean",
                0f,//"warped_forest",
                0.3f,//"windswept_forest",
                0.3f,//"windswept_gravelly_hills",
                0.3f,//"windswept_hills",
                0f,//"windswept_savanna",
                0f,//"wooded_badlands"
        };
        BiomeEffects[] effects = {
                BADLANDS_EFFECTS,//"badlands",
                DEFAULT_EFFECTS,//"bamboo_jungle",
                DEFAULT_EFFECTS,//"basalt_deltas",
                DEFAULT_EFFECTS,//"beach",
                DEFAULT_EFFECTS,//"birch_forest",
                DEFAULT_EFFECTS,//"cold_ocean",
                DEFAULT_EFFECTS,//"crimson_forest",
                DARK_FOREST_EFFECTS,//"dark_forest",
                DEFAULT_EFFECTS,//"deep_cold_ocean",
                DEFAULT_EFFECTS,//"deep_frozen_ocean",
                DEFAULT_EFFECTS,//"deep_lukewarm_ocean",
                DEFAULT_EFFECTS,//"deep_ocean",
                DEFAULT_EFFECTS,//"desert",
                DEFAULT_EFFECTS,//"dripstone_caves",
                DEFAULT_EFFECTS,//"end_barrens",
                DEFAULT_EFFECTS,//"end_highlands",
                DEFAULT_EFFECTS,//"end_midlands",
                BADLANDS_EFFECTS,//"eroded_badlands",
                DEFAULT_EFFECTS,//"flower_forest",
                DEFAULT_EFFECTS,//"forest",
                DEFAULT_EFFECTS,//"frozen_ocean",
                DEFAULT_EFFECTS,//"frozen_peaks",
                DEFAULT_EFFECTS,//"frozen_river",
                DEFAULT_EFFECTS,//"grove",
                DEFAULT_EFFECTS,//"ice_spikes",
                DEFAULT_EFFECTS,//"jagged_peaks",
                DEFAULT_EFFECTS,//"jungle",
                DEFAULT_EFFECTS,//"lukewarm_ocean",
                DEFAULT_EFFECTS,//"lush_caves",
                DEFAULT_EFFECTS,//"meadow",
                DEFAULT_EFFECTS,//"mushroom_fields",
                DEFAULT_EFFECTS,//"nether_wastes",
                DEFAULT_EFFECTS,//"ocean",
                DEFAULT_EFFECTS,//"old_growth_birch_forest",
                DEFAULT_EFFECTS,//"old_growth_pine_taiga",
                DEFAULT_EFFECTS,//"old_growth_spruce_taiga",
                DEFAULT_EFFECTS,//"plains",
                DEFAULT_EFFECTS,//"river",
                DEFAULT_EFFECTS,//"savanna",
                DEFAULT_EFFECTS,//"savanna_plateau",
                DEFAULT_EFFECTS,//"small_end_islands",
                DEFAULT_EFFECTS,//"snowy_beach",
                DEFAULT_EFFECTS,//"snowy_plains",
                DEFAULT_EFFECTS,//"snowy_slopes",
                DEFAULT_EFFECTS,//"snowy_taiga",
                DEFAULT_EFFECTS,//"soul_sand_valley",
                DEFAULT_EFFECTS,//"sparse_jungle",
                DEFAULT_EFFECTS,//"stony_peaks",
                DEFAULT_EFFECTS,//"stony_shore",
                DEFAULT_EFFECTS,//"sunflower_plains",
                SWAMP_EFFECTS,//"swamp",
                DEFAULT_EFFECTS,//"taiga",
                DEFAULT_EFFECTS,//"the_end",
                DEFAULT_EFFECTS,//"the_void",
                DEFAULT_EFFECTS,//"warm_ocean",
                DEFAULT_EFFECTS,//"warped_forest",
                DEFAULT_EFFECTS,//"windswept_forest",
                DEFAULT_EFFECTS,//"windswept_gravelly_hills",
                DEFAULT_EFFECTS,//"windswept_hills",
                DEFAULT_EFFECTS,//"windswept_savanna",
                BADLANDS_EFFECTS,//"wooded_badlands"
        };

        int i = 0;
        for (var b : biomeIds) {
            var biome = Biome.builder()
                    .name(NamespaceID.from(NamespaceID.MINECRAFT_NAMESPACE, b))
                    .category(categories[i])
                    .temperature(temperatures[i])
                    .downfall(downfalls[i])
                    .effects(DEFAULT_EFFECTS)
                    .build();
            map.put(b, biome);
            ++i;
        }
    }

    public static void registerBiomes() {
        for (var biome : map.values()) {
            MinecraftServer.getBiomeManager().addBiome(biome);
        }
    }

    public static Map<String, Biome> getMap() {
        return map;
    }
}
