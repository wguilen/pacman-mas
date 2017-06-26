package pacman.model.board;

public enum Direction
{

    NONE    (0,  0),
    LEFT    (-1, 0),
    RIGHT   (+1, 0),
    DOWN    (0, +1),
    UP      (0, -1);

    public final int yInc, xInc;

    Direction(int yInc, int xInc)
    {
        this.yInc = yInc;
        this.xInc = xInc;
    }
    
    public Direction getReverse()
    {
        Direction reverse = null;
        
        switch (this)
        {
            case DOWN:
                reverse = Direction.UP;
                break;

            case UP:
                reverse = Direction.DOWN;
                break;

            case LEFT:
                reverse = Direction.RIGHT;
                break;

            case RIGHT:
                reverse = Direction.LEFT;
                break;
        }
        
        return reverse;
    }

}
