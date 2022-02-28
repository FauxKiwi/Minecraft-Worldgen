package deepslate.math;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;

public class Util {
    public static double lerp(double a, double b, double c) {
        return b + a * (c - b);
    }

    public static double lerp2(double a, double b, double c, double d, double e, double f) {
        return lerp(b, lerp(a, c, d), lerp(a, e, f));
    }

    public static double lerp3(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double k) {
        return lerp(c, lerp2(a, b, d, e, f, g), lerp2(a, b, h, i, j, k));
    }

    public static int binarySearch(int n, int n2, Int2BooleanFunction predicate) {
        int n3 = n2 - n;
        while (n3 > 0) {
            int n4 = n3 / 2;
            int n5 = n + n4;
            if (predicate.get(n5)) {
                n3 = n4;
                continue;
            }
            n = n5 + 1;
            n3 -= n4 + 1;
        }
        return n;
    }

    public static double smoothstep(double x) {
        return x * x * x * (x * (x * 6.0 - 15.0) + 10.0);
    }

    public static double[] GRADIENT = {1.0, 1.0, 0.0, -1.0, 1.0, 0.0, 1.0, -1.0, 0.0, -1.0, -1.0, 0.0, 1.0, 0.0, 1.0, -1.0, 0.0, 1.0, 1.0, 0.0, -1.0, -1.0, 0.0, -1.0, 0.0, 1.0, 1.0, 0.0, -1.0, 1.0, 0.0, 1.0, -1.0, 0.0, -1.0, -1.0, 1.0, 1.0, 0.0, 0.0, -1.0, 1.0, -1.0, 1.0, 0.0, 0.0, -1.0, -1.0};

    public static double gradDot(int a, double b, double c, double d) {
        double grad0 = GRADIENT[a & 15];
        double grad1 = GRADIENT[(a & 15) + 1];
        double grad2 = GRADIENT[(a & 15) + 2];
        return grad0 * b + grad1 * c + grad2 * d;
    }

    public static double wrap(double value) {
        return value - Math.floor(value / 3.3554432e7 + 0.5) * 3.3554432e7;
    }
}

/*
export function square(x: number) {
	return x * x
}

export function clamp(x: number, min: number, max: number) {
	return Math.max(min, Math.min(max, x))
}

export function lerp(a: number, b: number, c: number): number {
	return b + a * (c - b)
}

export function lerp2(a: number, b: number, c: number, d: number, e: number, f: number): number {
	return lerp(b, lerp(a, c, d), lerp(a, e, f))
}

export function lerp3(a: number, b: number, c: number, d: number, e: number, f: number, g: number, h: number, i: number, j: number, k: number) {
	return lerp(c, lerp2(a, b, d, e, f, g), lerp2(a, b, h, i, j, k))
}

export function clampedLerp(a: number, b: number, c: number): number {
	if (c < 0) {
		return a
	} else if (c > 1) {
		return b
	} else {
		return lerp(c, a, b)
	}
}

export function inverseLerp(a: number, b: number, c: number) {
	return (a - b) / (c - b)
}

export function smoothstep(x: number): number {
	return x * x * x * (x * (x * 6 - 15) + 10)
}

export function map(a: number, b: number, c: number, d: number, e: number) {
	return lerp(inverseLerp(a, b, c), d, e)
}

export function clampedMap(a: number, b: number, c: number, d: number, e: number) {
	return clampedLerp(d, e, inverseLerp(a, b, c))
}

export function binarySearch(n: number, n2: number, predicate: (value: number) => boolean) {
	let n3 = n2 - n
	while (n3 > 0) {
		const n4 = Math.floor(n3 / 2)
		const n5 = n + n4
		if (predicate(n5)) {
			n3 = n4
			continue
		}
		n = n5 + 1
		n3 -= n4 + 1
	}
	return n
}

export function getSeed(x: number, y: number, z: number) {
	let seed = BigInt(x * 3129871) ^ BigInt(z) * BigInt(116129781) ^ BigInt(y)
	seed = seed * seed * BigInt(42317861) + seed * BigInt(11)
	return seed >> BigInt(16)
}

export function longfromBytes(a: number, b: number, c: number, d: number, e: number, f: number, g: number, h: number): bigint {
	return BigInt(a) << BigInt(56)
		| BigInt(b) << BigInt(48)
		| BigInt(c) << BigInt(40)
		| BigInt(d) << BigInt(32)
		| BigInt(e) << BigInt(24)
		| BigInt(f) << BigInt(16)
		| BigInt(g) << BigInt(8)
		| BigInt(h)
}
 */