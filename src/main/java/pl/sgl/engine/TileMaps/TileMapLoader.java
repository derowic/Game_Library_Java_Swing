package pl.sgl.engine.TileMaps;

import com.google.gson.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public class TileMapLoader {

    public static int[][] loadMap(String path) {
        try (Reader reader = new InputStreamReader(TileMapLoader.class.getResourceAsStream(path))) {
            Gson gson = new Gson();
            TiledMapModel model = gson.fromJson(reader, TiledMapModel.class);

            // Wybieramy pierwszą warstwę (zazwyczaj "Tile Layer 1")
            int[] rawData = model.layers.get(0).data;
            int[][] map2D = new int[model.height][model.width];

            // Konwersja tablicy 1D na 2D
            for (int y = 0; y < model.height; y++) {
                for (int x = 0; x < model.width; x++) {
                    // Tiled używa ID startujących od 1 (0 to puste pole)
                    // Odejmujemy 1, aby pasowało do indeksów Twojej tablicy obrazków
                    map2D[y][x] = rawData[y * model.width + x] - 1;
                }
            }
            return map2D;

        } catch (Exception e) {
            System.err.println("Błąd wczytywania mapy JSON: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public static Set<Integer> loadCollisionsFromTileset(String jsonPath) {
        Set<Integer> collidableTiles = new HashSet<>();

        // 1. Sprawdź czy plik w ogóle istnieje (zabezpieczenie przed NullPointerException)
        var inputStream = TileMapLoader.class.getResourceAsStream(jsonPath);
        if (inputStream == null) {
            System.err.println("Nie znaleziono pliku tilesetu: " + jsonPath);
            return collidableTiles;
        }

        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            // 2. Użyj JsonReader z opcją setLenient(true)
            com.google.gson.stream.JsonReader jsonReader = new com.google.gson.stream.JsonReader(reader);
            jsonReader.setLenient(true);

            // 3. Parsuj plik
            JsonObject json = JsonParser.parseReader(jsonReader).getAsJsonObject();

            JsonArray tiles = json.getAsJsonArray("tilesets");
            // 2. Pobieramy PIERWSZY element z tej tablicy (indeks 0) jako obiekt
            JsonObject firstTileset = tiles.get(0).getAsJsonObject();

// 3. Z tego obiektu wyciągamy tablicę "tiles"
            if (firstTileset.has("tiles")) {
                tiles = firstTileset.getAsJsonArray("tiles");

                System.out.println("Znaleziono definicję dla kafelków");

            } else {
                System.out.println("Ten tileset nie ma zdefiniowanych właściwości kafelków (brak tablicy 'tiles')");
            }

            if (tiles != null) {
                for (JsonElement tileElement : tiles) {
                    JsonObject tileObj = tileElement.getAsJsonObject();
                    int id = tileObj.get("id").getAsInt();

                    if (tileObj.has("properties")) {
                        JsonArray properties = tileObj.getAsJsonArray("properties");
                        for (JsonElement propElement : properties) {
                            JsonObject prop = propElement.getAsJsonObject();
                            if (prop.get("name").getAsString().equals("collidable") &&
                                    prop.get("value").getAsBoolean()) {
                                collidableTiles.add(id);
                                System.out.println("dodano clidable");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas parsowania JSON: " + jsonPath);
            e.printStackTrace();
        }

        return collidableTiles;
    }

//    public boolean isSolid(int tileId) {
//        // Jeśli ID jest w zbiorze collidableTiles, to znaczy że kafel jest twardy
//        return collidableTiles.contains(tileId);
//    }
}