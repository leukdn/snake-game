package Model.units;

public class ZigZagSnakeFactory implements SnakeFactory {
    @Override
    public AbstractSnake createSnake(int lives, int k) {
        return new ZigZagSnake(lives, k);
    }
}