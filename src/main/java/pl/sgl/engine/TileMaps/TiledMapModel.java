package pl.sgl.engine.TileMaps;

import java.util.List;

public class TiledMapModel {
    public int width;        // szerokość w kafelkach
    public int height;       // wysokość w kafelkach
    public int tilewidth;    // szerokość kafelka w px
    public int tileheight;   // wysokość kafelka w px
    public List<TiledLayer> layers;

    public static class TiledLayer {
        public int[] data;   // tablica z ID kafelków
        public String name;
        public boolean visible;
    }
}