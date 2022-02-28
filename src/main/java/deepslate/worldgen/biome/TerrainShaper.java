package deepslate.worldgen.biome;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import deepslate.math.CubicSpline;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import static deepslate.math.Util.lerp;

public class TerrainShaper {
    //public static final String[] COORDINATES = {"continents", "erosion", "weirdness", "ridges"};

    public static final double GLOBAL_OFFSET = -0.50375;

    public static final ToDoubleFunction<Point> CONTINENTS = Point::continents;
    public static final ToDoubleFunction<Point> EROSION = Point::erosion;
    public static final ToDoubleFunction<Point> WEIRDNESS = Point::weirdness;
    public static final ToDoubleFunction<Point> RIDGES = Point::ridges;

    private static final Function<String, ToDoubleFunction<Point>> EXTRACTOR = coordinate -> {
        var key = coordinate;//JsonParser.parseString(coordinate);
        //if (key == null) key = "continents";
        return switch (key) {
            case "continents" -> CONTINENTS;
            case "erosion" -> EROSION;
            case "weirdness" -> WEIRDNESS;
            case "ridges" -> RIDGES;
            default -> null;
        };
    };

    private final CubicSpline<Point> offsetSampler;
    private final CubicSpline<Point> factorSampler;
    private final CubicSpline<Point> jaggednessSampler;

    public TerrainShaper(CubicSpline<Point> offsetSampler, CubicSpline<Point> factorSampler, CubicSpline<Point> jaggednessSampler) {
        this.offsetSampler = offsetSampler;
        this.factorSampler = factorSampler;
        this.jaggednessSampler = jaggednessSampler;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static TerrainShaper fromJson(JsonObject obj) {
        if (obj == null) return null;
        return new TerrainShaper(
                (CubicSpline) CubicSpline.fromJson(obj.get("offset"), EXTRACTOR),
                (CubicSpline) CubicSpline.fromJson(obj.get("factor"), EXTRACTOR),
                (CubicSpline) CubicSpline.fromJson(obj.get("jaggedness"), EXTRACTOR)
        );
    }

    /*
    public static fromJson(obj: unknown): TerrainShaper {
		const root = Json.readObject(obj) ?? {}
		return new TerrainShaper(
			CubicSpline.fromJson(root.offset, this.EXTRACTOR),
			CubicSpline.fromJson(root.factor, this.EXTRACTOR),
			CubicSpline.fromJson(root.jaggedness, this.EXTRACTOR),
		)
	}
     */

    public double offset(Point point) {
        return offsetSampler.applyAsDouble(point) + GLOBAL_OFFSET;
    }

    public double factor(Point point) {
        return factorSampler.applyAsDouble(point);
    }

    public double jaggedness(Point point) {
        return jaggednessSampler.applyAsDouble(point);
    }

    public static Point point(double continents, double erosion, double weirdness) {
        return new Point(
                continents,
                erosion,
                weirdness,
                peaksAndValleys(weirdness)
        );
    }

    public static double peaksAndValleys(double weirdness) {
        return -(Math.abs(Math.abs(weirdness) - 0.6666667) - 0.33333334) * 3.0;
    }

    public static TerrainShaper overworld() {
        var t = buildErosionOffsetSpline("beachSpline", -.05, 0, 0, .1, 0, -.03, false, false);
        var e = buildErosionOffsetSpline("lowSpline", -.1, .03, .1, .1, .01, -.03, false, false);
        var i = buildErosionOffsetSpline("midSpline", -.1, .03, .1, .7, .01, -.03, true, true);
        var s = buildErosionOffsetSpline("highSpline", .3, .03, .1, 1, .01, .01, true, true);
        var offsetSampler = new CubicSpline.MultiPoint<>(/*"Offset", */Point::continents).addPoint(-1.1, .044).addPoint(-1.02, -.2222).addPoint(-.51, -.2222).addPoint(-.44, -.12).addPoint(-.18, -.12).addPoint(-.16, t).addPoint(-.15, t).addPoint(-.1, e).addPoint(.25, i).addPoint(1, s);
        var factorSampler = new CubicSpline.MultiPoint<>(/*"Factor", */Point::continents).addPoint(-.19, 3.95).addPoint(-.15, getErosionFactor("erosionCoast", 6.25, true)).addPoint(-.1, getErosionFactor("erosionInland", 5.47, true)).addPoint(.03, getErosionFactor("erosionMidInland", 5.08, true)).addPoint(.06, getErosionFactor("erosionFarInland", 4.69, false));
        var jaggednessSampler = new CubicSpline.MultiPoint<>(/*"Jaggedness", */Point::continents).addPoint(.11, 0).addPoint(.03, buildErosionJaggednessSpline(1, .5, 0, 0)).addPoint(.65, buildErosionJaggednessSpline(1, 1, 1, 0));
        return new TerrainShaper(offsetSampler, factorSampler, jaggednessSampler);
    }

    private static CubicSpline getErosionFactor(String t, double e, boolean i) {
        var s = new CubicSpline.MultiPoint<>(/*"weirdness", */Point::weirdness).addPoint(-.2, 6.3).addPoint(.2, e);
        var n = new CubicSpline.MultiPoint<>(/*t, */Point::erosion).addPoint(-.6, s).addPoint(-.5,
                new CubicSpline.MultiPoint<>(/*"weirdness", */Point::weirdness).addPoint(-.05, 6.3).addPoint(.05, 2.67)).addPoint(-.35, s).addPoint(-.25, s).addPoint(-.1,
                new CubicSpline.MultiPoint<>(/*"weirdness", */Point::weirdness).addPoint(-.05, 2.67).addPoint(.05, 6.3)).addPoint(.03, s);
        if (i) {
            var t0 = new CubicSpline.MultiPoint<>(/*"weirdnessShattered", */Point::weirdness).addPoint(0, e).addPoint(.1, .625);
            var i0 = new CubicSpline.MultiPoint<>(/*"ridgesShattered", */Point::ridges).addPoint(-.9, e).addPoint(-.69, t0);
            n.addPoint(.35, e).addPoint(.45, i0).addPoint(.55, i0).addPoint(.62, e);
        } else {
            var t0 = new CubicSpline.MultiPoint<>(/*"ridges", */Point::ridges).addPoint(-.7, s).addPoint(-.15, 1.37);
            var i0 = new CubicSpline.MultiPoint<>(/*"ridges", */Point::ridges).addPoint(.45, s).addPoint(.7, 1.56);
            n.addPoint(.05, i0).addPoint(.4, i0).addPoint(.45, t0).addPoint(.55, t0).addPoint(.58, e);
        }
        return n;
    }

    private static CubicSpline buildErosionOffsetSpline(String id, double e, double i, double s, double n, double r, double o, boolean a, boolean l) {
        var h = buildMountainRidgeSplineWithPoints(lerp(n, .6, 1.5), l);
        var u = buildMountainRidgeSplineWithPoints(lerp(n, .6, 1), l);
        var c = buildMountainRidgeSplineWithPoints(n, l);
        var d = ridgeSpline(id + "-widePlateau", e - .15, .5 * n, lerp(.5, .5, .5) * n, .5 * n, .6 * n, .5);
        var f = ridgeSpline(id + "-narrowPlateau", e, r * n, i * n, .5 * n, .6 * n, .5);
        var g = ridgeSpline(id + "-plains", e, r, r, i, s, .5);
        var p = ridgeSpline(id + "-plainsFarInland", e, r, r, i, s, .5);
        var m = new CubicSpline.MultiPoint<>(/*id, */Point::ridges).addPoint(-1, e).addPoint(-.4, g).addPoint(0, s + .07);
        var v = ridgeSpline(id + "-swamps", -.02, o, o, i, s, 0);
        var b = new CubicSpline.MultiPoint<>(/*id, */Point::erosion).addPoint(-.85, h).addPoint(-.7, u).addPoint(-.4, c).addPoint(-.35, d).addPoint(-.1, f).addPoint(.2, g);
        //return a && b.addPoint(.4, p).addPoint(.45, m).addPoint(.55, m).addPoint(.58, p), b.addPoint(.7, v), b
        if (a) b.addPoint(.4, p).addPoint(.45, m).addPoint(.55, m).addPoint(.58, p);
        b.addPoint(.7, v);
        return b;
    }

    private static CubicSpline buildMountainRidgeSplineWithPoints(double t, boolean e) {
        var i = new CubicSpline.MultiPoint<>(/*`M-spline for continentalness: ${t} ${e}`, */Point::ridges);
        var s = mountainContinentalness(-1, t, -.7);
        var n = mountainContinentalness(1, t, -.7);
        var r = calculateMountainRidgeZeroContinentalnessPoint(t);
        if (-.65 < r && r < 1) {
            var e0 = mountainContinentalness(-.65, t, -.7);
            var o = mountainContinentalness(-.75, t, -.7);
            var a = calculateSlope(s, o, -1, -.75);
            i.addPoint(-1, s, a);
            i.addPoint(-.75, o);
            i.addPoint(-.65, e0);
            var l = mountainContinentalness(r, t, -.7);
            var h = calculateSlope(l, n, r, 1);
            i.addPoint(r - .01, l);
            i.addPoint(r, l, h);
            i.addPoint(1, n, h);
        } else {
            var t0 = calculateSlope(s, n, -1, 1);
            if (e) {
                i.addPoint(-1, Math.max(.2, s));
                i.addPoint(0, lerp(.5, s, n), t0);
            } else {
                i.addPoint(-1, s, t0);
                i.addPoint(1, n, t0);
            }
        }
        return i;
    }

    private static double mountainContinentalness(double t, double e, double i) {
        var s = .46082947 * (t + 1.17) * (1 - .5 * (1 - e)) - .5 * (1 - e);
        return t < i ? Math.max(s, -.2222) : Math.max(s, 0);
    }

    private static double calculateMountainRidgeZeroContinentalnessPoint(double t){
        return .5 * (1 - t) / (.46082947 * (1 - .5 * (1 - t))) - 1.17;
    }

    private static double calculateSlope(double t, double e, double i, double s) {
        return (e - t) / (s - i);
    }

    private static CubicSpline ridgeSpline(String t, double e, double i, double s, double n, double r, double o) {
        var a = Math.max(.5 * (i - e), o);
        var l = 5 * (s - i);
        return new CubicSpline.MultiPoint<>(/*t, */Point::ridges).addPoint(-1, e, a).addPoint(-.4, i, Math.min(a, l)).addPoint(0, s, l).addPoint(.4, n, 2 * (n - s)).addPoint(1, r, .7 * (r - n));
    }

    private static CubicSpline buildErosionJaggednessSpline(double t, double e, double i, double s) {
        var n = buildRidgeJaggednessSpline(t, i);
        var r = buildRidgeJaggednessSpline(e, s);
        return new CubicSpline.MultiPoint<>(/*"Jaggedness-erosion", */Point::erosion).addPoint(-1, n).addPoint(-.78, r).addPoint(-.5775, r).addPoint(-.375, 0);
    }

    private static CubicSpline buildRidgeJaggednessSpline(double t, double e) {
        var i = TerrainShaper.peaksAndValleys(.4);
        var s = (i + TerrainShaper.peaksAndValleys(.56666666)) / 2;
        var spline = new CubicSpline.MultiPoint<>(/*"Jaggedness-ridges", */Point::ridges).addPoint(i, 0);
        //.addPoint(s, e > 0 ? buildWeirdnessJaggednessSpline(e) : 0).addPoint(1, t > 0 ? buildWeirdnessJaggednessSpline(t) : 0);
        if (e > 0) spline.addPoint(s, buildWeirdnessJaggednessSpline(e));
        else spline.addPoint(s, 0);

        if (t > 0) spline.addPoint(1, buildWeirdnessJaggednessSpline(t));
        else spline.addPoint(1, 0);

        return spline;
    }

    public static CubicSpline buildWeirdnessJaggednessSpline(double t) {
        return new CubicSpline.MultiPoint<>(/*"Jaggedness-weirdness", */Point::weirdness).addPoint(-.01, .63 * t).addPoint(.01, .3 * t);
    }
}