package deepslate.noise;

import deepslate.random.RandomSource;
import deepslate.worldgen.biome.MultiOctaves;

public record Sampler(
        NormalNoise temperature,
        NormalNoise humidity,
        NormalNoise continentalness,
        NormalNoise erosion,
        NormalNoise weirdness,
        NormalNoise offset
) {
    public static Sampler fromOctaves(long seed, MultiOctaves octaves) {
        return new Sampler(
                new NormalNoise(new RandomSource.Legacy(seed), octaves.temperature()),
                new NormalNoise(new RandomSource.Legacy(seed + 1), octaves.humidity()),
                new NormalNoise(new RandomSource.Legacy(seed + 2), octaves.continentalness()),
                new NormalNoise(new RandomSource.Legacy(seed + 3), octaves.erosion()),
                new NormalNoise(new RandomSource.Legacy(seed + 4), octaves.weirdness()),
                new NormalNoise(new RandomSource.Legacy(seed + 5), octaves.shift())
        );
    }
}
