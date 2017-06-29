package pacman.model.board;

import java.awt.Color;

public enum CellType
{

    EMPTY('E', Color.BLACK),            // Empty place
    WALL('#', new Color(150, 75, 0)),   // Wall
    PACMAN_HOUSE('S', Color.BLACK),     // Pacman house (Pacman agent initial position)
    PACMAN('P', Color.ORANGE),          // Pacman agent
    GHOST_HOUSE('H', Color.BLACK),      // Ghost house (Ghosts agents initial position)
    GHOST('G', Color.WHITE),            // Ghost agent
    DOOR('D', Color.GRAY),              // Door (for the ghosts to leave their houses)
    DOT('.', Color.YELLOW),             // Simple collectible item
    POWERUP('U', Color.GREEN);          // Powerup collectible item
 
    private final char value;
    private final Color color;
    
    CellType(char value, Color color)
    {
        this.value = value;
        this.color = color;
    }
    
    public char getValue()
    {
        return value;
    }
    
    public Color getColor()
    {
        return color;
    }

    @Override
    public String toString()
    {
        return String.valueOf(value);
    }
    
}
