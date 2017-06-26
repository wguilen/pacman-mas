package pacman.model.core;

public class Constant
{
    
    // General
    public static final boolean DEBUG                           = false; // If the board should be printed on console after each movement
    
    // Board
    public static final int BOARD_CELL_SIZE                     = 30;   // Squarred size of each cell in the GUI
    
    // Entities general
    public static final int MOVEMENT_DELAY                      = 500; // Delay (in milliseconds) for each movement from ghosts and pacman
    
    // Ghosts
    public static final int GHOST_LEAVE_HOUSE_DELAY             = 3000; // Delay (in milliseconds) for each ghost leaving the house
    public static final float GHOST_TURN_ON_BIFURCATION_CHANCE  = 0.3f;
    
}
