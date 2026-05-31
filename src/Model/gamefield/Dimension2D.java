package Model.gamefield;

public class Dimension2D {
    private final int width;
    private final int height;

    public Dimension2D(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Размеры поля должны быть положительными");
        }
        this.width = width;
        this.height = height;
    }

    public int getWidth()  { return width; }
    public int getHeight() { return height; }
}