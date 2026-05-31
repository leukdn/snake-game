package Model.gamefield;

public enum Direction {
    NORTH, SOUTH, EAST, WEST;

    // вернуть противоположное направление
    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST  -> WEST;
            case WEST  -> EAST;
        };
    }


    public Direction left() {
        return switch (this) {
            case NORTH -> WEST;
            case SOUTH -> EAST;
            case EAST  -> NORTH;
            case WEST  -> SOUTH;
        };
    }


    public Direction right() {
        return switch (this) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case EAST  -> SOUTH;
            case WEST  -> NORTH;
        };
    }
}