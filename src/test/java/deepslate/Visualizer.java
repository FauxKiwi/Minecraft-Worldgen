package deepslate;

import deepslate.worldgen.NoiseSampler;
import deepslate.worldgen.NoiseSettings;
import deepslate.worldgen.biome.MultiOctaves;
import deepslate.worldgen.biome.Octaves;
import deepslate.worldgen.biome.Point;
import deepslate.worldgen.biome.TerrainShaper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Visualizer extends JFrame {
    private final Canvas canvas;

    private final BufferedImage image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
    private final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    public Visualizer() {
        setSize(1280, 720);
        setResizable(false);
        setTitle("MultiNoise");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        canvas = new Canvas();
        canvas.setSize(getSize());
        add(canvas);

        setVisible(true);

        startRenderThread();
    }

    private void startRenderThread() {
        new Thread(() -> {
            while (true) {
                synchronized (canvas) {
                    canvas.getGraphics().drawImage(image, 0, 0, null);
                }
                try {
                    Thread.sleep(1000 / 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setPixel(int x, int y, int color) {
        synchronized (canvas) {
            pixels[y * 1280 + x] = color;
        }
    }

    public static void main(String[] args) {
        var visualizer = new Visualizer();

        var seed = 0;

        var overworldOctaves = new MultiOctaves(
                new Octaves(-9, new double[]{1.5, 0, 1, 0, 0, 0}), // temperature
                new Octaves(-7, new double[]{1, 1, 0, 0, 0, 0}), // humidity
                new Octaves(-9, new double[]{1, 1, 2, 2, 2, 1, 1, 1, 1}), // continentalness
                new Octaves(-9, new double[]{1, 1, 0, 1, 1}), // erosion
                new Octaves(-7, new double[]{1, 2, 1, 0, 0, 0}), // weirdness
                new Octaves(-3, new double[]{1, 1, 1, 0}) // shift
        );

        var shaper = TerrainShaper.overworld();
        //var sampler = new NoiseSampler(4, 4, 32, NoiseSettings.fromJson(null), overworldOctaves, seed);

        visualizer.setTitle("Loading...");

        var x_ = deepslate.noise.Sampler.fromOctaves(seed, overworldOctaves);

        for (int y = 0; y < 710 / 2; ++y) {
            for (int x = 0; x < 1280 / 2; ++x) {
                double wx = x / 4.0 * 16, wy = y / 4.0 * 16;

                //visualizer.setPixel(x, y, -1);
                Point point;// = TerrainShaper.point(sampler.getContinentalness(x*6, y*6), sampler.getErosion(x*6, y*6), sampler.getWeirdness(x*6, y*6));
                point = TerrainShaper.point(x_.continentalness().sample(wx, 0, wy),x_.erosion().sample(wx, 0, wy),x_.weirdness().sample(wx, 0, wy));

                double offset = shaper.offset(point);
                double factor = shaper.factor(point);
                double jaggedness = shaper.jaggedness(point);
                double height = (offset - TerrainShaper.GLOBAL_OFFSET) * 32 * factor + 64 + jaggedness;

                //value = x_.continentalness().sample(x, 0, y);

                visualizer.setPixel(x, y, color(offset, -1, 1, TerrainShaper.GLOBAL_OFFSET));
                visualizer.setPixel(x + 1280 / 2, y, color(factor, 0, 16, 3.951));
                visualizer.setPixel(x, y + 710 / 2, color(jaggedness, 0, 1, 0));
                visualizer.setPixel(x + 1280 / 2, y + 710 / 2, color(height, 0, 256, 64));

                // 0xff << 24 | 0x010101 * (int) (0xff * ((n + 1) / 2.0));
            }
        }

        /*var s_ = TerrainShaper.overworld();
        for (int x = 0; x < 1280; ++x) {
            double value = s_.offset(TerrainShaper.point(x / 1280.0 * 2.1 - 1.1, 0.5, 0.5));
            for (int y = 0; y < value + 0.5 * 720; ++y) {
                if (y >= 720) break;
                visualizer.setPixel(x, y, -1);
            }
        }*/

        visualizer.setTitle("Done.");
    }

    private static int color(double value, double min, double max, double highlight) {
        int c = 0xff << 24 | 0x010101 * (int) (0xff * ((value - min) / (max - min)));
        if (value >= highlight) return c & 0xff_ff0000;
        else return c & 0xff_0000ff;
        //return c;
    }
}
