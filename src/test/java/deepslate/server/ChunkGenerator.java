package deepslate.server;

import deepslate.noise.Sampler;
import deepslate.worldgen.biome.MultiOctaves;
import deepslate.worldgen.biome.Octaves;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.DynamicChunk;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.batch.ChunkGenerationBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTDouble;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkGenerator implements net.minestom.server.instance.ChunkGenerator {
    //public static final double ZOOM = 0.00125 * 0.5;

    public int seed = new java.util.Random().nextInt();

    public MultiOctaves overworldOctaves = new MultiOctaves(
            new Octaves(-9, new double[]{1.5, 0, 1, 0, 0, 0}), // temperature
            new Octaves(-7, new double[]{1, 1, 0, 0, 0, 0}), // humidity
            new Octaves(-9, new double[]{1, 1, 2, 2, 2, 1, 1, 1, 1}), // continentalness
            new Octaves(-9, new double[]{1, 1, 0, 1, 1}), // erosion
            new Octaves(-7, new double[]{1, 2, 1, 0, 0, 0}), // weirdness
            new Octaves(-3, new double[]{1, 1, 1, 0}) // shift
    );

    public Sampler sampler = Sampler.fromOctaves(seed, overworldOctaves);

    /*public BiomeGenerator bg;
    public BlocksPopulator bp;

    {
        bg = new BiomeGenerator();
        bg.cg = this;
        bp = new BlocksPopulator();
    }*/

    @Override
    public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {
        for (byte cz = 0; cz < Chunk.CHUNK_SIZE_Z; ++cz) {
            for (byte cx = 0; cx < Chunk.CHUNK_SIZE_X; ++cx) {
                int x = chunkX * Chunk.CHUNK_SIZE_X + cx, z = chunkZ * Chunk.CHUNK_SIZE_Z + cz;
                //double nx = x / (4.0 * 32), nz = z / (4.0 * 32);
                double nx = x / 4.0, nz = z / 4.0;

                double xx = nx + sampler.offset().sample(nx, 0, nz) * 4;
                double zz = nz + sampler.offset().sample(nz, nz, 0) * 4;

                double temperature = sampler.temperature().sample(xx, 0, zz);
                double humidity = sampler.humidity().sample(xx, 0, zz);
                double continentalness = sampler.continentalness().sample(xx, 0, zz);
                double erosion = sampler.erosion().sample(xx, 0, zz);
                double weirdness = sampler.weirdness().sample(xx, 0, zz);

                Map<String, NBT> nbt = new HashMap<>();
                nbt.put("temperature", new NBTDouble(temperature));
                nbt.put("humidity", new NBTDouble(humidity));
                nbt.put("continentalness", new NBTDouble(continentalness));
                nbt.put("erosion", new NBTDouble(erosion));
                nbt.put("weirdness", new NBTDouble(weirdness));
                nbt.put("xx", new NBTDouble(xx));
                nbt.put("zz", new NBTDouble(zz));

                var block = Block.BEDROCK.withNbt(new NBTCompound(nbt));

                batch.setBlock(x, -64, z, block);
            }
        }
    }

    @Override
    public @Nullable List<ChunkPopulator> getPopulators() {
        return List.of((batch, chunk) -> {
            try {
                var cf = ChunkGenerationBatch.class.getDeclaredField("chunk");
                var blocks = DynamicChunk.class.getDeclaredField("entries");
                cf.setAccessible(true);
                blocks.setAccessible(true);
                var b = (Int2ObjectMap<Block>) blocks.get(cf.get(batch));
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        var index = ChunkUtils.getBlockIndex(x, -64, z);
                        synchronized (b) {
                            chunk.setBlock(x, -64, z, b.get(index));
                        }
                    }
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }, new BlocksPopulator());
    }
}
