package Model.units;


public class SineSnakeFactory implements SnakeFactory {
    @Override
    public AbstractSnake createSnake(int lives, int k) {
        return new SineSnake(lives, k);
    }
}