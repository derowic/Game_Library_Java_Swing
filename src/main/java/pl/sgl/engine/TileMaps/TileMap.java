package pl.sgl.engine.TileMaps;

import pl.sgl.engine.texture.TextureLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class TileMap {
    private int[][] mapData; // Tablica z ID kafelków
    private BufferedImage[] tileImages; // Pocięte kafelki z Tilesetu
    private int tileSize;
    public Set<Integer> collidableTiles = new HashSet<>();

//    public TileMap(String path, int tileSize) {
//        this.tileSize = tileSize;
//        // 1. Ładujemy Tileset i tniemy go na kawałki
//        this.tileImages = loadTileset("/textures/tileset.png", tileSize);
////        System.out.println("loaded: "+ tileImages.length + " tiles" );
//
//        // 2. Przykładowa mapa (w realnej grze wczytasz to z pliku)
//        mapData = new int[][]{
//            {1, 1, 1, 1, 1},
//            {1, 0, 0, 0, 1},
//            {1, 0, 2, 0, 1},
//            {1, 1, 1, 1, 1}
//        };
//    }

    public TileMap(int[][] mapData, String path, int tileSize) {
        this.tileSize = tileSize;
        // 1. Ładujemy Tileset i tniemy go na kawałki
        this.tileImages = loadTileset(path, tileSize);
//        this.collidableTiles = TileMapLoader.loadCollisionsFromTileset(path);
//        System.out.println("loaded: "+ tileImages.length + " tiles" );

        // 2. Przykładowa mapa (w realnej grze wczytasz to z pliku)
        this.mapData = mapData;

        int[][] data = getMapData();

        if (data == null) {
            System.out.println("Tablica danych jest pusta (null)!");
        } else {
            System.out.println("--- PODGLĄD MAPY ---");
            for (int row = 0; row < data.length; row++) {
                for (int col = 0; col < data[row].length; col++) {
                    // Używamy printf z %3d, aby każda liczba zajmowała tyle samo miejsca (ładne kolumny)
                    System.out.printf("%3d ", data[row][col]);
                }
                // Po każdej narysowanej linii przechodzimy do nowego wiersza
                System.out.println();
            }
            System.out.println("--------------------");
        }
    }

    private BufferedImage[] loadTileset(String path, int size) {
        BufferedImage sheet = TextureLoader.load(path);
        int cols = sheet.getWidth() / size;
        int rows = sheet.getHeight() / size;
        BufferedImage[] tiles = new BufferedImage[cols * rows];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                tiles[y * cols + x] = sheet.getSubimage(x * size, y * size, size, size);
            }
        }
        return tiles;
    }

    public void draw(Graphics2D g, double camX, double camY, int screenW, int screenH) {
        // --- CULLING (Rysujemy tylko to, co widać na ekranie) ---
        int startCol = Math.max(0, (int) (camX / tileSize));
        int endCol = Math.min(mapData[0].length, (int) ((camX + screenW) / tileSize) + 1);
        int startRow = Math.max(0, (int) (camY / tileSize));
        int endRow = Math.min(mapData.length, (int) ((camY + screenH) / tileSize) + 1);

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                int tileId = mapData[row][col];
                // Zakładamy, że tileId 0 to puste miejsce
                if (tileId >= 0) {
                    g.drawImage(tileImages[tileId], col * tileSize, row * tileSize, tileSize, tileSize, null);
                }
            }
        }
    }

    public int getTileSize() { return tileSize; }
    public int[][] getMapData() { return mapData; }

    // --- FUNKCJA SPRAWDZAJĄCA CZY NA DANEJ POZYCJI JEST ŚCIANA ---
    public boolean isCollidingWithWall(double playerX, double playerY, int playerWidth, int playerHeight) {
        int tileSize = getTileSize();
        int[][] data =getMapData();


        // Sprawdzamy 4 narożniki gracza, aby wiedzieć czy dotyka ściany
        int left = (int) (playerX / tileSize);
        int right = (int) ((playerX + playerWidth - 1) / tileSize);
        int top = (int) (playerY / tileSize);
        int bottom = (int) ((playerY + playerHeight - 1) / tileSize);

        // Zabezpieczenie przed wyjściem poza tablicę mapy
        if (left < 0 || right >= data[0].length || top < 0 || bottom >= data.length) return false;

        // Jeśli którykolwiek narożnik dotyka kafelka o ID 1 (np. ściana) -> kolizja
        boolean tmp = (
                isColision(data[top][left]) ||
                        isColision(data[top][right]) ||
                isColision(data[bottom][left]) ||
                        isColision(data[bottom][right])
        );

//        System.out.println( isColision(data[top][left]));
//        System.out.println(isColision(data[top][right]));
//        System.out.println(isColision(data[bottom][left]));
//        System.out.println(isColision(data[bottom][right]));

        return tmp;
    }

    public boolean isColision(int tileId) {
        // Jeśli ID jest w zbiorze collidableTiles, to znaczy że kafel jest twardy
        return collidableTiles.contains(tileId);
    }

}