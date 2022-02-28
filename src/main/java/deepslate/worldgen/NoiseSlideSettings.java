package deepslate.worldgen;

import com.google.gson.JsonObject;

public record NoiseSlideSettings(
        double target,
        double size,
        double offset
) {
    public static NoiseSlideSettings fromJson(JsonObject obj) {
        var root = obj == null ? new JsonObject() : obj;
        return new NoiseSlideSettings(
                root.has("target") ? root.get("target").getAsDouble() : 0,
                root.has("size") ? root.get("size").getAsDouble() : 0,
                root.has("offset") ? root.get("offset").getAsDouble() : 0
        );
    }
}

/*
	export function apply(slide: NoiseSlideSettings, density: number, y: number) {
		if (slide.size <= 0) return density
		const t = (y - slide.offset) / slide.size
		return clampedLerp(slide.target, density, t)
	}
}
 */