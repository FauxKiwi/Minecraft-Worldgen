package deepslate.noise;

import deepslate.random.RandomSource;
import deepslate.worldgen.biome.Octaves;

public class NormalNoise {
    private static final double INPUT_FACTOR = 1.0181268882175227;

    private final PerlinNoise first;
    private final PerlinNoise second;
    private final double valueFactor;

    public NormalNoise(RandomSource random, Octaves parameters) {
        first = new PerlinNoise(random, parameters);
        second = new PerlinNoise(random, parameters);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        {
            int i = 0;
            for (double a : parameters.amplitudes()) {
                if (a != 0.0) {
                    min = Math.min(min, i);
                    max = Math.max(max, i);
                }
                ++i;
            }
        }

        valueFactor = (1.0 / 6.0) / (0.1 * (1.0 + 1.0 / (double) (max - min + 1)));
    }

    public double sample(double x, double y, double z) {
        double xx = x * INPUT_FACTOR, yy = y * INPUT_FACTOR, zz = z * INPUT_FACTOR;
        double first = this.first.sample(x, y, z, 0.0, 0.0, false);
        double second = this.second.sample(xx, yy, zz, 0.0, 0.0, false);
        return (first + second) * valueFactor;
    }
}

/*
	pub fn sample(&self, x: f64, y: f64, z: f64) -> f64 {
		let xx = x * Self::INPUT_FACTOR;
		let yy = y * Self::INPUT_FACTOR;
		let zz = z * Self::INPUT_FACTOR;
		let first = self.first.sample(x, y, z, 0.0, 0.0, false);
		let second = self.second.sample(xx, yy, zz, 0.0, 0.0, false);
		(first + second) * self.value_factor
	}
}
 */