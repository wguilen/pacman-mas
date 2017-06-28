package pacman.model.board;

public class Coord2D
{
    
    public final int x;
    public final int y;

    public Coord2D(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Coord2D other)
    {
        return other != null && 
               (this == other || (this.x == other.x && this.y == other.y));
    }
    
    @Override
    public String toString()
    {
        return "{ x = " + x + ", y = " + y + " }";
    }
    
}