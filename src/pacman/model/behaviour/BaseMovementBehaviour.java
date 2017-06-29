package pacman.model.behaviour;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.CellType;
import pacman.model.board.Coord2D;
import pacman.model.board.Direction;

public abstract class BaseMovementBehaviour extends SimpleBehaviour
{

    protected final Board board;
    protected final Cell myCell;
    protected final ACLMessage originMessage;

    // Game control properties
    protected boolean moved;      // Tracks if Pacman has done its movement

    public BaseMovementBehaviour(ACLMessage originMessage, Board board, Cell myCell)
    {
        this.board = board;
        this.myCell = myCell;
        this.originMessage = originMessage;

        // Inits the game control properties
        moved = false;
    }

    @Override
    public boolean done()
    {
        return moved;
    }

    protected boolean isValidDestination(Cell cell)
    {
        return CellType.DOOR != cell.getType()              // Cannot run to a door
               && CellType.GHOST_HOUSE != cell.getType()    // Neither to a ghost house
               && CellType.WALL != cell.getType();          // Neither to a wall
    }

    protected Coord2D getNewPosition(Coord2D currentPosition, Direction destination)
    {
        Coord2D newPosition = new Coord2D(currentPosition.x + destination.xInc, currentPosition.y + destination.yInc);

        // Validates x position
        if (newPosition.x < 0)
        {
            newPosition = new Coord2D(board.countRows() - 1, newPosition.y);
        } 
        else if (newPosition.x > board.countRows() - 1)
        {
            newPosition = new Coord2D(0, newPosition.y);
        }

        // Validates y position        
        if (newPosition.y < 0)
        {
            newPosition = new Coord2D(newPosition.x, board.countColumns() - 1);
        } 
        else if (newPosition.y > board.countColumns() - 1)
        {
            newPosition = new Coord2D(newPosition.x, 0);
        }

        return newPosition;
    }

    
    // --- Getters and setters
    
    protected abstract Direction getCurrentDirection();
    protected abstract void setCurrentDirection(Direction direction);
    protected abstract Direction getLastDirection();
    protected abstract void setLastDirection(Direction direction);

}
