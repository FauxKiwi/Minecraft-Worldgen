package deepslate.worldgen;

import com.google.gson.JsonObject;
import deepslate.worldgen.biome.TerrainShaper;

public record NoiseSettings(
        int minY,
        int height,
        double xzSize,
        double ySize,
        NoiseSamplingSettings sampling,
        NoiseSlideSettings topSlide,
        NoiseSlideSettings bottomSlide,
        TerrainShaper terrainShaper
) {
    public static NoiseSettings fromJson(JsonObject obj) {
        var root = obj == null ? new JsonObject() : obj;
        return new NoiseSettings(
                root.has("min_y") ? root.get("min_y").getAsInt() : 0,
                root.has("height") ? root.get("height").getAsInt() : 256,
                root.has("size_horizontal") ? root.get("size_horizontal").getAsDouble() : 1,
                root.has("size_vertical") ? root.get("size_vertical").getAsDouble() : 1,
                NoiseSamplingSettings.fromJson(root.getAsJsonObject("sampling")),
                NoiseSlideSettings.fromJson(root.getAsJsonObject("top_slide")),
                NoiseSlideSettings.fromJson(root.getAsJsonObject("bottom_slide")),
                TerrainShaper.fromJson(root.getAsJsonObject("terrain_shaper"))
        );
    }
}

/*
	export function cellHeight(settings: NoiseSettings) {
		return settings.ySize << 2
	}

	export function cellWidth(settings: NoiseSettings) {
		return settings.xzSize << 2
	}

	export function cellCountY(settings: NoiseSettings) {
		return settings.height / cellHeight(settings)
	}

	export function minCellY(settings: NoiseSettings) {
		return Math.floor(settings.minY / cellHeight(settings))
	}

	export function applySlides(settings: NoiseSettings, density: number, y: number) {
		const yCell = Math.floor(y / cellHeight(settings)) - NoiseSettings.minCellY(settings)
		density = NoiseSlideSettings.apply(settings.topSlide, density, NoiseSettings.cellCountY(settings) - yCell)
		density = NoiseSlideSettings.apply(settings.bottomSlide, density, yCell)
		return density
	}
}
 */