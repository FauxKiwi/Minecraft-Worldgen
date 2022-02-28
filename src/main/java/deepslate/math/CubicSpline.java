package deepslate.math;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import deepslate.worldgen.biome.Point;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import static deepslate.math.Util.binarySearch;
import static deepslate.math.Util.lerp;

public interface CubicSpline<C> extends ToDoubleFunction<C> {
    double min();
    double max();
    CubicSpline<C> mapAll(CoordinateVisitor<C> visitor);

    @FunctionalInterface
    interface CoordinateVisitor<C> {
        ToDoubleFunction<C> visit(ToDoubleFunction<C> f);
    }

    @SuppressWarnings("unchecked")
    static <C> CubicSpline<Object> fromJson(JsonElement obj, Function<String, ToDoubleFunction<C>> extractor) {
        if (obj.isJsonPrimitive() && obj.getAsJsonPrimitive().isNumber()) {
            return new Constant(obj.getAsDouble());
        }
        var root = obj.getAsJsonObject();

        var spline = new MultiPoint<>(extractor.apply(root.get("coordinate").getAsString()));
        var points = root.getAsJsonArray("points");//Json.readArray(root.points, Json::readObject);
        if (/*points == null || */points.size() == 0) {
            return new Constant(0);
        }
        for (var pointElement : points) {
            var point = pointElement.getAsJsonObject();
            double location = point.has("location") ? point.get("location").getAsDouble() : 0.0;//Json.readNumber(point.location);
            //if (location == null) location = 0;
            var value = fromJson(point.get("value"), extractor);
            double derivative = point.has("derivative") ? point.get("derivative").getAsDouble() : 0.0;//Json.readNumber(point.derivative);
            //if (derivative == null) derivative = 0;
            spline.addPoint(location, (CubicSpline) value, derivative);
        }
        return (CubicSpline<Object>) spline;
    }

    record Constant(double value) implements CubicSpline<Object> {
        @Override
        public double applyAsDouble(Object o) {
            return value;
        }
        @Override
        public double min() {
            return value;
        }
        @Override
        public double max() {
            return value;
        }
        @Override
        public CubicSpline<Object> mapAll(CoordinateVisitor<Object> visitor) {
            return this;
        }

    }

    record MultiPoint<C>(
            ToDoubleFunction<C> coordinate,
            DoubleList locations,
            List<CubicSpline<Object>> values,
            DoubleList derivatives
    ) implements CubicSpline<C> {
        public MultiPoint(ToDoubleFunction<C> coordinate) {
            this(coordinate, new DoubleArrayList(), new ArrayList<>(), new DoubleArrayList());
        }

        @Override
        public double applyAsDouble(C c) {
            var coordinate = this.coordinate.applyAsDouble(c);
            int i = binarySearch(0, locations.size(), n -> coordinate < locations.getDouble(n)) - 1;
            int n = locations.size() - 1;
            if (i < 0) {
                return values.get(0).applyAsDouble(c) + derivatives.getDouble(0) * (coordinate - locations.getDouble(0));
            }
            if (i == n) {
                return values.get(n).applyAsDouble(c) + derivatives.getDouble(n) * (coordinate - locations.getDouble(n));
            }
            double loc0 = locations.getDouble(i), loc1 = locations.getDouble(i + 1);
            double der0 = derivatives.getDouble(i), der1 = derivatives.getDouble(i + 1);
            double f = (coordinate - loc0) / (loc1 - loc0);

            double val0 = values.get(i).applyAsDouble(c);
            double val1 = values.get(i + 1).applyAsDouble(c);

            double f8 = der0 * (loc1 - loc0) - (val1 - val0);
            double f9 = -der1 * (loc1 - loc0) + (val1 - val0);
            double f10 = lerp(f, val0, val1) + f * (1.0 - f) * lerp(f, f8, f9);
            return f10;
        }

        @Override
        public double min() {
            return values.stream().map(CubicSpline::min).min(Comparator.naturalOrder()).get();
        }

        @Override
        public double max() {
            return values.stream().map(CubicSpline::max).max(Comparator.naturalOrder()).get();
        }

        @Override
        @SuppressWarnings("unchecked")
        public CubicSpline<C> mapAll(CoordinateVisitor<C> visitor) {
            return new MultiPoint<>(visitor.visit(coordinate), locations, values.stream().map(v -> v.mapAll((CoordinateVisitor<Object>) visitor)).toList(), derivatives);
        }

        @SuppressWarnings("unchecked")
        public MultiPoint<C> addPoint(double location, CubicSpline<C> value, double derivative /*= 0*/) {
            locations.add(location);
            values.add((CubicSpline<Object>) value);
            derivatives.add(derivative);
            return this;
        }

        public MultiPoint<C> addPoint(double location, CubicSpline<C> value) {
            return addPoint(location, value, 0);
        }

        public MultiPoint<C> addPoint(double location, double value, double derivative /*= 0*/) {
            locations.add(location);
            values.add(new Constant(value));
            derivatives.add(derivative);
            return this;
        }

        public MultiPoint<C> addPoint(double location, double value) {
            return addPoint(location, value, 0);
        }
    }
}