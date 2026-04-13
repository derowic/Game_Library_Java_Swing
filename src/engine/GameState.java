package engine;

public class GameState {
    public final double x, y, lastX, lastY;
    public final boolean didTeleport;

    public GameState(double x, double y, double lastX, double lastY, boolean didTeleport) {
        this.x = x; this.y = y;
        this.lastX = lastX; this.lastY = lastY;
        this.didTeleport = didTeleport;
    }

    public GameState(double x, double y, double lastX, double lastY) {
        this.x = x; this.y = y;
        this.lastX = lastX; this.lastY = lastY;
        this.didTeleport = false;
    }
}
