package deepslate.random;

public interface RandomSource {
    void setSeed(long seed);
    void consume(int n);
    int nextInt();
    int nextIntMax(int max);
    long nextLong();
    float nextFloat();
    double nextDouble();

    class Legacy implements RandomSource {
        private static final int MODULUS_BITS = 48;
        private static final long MODULUS_MASK = 0xFFFFFFFFFFFFL;
        private static final long MULTIPLIER = 25214903917L;
        private static final long INCREMENT = 11L;
        private static final float FLOAT_MULTIPLIER = 5.9604645E-8f;
        private static final double DOUBLE_MULTIPLIER = 1.110223E-16;

        private long seed;

        public Legacy(long seed) {
            this.seed = initialSeed(seed);
        }

        private static long initialSeed(long seed) {
            return (seed ^ 0x5DEECE66DL) & MODULUS_MASK;
        }

        private int next(int n) {
            seed = ((seed * MULTIPLIER) + INCREMENT) & MODULUS_MASK;
            return (int) (seed >> MODULUS_BITS - n);
        }

        @Override
        public void setSeed(long seed) {
            seed = initialSeed(seed);
        }

        @Override
        public void consume(int n) {
            for (int i = 0; i < n; ++i) nextInt();
        }

        @Override
        public int nextInt() {
            return next(32);
        }

        @Override
        public int nextIntMax(int max) {
            assert max > 0;
            if ((max & max - 1) == 0) {
                return (int) ((long) max * (long) next(31) >> 31);
            }
            while (true) {
                int a = next(31);
                int b = a % max;
                if (a - b + max - 1 >= 0) return b;
            }
        }

        @Override
        public long nextLong() {
            long lo = next(32);
            long hi = next(32);
            return (lo << 32) + hi;
        }

        @Override
        public float nextFloat() {
            return (float) next(24) * FLOAT_MULTIPLIER;
        }

        @Override
        public double nextDouble() {
            long lo = next(26);
            long hi = next(27);
            return (double) ((lo << 27) + hi) * DOUBLE_MULTIPLIER;
        }
    }
}