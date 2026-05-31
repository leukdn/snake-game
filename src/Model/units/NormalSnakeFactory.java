package Model.units;

public class NormalSnakeFactory implements SnakeFactory {
    @Override
    public AbstractSnake createSnake(int lives, int k) {
        return new NormalSnake(lives, k);
    }
}