package dev.insilicon.genim.Terrian;

import dev.insilicon.genim.Genim;

public class TerrianManager {

    private TerrianDB terrianDB;
    private ChunkHandler chunkHandler;
    private Genim plugin;

    public TerrianManager(Genim plugin) {
        this.plugin = plugin;
    }


    public void setGenerationCenter(int x, int y, int z) {
        terrianDB.replaceParam(1, x);
        terrianDB.replaceParam(2, y);
        terrianDB.replaceParam(3, z);

    }

}
