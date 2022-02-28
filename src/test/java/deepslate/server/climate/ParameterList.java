package deepslate.server.climate;

import deepslate.server.BiomeConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ParameterList {
    public static final int SPACE = 7;
    public static final double QUANTIZE_SCALE = 10000.0;

    private final Node root;

    public ParameterList(BiomeConfig biomeConfig) {
        var nodes = Arrays.stream(biomeConfig.getBiomes()).map(Node::leaf).collect(Collectors.toList());
        root = Node.build(nodes);
    }

    public BiomeConfig.Biome find(TargetPoint target) {
        var node = root.search(target.space());
        var biome = node.biome;
        assert biome != null : "Expected a leaf node";
        return biome;
    }

    public static long quantize(double x)  {
        return (long) (x * QUANTIZE_SCALE);
    }

    public static double unquantize(long x) {
        return (double) x / QUANTIZE_SCALE;
    }

    public record Param(long min, long max) {
        public Param {
            assert min <= max;
        }

        public static Param point(double v) {
            return span(v, v);
        }

        public static Param span(double min, double max) {
            return new Param(quantize(min), quantize(max));
        }

        public long distance(long x) {
            long diffMax = x - max;
            long diffMin = min - x;
            return diffMax > 0 ? diffMax : Math.max(diffMin, 0);
        }

        public Param union(Param other) {
            if (other == null) return this;
            return new Param(Math.min(min, other.min), Math.max(max, other.max));
        }
    }

    public record Node(Param[] space, List<Node> children, BiomeConfig.Biome biome) {
        public static Node subtree(List<Node> children) {
            return new Node(buildSpace(children), children, null);
        }

        public static Node leaf(BiomeConfig.Biome biome) {
            return new Node(new Param[]{
                    Param.span(biome.temperature0(), biome.temperature1()),
                    Param.span(biome.humidity0(), biome.humidity1()),
                    Param.span(biome.continentalness0(), biome.continentalness1()),
                    Param.span(biome.erosion0(), biome.erosion1()),
                    Param.span(biome.weirdness0(), biome.weirdness1()),
                    Param.span(biome.depth0(), biome.depth1()),
                    Param.span(biome.offset(), biome.offset()),
            }, new ArrayList<>(), biome);
        }

        public Node search(long[] /*TargetSpace*/ values) {
            if (biome != null) return this;

            long dist = Long.MAX_VALUE;
            Node result = this;

            for (var node : children) {
                long d1 = node.distance(values);
                if (dist <= d1) continue;
                var child = node.search(values);
                long d2 = node.equals(child) ? d1 : child.distance(values);
                if (dist <= d2) continue;
                dist = d2;
                result = child;
            }

            return result;
        }

        public long distance(long[] /*TargetSpace*/ target) {
            long dist = 0;
            for (int i = 0; i < SPACE; ++i) {
                long d = space[i].distance(target[i]);
                dist += d * d;
            }
            return dist;
        }

        public static Node build(List<Node> nodes) {
            int n = nodes.size();
            if (n == 0) {
                throw new RuntimeException("Need at least one child to build a node");
            } else if (n == 1) {
                return nodes.remove(0);
            } else if (n <= 10) {
                nodes.sort(Comparator.comparingLong(a -> cost(a.space)));
                return subtree(nodes);
            } else  {
                long minCost = Long.MAX_VALUE;
                int minN = 0;
                List<Node> minBuckets = null;

                for (int i = 0; i < SPACE; ++i) {
                    sort(nodes, i, false);
                    var buckets = bucketize(new ArrayList<>(nodes));
                    long cost = 0;
                    for (var bucket : buckets) {
                        cost += cost(bucket.space);
                    }
                    if (minCost <= cost) continue;
                    minCost = cost;
                    minN = i;
                    minBuckets = buckets;
                }

                var buckets = minBuckets;
                assert buckets != null : "Error splitting nodes in buckets";
                sort(buckets, minN, true);

                List<Node> result = new ArrayList<>(buckets.size());
                for (var bucket : buckets) {
                    if (bucket.biome == null) result.add(build(bucket.children));
                }
                return subtree(result);
            }
        }

        private static void sort(List<Node> nodes, int n, boolean abs) {
            nodes.sort(Comparator.comparingLong(node -> {
                var sum = 0;
                for (int i = 0; i < SPACE; ++i) {
                    var param = node.space[(n + i) % SPACE];
                    long mid = (param.min + param.max) / 2;
                    sum += abs ? Math.abs(mid) : mid;
                }
                return sum;
            }));
        }

        public static List<Node> bucketize(List<Node> nodes) {
            int n = (int) Math.pow(10.0, (int) Math.floor(Math.log(nodes.size() - 0.01) / Math.log(10.0)));

            List<Node> buckets = new ArrayList<>();
            List<Node> buffer = new ArrayList<>(n);

            for (var node : nodes) {
                buffer.add(node);
                if (buffer.size() >= n) {
                    buckets.add(subtree(buffer));
                    buffer = new ArrayList<>(n);
                }
            }
            if (buffer.size() != 0) {
                buckets.add(subtree(buffer));
            }
            return buckets;
        }

        public static long cost(Param[] /*ParamSpace*/ space) {
            long cost = 0;
            for (var param : space) {
                cost += Math.abs(param.max - param.min);
            }
            return cost;
        }

        private static Param[] buildSpace(List<Node> nodes) {
            assert !nodes.isEmpty() : "SubTree needs at least one child";
            Param[] space = new Param[SPACE];
            for (var node : nodes) {
                for (int i = 0; i < SPACE; ++i) {
                    space[i] = node.space[i].union(space[i]);
                }
            }
            return space;
        }
    }
}
