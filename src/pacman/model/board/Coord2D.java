package pacman.model.board;

import java.io.Serializable;

public class Coord2D implements Serializable
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