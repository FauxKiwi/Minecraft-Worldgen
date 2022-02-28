package deepslate.server;

import deepslate.server.climate.ParameterList;
import deepslate.server.climate.TargetPoint;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.biomes.Biome;
import org.jglrxavpok.hephaistos.nbt.NBTString;

import java.io.File;
import java.io.IOException;

public class BiomeGenerator implements ChunkPopulator {
    BiomeConfig biomeConfig;
    ParameterList parameterList;

    public BiomeGenerator() {
        try {
            biomeConfig = new BiomeConfig(new File("D:\\IdeaProjects\\MultiNoise\\dimension.json"));
            parameterList = new ParameterList(biomeConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void populateChunk(ChunkBatch batch, Chunk chunk) {
        int chunkX = chunk.getChunkX(), chunkZ = chunk.getChunkZ();
        for (byte cz = 0; cz < Chunk.CHUNK_SIZE_Z; ++cz) {
            for (byte cx = 0; cx < Chunk.CHUNK_SIZE_X; ++cx) {
                int x = chunkX * Chunk.CHUNK_SIZE_X + cx, z = chunkZ * Chunk.CHUNK_SIZE_Z + cz;

                var biome = getBiomeAt(chunk.getBlock(x, -64, z));

                //for (int i = 0; i < 16; ++i) {
                    int ix = cx/* * 4 + i % 4*/, iz = cz/* * 4 + i / 4*/;
                    for (int y = -64; y < 320; ++y) {
                        setBiome(chunk, ix, y, iz, biome);
                    }
                    var b = chunk.getBlock(ix, -64, iz);
                    chunk.setBlock(ix, -64, iz, b.withNbt(
                            b.nbt().modify(nbt -> nbt.put("biome", new NBTString(biome.name().path())))
                    ));
                //}
            }
        }
    }

    private void setBiome(Chunk chunk, int x, int y, int z, Biome biome) {
        chunk.setBiome(x, y, z, biome);
        /*var b = chunk.getBlock(x, -64, z);
        chunk.setBlock(x, -64, z, b.withNbt(
                b.nbt().modify(nbt -> nbt.put("biome", new NBTString(biome.name().asString())))
        ));*/
    }

    @SuppressWarnings("ConstantConditions")
    private Biome getBiomeAt(Block block) {
        var nbt = block.nbt();
        if (nbt == null) System.out.println(block);

        var mb = parameterList.find(new TargetPoint(
                nbt.getAsDouble("temperature"),
                nbt.getAsDouble("humidity"),
                nbt.getAsDouble("continentalness"),
                nbt.getAsDouble("erosion"),
                nbt.getAsDouble("weirdness"),
                0.0
        ));
        var b = Biomes.getMap().get(mb.name().substring("minecraft:".length()));
        if (b == null) System.out.println(mb.name());
        return b;
    }
}
