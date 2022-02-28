package deepslate.server.climate;

public class TargetPoint {
    private final long temperature;
    private final long humidity;
    private final long continentalness;
    private final long erosion;
    private final long weirdness;
    private final long depth;

    public TargetPoint(double temperature, double humidity, double continentalness, double erosion, double weirdness, double depth) {
        this.temperature = ParameterList.quantize(temperature);
        this.humidity = ParameterList.quantize(humidity);
        this.continentalness = ParameterList.quantize(continentalness);
        this.erosion = ParameterList.quantize(erosion);
        this.weirdness = ParameterList.quantize(weirdness);
        this.depth = ParameterList.quantize(depth);
    }

    public long[] /*TargetSpace*/ space() {
        return new long[]{temperature, humidity, continentalness, erosion, weirdness, depth, 0};
    }
}
