package deepslate.worldgen;

import deepslate.worldgen.biome.MultiOctaves;

public record NoiseSampler(
) {
    public NoiseSampler(int i, int i1, int i2, NoiseSettings fromJson, MultiOctaves overworldOctaves, int seed) {
        this();
    }

    public double getContinentalness(int i, int i1) {
        return 0;
    }

    public double getErosion(int i, int i1) {
        return 0;
    }

    public double getWeirdness(int i, int i1) {
        return 0;
    }
}
