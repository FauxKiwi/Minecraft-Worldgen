package deepslate.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BiomeConfig {
    private final Biome[] biomes;

    public Biome[] getBiomes() {
        return biomes;
    }

    /*public Biome biome(Biome last, double temperature, double humidity, double continentalness, double erosion, double weirdness) {
        while (last != null) {
            if (temperature < last.temperature0) break;
            if (temperature >= last.temperature1) break;
            if (humidity < last.humidity0) break;
            if (humidity >= last.humidity1) break;
            if (continentalness < last.continentalness0) break;
            if (continentalness >= last.continentalness1) break;
            if (erosion < last.erosion0) break;
            if (erosion >= last.erosion1) break;
            if (weirdness < last.weirdness0) break;
            if (weirdness >= last.weirdness1) break;
            return last;
        }
        return biomeRaw(temperature, humidity, continentalness, erosion, weirdness);
    }

    public String biome(double temperature, double humidity, double continentalness, double erosion, double weirdness) {
        var b = biomeRaw(temperature, humidity, continentalness, erosion, weirdness);
        if (b == null) return null;
        return b.name;
    }

    public Biome biomeRaw(double temperature, double humidity, double continentalness, double erosion, double weirdness) {
        for (Biome biome : biomes) {
            if (temperature < biome.temperature0) continue;
            if (temperature >= biome.temperature1) continue;
            if (humidity < biome.humidity0) continue;
            if (humidity >= biome.humidity1) continue;
            if (continentalness < biome.continentalness0) continue;
            if (continentalness >= biome.continentalness1) continue;
            if (erosion < biome.erosion0) continue;
            if (erosion >= biome.erosion1) continue;
            if (weirdness < biome.weirdness0) continue;
            if (weirdness >= biome.weirdness1) continue;
            return biome;
        }
        return null;
    }*/

    public BiomeConfig(File file) throws IOException {
        this(JsonParser.parseReader(new FileReader(file)).getAsJsonObject());
        //System.out.println(Arrays.toString(biomes));
    }

    public BiomeConfig(JsonObject root) {
        var biomes = root.getAsJsonObject("generator").getAsJsonObject("biome_source").getAsJsonArray("biomes");
        int nBiomes = biomes.size();

        this.biomes = new Biome[nBiomes];
        for (int b = 0; b < nBiomes; ++b) {
            var name = biomes.get(b).getAsJsonObject().get("biome").getAsString();
            var parameters = biomes.get(b).getAsJsonObject().getAsJsonObject("parameters");

            var temperature = parameters.get("temperature");
            var humidity = parameters.get("humidity");
            var continentalness = parameters.get("continentalness");
            var erosion = parameters.get("erosion");
            var weirdness = parameters.get("weirdness");
            var depth = parameters.get("depth");
            var offset = parameters.get("offset").getAsDouble();

            this.biomes[b] = new Biome(
                    name,
                    temperature.isJsonPrimitive() ? temperature.getAsDouble() : temperature.getAsJsonArray().get(0).getAsDouble(),
                    temperature.isJsonPrimitive() ? temperature.getAsDouble() : temperature.getAsJsonArray().get(1).getAsDouble(),
                    humidity.isJsonPrimitive() ? humidity.getAsDouble() : humidity.getAsJsonArray().get(0).getAsDouble(),
                    humidity.isJsonPrimitive() ? humidity.getAsDouble() : humidity.getAsJsonArray().get(1).getAsDouble(),
                    continentalness.isJsonPrimitive() ? continentalness.getAsDouble() : continentalness.getAsJsonArray().get(0).getAsDouble(),
                    continentalness.isJsonPrimitive() ? continentalness.getAsDouble() : continentalness.getAsJsonArray().get(1).getAsDouble(),
                    erosion.isJsonPrimitive() ? erosion.getAsDouble() : erosion.getAsJsonArray().get(0).getAsDouble(),
                    erosion.isJsonPrimitive() ? erosion.getAsDouble() : erosion.getAsJsonArray().get(1).getAsDouble(),
                    weirdness.isJsonPrimitive() ? weirdness.getAsDouble() : weirdness.getAsJsonArray().get(0).getAsDouble(),
                    weirdness.isJsonPrimitive() ? weirdness.getAsDouble() : weirdness.getAsJsonArray().get(1).getAsDouble(),
                    depth.isJsonPrimitive() ? depth.getAsDouble() : depth.getAsJsonArray().get(0).getAsDouble(),
                    depth.isJsonPrimitive() ? depth.getAsDouble() : depth.getAsJsonArray().get(1).getAsDouble(),
                    offset
            );
        }
    }

    public record Biome(
            String name,
            double temperature0,
            double temperature1,
            double humidity0,
            double humidity1,
            double continentalness0,
            double continentalness1,
            double erosion0,
            double erosion1,
            double weirdness0,
            double weirdness1,
            double depth0,
            double depth1,
            double offset
    ) {
    }
}
