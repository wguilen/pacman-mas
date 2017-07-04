package pacman.model.core;

public class Constant
{
    
    // General
    public static final boolean DEBUG                               = false;    // If the board should be printed on console after each movement
    
    // Board
    public static final int BOARD_CELL_SIZE                         = 30;       // Squarred size of each cell in the GUI
    
    // Entities general
    public static final int MOVEMENT_DELAY                          = 120;      // Delay (in milliseconds) for each movement from ghosts and pacman
    
    // Game agent
    public static final String GAME_AGENT_NAME                      = "Game";   // Game agent name
    public static final int GAME_START_DELAY                        = 3000;     // Delay (in milliseconds) before the game starts running
    public static final int RESEND_MOVE_ORDER_TIMEOUT               = 3;        // Number of turns Game agent must wait before resend move order to agents
    
    // Pacman agent
    public static final float PACMAN_TURN_ON_BIFURCATION_CHANCE     = 0.3f;     // Chance of Pacman to turn on bifurcation when one is found in his continous path
    public static final int PACMAN_POWERUP_TURNS                    = 60;       // Number of turns Pacman must have a powerful state after collecting a powerup
    
    // Ghosts agents
    public static final int GHOST_LEAVE_HOUSE_DELAY                 = 2500;     // Delay (in milliseconds) for each ghost leaving the house
    public static final float GHOST_TURN_ON_BIFURCATION_CHANCE      = 0.3f;     // Chance of a ghost to turn on bifurcation when one is found in his continous path
    public static final int GHOST_RUN_FROM_ANOTHER_NEAR_DISTANCE    = 5;        // Distance (in cells) of the ghost in relation to another ghost used for reversing the ghost direction
    
}
