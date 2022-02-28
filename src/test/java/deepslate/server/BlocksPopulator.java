package deepslate.server;

import deepslate.worldgen.biome.TerrainShaper;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;

public class BlocksPopulator implements ChunkPopulator {
    public TerrainShaper terrainShaper = TerrainShaper.overworld();

    @Override
    public void populateChunk(ChunkBatch batch, Chunk chunk) {
        int chunkX = chunk.getChunkX(), chunkZ = chunk.getChunkZ();
        for (byte cz = 0; cz < Chunk.CHUNK_SIZE_Z; ++cz) {
            for (byte cx = 0; cx < Chunk.CHUNK_SIZE_X; ++cx) {
                //int x = chunkX * Chunk.CHUNK_SIZE_X + cx, z = chunkZ * Chunk.CHUNK_SIZE_Z + cz;

                int height = getHeight(chunk, cx, cz);
                var biome = getBiome(chunk, cx, cz);

                if (Biomes.isColdWater(biome)) {
                    for (int y = -63; y <= height - 1; ++y) {
                        chunk.setBlock(cx, y, cz, Block.STONE);
                    }
                    chunk.setBlock(cx, height, cz, Block.GRAVEL);
                } else if (Biomes.isWarmWater(biome)) {
                    for (int y = -63; y <= height - 1; ++y) {
                        chunk.setBlock(cx, y, cz, Block.STONE);
                    }
                    chunk.setBlock(cx, height, cz, Block.SAND);
                } else if (Biomes.isSandy(biome)) {
                    for (int y = -63; y <= height - 6; ++y) {
                        chunk.setBlock(cx, y, cz, Block.STONE);
                    }
                    for (int y = height - 6; y <= height - 3; ++y) {
                        chunk.setBlock(cx, y, cz, Block.SANDSTONE);
                    }
                    for (int y = height - 3; y <= height; ++y) {
                        chunk.setBlock(cx, y, cz, Block.SAND);
                    }
                } else if (Biomes.isStony(biome)) {
                    for (int y = -63; y <= height; ++y) {
                        chunk.setBlock(cx, y, cz, Block.STONE);
                    }
                } else if (Biomes.isBadlands(biome)) {
                    if (height < 70) {
                        for (int y = -63; y <= height - 3; ++y) {
                            chunk.setBlock(cx, y, cz, Block.STONE);
                        }
                        for (int y = height - 3; y <= height; ++y) {
                            chunk.setBlock(cx, y, cz, Block.RED_SAND);
                        }
                    } else {
                        for (int y = -63; y <= 67; ++y) {
                            chunk.setBlock(cx, y, cz, Block.STONE);
                        }
                        for (int y = 68; y <= height; ++y) {
                            chunk.setBlock(cx, y, cz, Block.TERRACOTTA);
                        }
                    }
                } else {
                    for (int y = -63; y <= height - 4; ++y) {
                        chunk.setBlock(cx, y, cz, Block.STONE);
                    }
                    for (int y = height - 3; y <= height - 1; ++y) {
                        chunk.setBlock(cx, y, cz, Block.DIRT);
                    }
                    chunk.setBlock(cx, height, cz, Block.GRASS_BLOCK);
                }

                if (Biomes.isSnowy(biome)) {
                    if (Biomes.hasDeepSnow(biome)) {
                        chunk.setBlock(cx, height, cz, Block.SNOW_BLOCK);
                    }
                    if (height >= 64) {
                        chunk.setBlock(cx, height + 1, cz, Block.SNOW);
                    } else {
                        chunk.setBlock(cx, 64, cz, Block.ICE);
                    }
                }

                for (int y = height + 1; y <= 64; ++y) {
                    chunk.setBlock(cx, y, cz, Block.WATER);
                }

                if (height < 64 && Biomes.isFrozen(biome)) {
                    chunk.setBlock(cx, 64, cz, Block.ICE);
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private int getHeight(Chunk chunk, int cx, int cz) {
        var nbt = chunk.getBlock(cx, -64, cz).nbt();

        var point = TerrainShaper.point(
                nbt.getDouble("continentalness"),
                nbt.getDouble("erosion"),
                nbt.getDouble("weirdness")
        );

        double offset = terrainShaper.offset(point);
        double factor = terrainShaper.factor(point);
        double jaggedness = terrainShaper.jaggedness(point);
        double height = (offset - TerrainShaper.GLOBAL_OFFSET) * (32) * factor + 64 + jaggedness;

        return (int) height;
    }

    @SuppressWarnings("ConstantConditions")
    private String getBiome(Chunk chunk, int cx, int cz) {
        return chunk.getBlock(cx, -64, cz).nbt().getString("biome");
    }
}
