package deepslate.noise;

import deepslate.random.RandomSource;
import deepslate.worldgen.biome.Octaves;

import static deepslate.math.Util.wrap;

public class PerlinNoise {
    private final double[] amplitudes;
    private final ImprovedNoise[] noises;
    private final double lowestFreqInput;
    private final double lowestFreqValue;

    public PerlinNoise(RandomSource random, Octaves parameters) {
        int n = parameters.amplitudes().length; // let n = params.amplitudes.len() as i32;
        assert 1 - parameters.firstOctave() >= n : "Positive octaves are disabled"; // assert!(1 - params.first_octave >= n, "Positive octaves are disabled");

        amplitudes = new double[n]; // let mut levels = Vec::with_capacity(n as usize);
        noises = new ImprovedNoise[n];

        for (int i = -parameters.firstOctave(); i >= 0; --i) { //for i in (0..=-params.first_octave).rev() {
            if (i < n && parameters.amplitudes()[i] != 0.0) { // if i < n && params.amplitudes[i as usize] != 0.0 {
                amplitudes[i] = parameters.amplitudes()[i]; // levels[i as usize] = Some((params.amplitudes[i as usize],
                noises[i] = new ImprovedNoise(random); // ImprovedNoise::new(random)));
            } else {
                random.consume(262); // random.consume(262);
            }
        }

        lowestFreqInput = Double.longBitsToDouble((1023L + parameters.firstOctave()) << 52); // lowest_freq_input: (2 as f64).powi(params.first_octave),
        lowestFreqValue = Double.longBitsToDouble((1023L + n - 1) << 52)
                / (Double.longBitsToDouble((1023L + n) << 52) - 1.0); // lowest_freq_value: (2 as f64).powi(n - 1) / ((2 as f64).powi(n) - 1.0),
    }

    public double sample(double x, double y, double z, double yScale, double yLimit, boolean fixY) {
        double value = 0.0; // let mut value = 0.0;
        double inputFactor = lowestFreqInput, valueFactor = lowestFreqValue; // let mut input_factor = self.lowest_freq_input; let mut value_factor = self.lowest_freq_value;
        //System.out.println("" + inputFactor + " || " + valueFactor);

        for (int i = 0; i < amplitudes.length; ++i) { // for i in 0..self.levels.len() {
            ImprovedNoise level = noises[i];
            if (level != null) { // if let Some((amplitude, level)) = &self.levels[i as usize] {
                double amplitude = amplitudes[i];

                var noise = level.sample( // let noise = level.sample(
                        wrap(x * inputFactor), // util::wrap(x * input_factor),
                        fixY ? -level.yo() : wrap(y * inputFactor), // if fix_y { -level.yo } else { util::wrap(y * input_factor) },
                        wrap(z * inputFactor), // util::wrap(z * input_factor),
                        yScale * inputFactor, // y_scale * input_factor,
                        yLimit * inputFactor // y_limit * input_factor,
                );
                value += amplitude * valueFactor * noise; // value += amplitude * value_factor * noise;
            }
            inputFactor *= 2.0; // input_factor *= 2.0;
            valueFactor /= 2.0; // value_factor /= 2.0;
        }
        return value; // value
    }
}