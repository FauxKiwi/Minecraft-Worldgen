package deepslate.worldgen.biome;

public record MultiOctaves(
        Octaves temperature,
        Octaves humidity,
        Octaves continentalness,
        Octaves erosion,
        Octaves weirdness,
        Octaves shift
) {
}
