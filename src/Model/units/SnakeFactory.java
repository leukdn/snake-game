package Model.units;

/**
 * Фабрика змей
 */
public interface SnakeFactory {
    AbstractSnake createSnake(int lives, int k);
}