package deepslate.noise;

import deepslate.random.RandomSource;

import static deepslate.math.Util.*;

public class ImprovedNoise {
    private double xo, yo, zo;
    private short[] p;

    public ImprovedNoise(RandomSource random) {
        xo = random.nextDouble() * 256.0;
        yo = random.nextDouble() * 256.0;
        zo = random.nextDouble() * 256.0;

        p = new short[256];
        for (int i = 0; i < 256; ++i) {
            p[i] = (short) i;
        }
        for (int i = 0; i < 256; ++i) {
            int j = random.nextIntMax(256 - i);
            short temp = p[i];
            p[i] = p[i + j];
            p[i + j] = temp;
        }
    }

    public double sample(double x, double y, double z, double yScale, double yLimit) {
        double x2 = x + xo, y2 = y + yo, z2 = z +zo;
        double x3 = Math.floor(x2), y3 = Math.floor(y2), z3 = Math.floor(z2);
        double x4 = x2 - x3, y4 = y2 - y3, z4 = z2 - z3;

        double y6 = 0.0;
        if (yScale != 0.0) {
            double t = yLimit >= 0.0 && yLimit < y4 ? yLimit : y4;
            y6 = Math.floor(t / yScale + 1.0e-7);
        }

        return sampleAndLerp((int) x3, (int) y3, (int) z3, x4, y4 - y6, z4, y4);
    }

    private double sampleAndLerp(int a, int b, int c, double d, double e, double f, double g) {
        int h = p(a), i = p(a + 1), j = p(h + b), k = p(h + b + 1), l = p(i + b), m = p(i + b + 1);

        double n = gradDot(p(j + c), d, e, f);
        double o = gradDot(p(l + c), d - 1.0, e, f);
        double p = gradDot(p(k + c), d, e - 1.0, f);
        double q = gradDot(p(m + c), d - 1.0, e - 1.0, f);
        double r = gradDot(p(j + c + 1), d, e, f - 1.0);
        double s = gradDot(p(l + c + 1), d - 1.0, e, f - 1.0);
        double t = gradDot(p(k + c + 1), d, e - 1.0, f - 1.0);
        double u = gradDot(p(m + c + 1), d - 1.0, e - 1.0, f - 1.0);

        double v = smoothstep(d);
        double w = smoothstep(g);
        double x = smoothstep(f);

        return lerp3(v, w, x, n, o, p, q, r, s, t, u);
    }

    private int p(int i) {
        return p[i & 255];
    }

    public double yo() {
        return yo;
    }
}