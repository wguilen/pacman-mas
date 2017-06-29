package pacman.model.behaviour;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.concurrent.ThreadLocalRandom;
import pacman.model.agent.PacmanAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.CellType;
import pacman.model.board.Coord2D;
import pacman.model.board.Direction;
import pacman.model.core.Constant;
import pacman.model.core.GameVocabulary;

public class PacmanMovementBehaviour extends SimpleBehaviour
{

    private final Board board;
    private final Cell myCell;
    private final ACLMessage originMessage;

    // Control properties
    private boolean moved;      // Tracks if Pacman has done its movement

    public PacmanMovementBehaviour(ACLMessage originMessage, Board board, Cell myCell)
    {
        this.board = board;
        this.myCell = myCell;
        this.originMessage = originMessage;

        // Inits the game control properties
        moved = false;
    }

    @Override
    public void action()
    {
        // If the game is not running or Pacman still didn't 
        //      left his house, doesn't move
        if (!(                                              // If not
                ((PacmanAgent) myAgent).isGameRunning()      // ... game is running
           ))
        {
            // Replies to the Game Agent denying this behaviour
            /*ACLMessage reply = originMessage.createReply();
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent(GameVocabulary.MOVED_MY_BODY);
            myAgent.send(reply);*/
        
            // Removes this behaviour from Pacman
            myAgent.removeBehaviour(this);
            
            return;
        }
        
        synchronized(this)
        {
            move(); // Pacman makes a movement
        }
    }

    @Override
    public boolean done()
    {
        return moved;
    }

    private void move()
    {
        boolean cellSelected = false;

        Coord2D myPosition = myCell.getPosition();
        Coord2D myNewPosition = null;
        Cell nearCell;

        // Control variable used for letting Pacman reverse his way, 
        //      if it gets stuck
        int attemps = 0;

        do
        {
            ++attemps;

            // If no direction is being followed, selects one
            if (null == getCurrentDirection())
            {
                // Selects a direction to follow
                for (Direction direction : Direction.values())
                {
                    if (direction.equals(Direction.NONE))
                    {
                        continue;
                    }
                    
                    myNewPosition = getNewPosition(myPosition, direction);
                    nearCell = board.getCell(myNewPosition);

                    // If it's a valid cell and the direction is not a reverse one, selects it
                    if (isValidDestination(nearCell) && (getLastDirection() != direction || attemps >= 3))
                    {
                        cellSelected = true;
                        setCurrentDirection(direction);
                        setLastDirection(direction.getReverse());
                        break;
                    }
                }
            }
            // Tries to keep following this direction
            else
            {
                // If a bifurcation was found, decides to follow it or not
                for (Direction direction : Direction.values())
                {
                    if (direction.equals(getCurrentDirection())
                            || direction.equals(getLastDirection())
                            || direction.equals(Direction.NONE))
                    {
                        continue;
                    }

                    myNewPosition = getNewPosition(myPosition, direction);
                    nearCell = board.getCell(myNewPosition);

                    // Found a valid bifurcation
                    if (isValidDestination(nearCell))
                    {
                        float changeDirection = ThreadLocalRandom.current().nextFloat();

                        // May I follow it?
                        if (changeDirection <= Constant.GHOST_TURN_ON_BIFURCATION_CHANCE)
                        {
                            cellSelected = true;
                            setCurrentDirection(direction);
                            setLastDirection(direction.getReverse());
                            break;
                        }
                    }
                }

                // Else, tries to keep following the previous direction
                if (!cellSelected)
                {
                    myNewPosition = getNewPosition(myPosition, getCurrentDirection());
                    nearCell = board.getCell(myNewPosition);

                    // If the direction being followed is no longer valid, selects another
                    if (!isValidDestination(nearCell))
                    {
                        setCurrentDirection(null);
                    }
                    else
                    {
                        cellSelected = true;
                    }
                }
            }
        } while (!cellSelected);

        // Effectively makes the movement
        board.moveCell(myCell, myNewPosition, true);
        moved = true;
        
        // Notifies GameAgent I've made my movement
        ACLMessage reply = originMessage.createReply();
        reply.setPerformative(ACLMessage.CONFIRM);
        reply.setContent(GameVocabulary.MOVED_MY_BODY);
        myAgent.send(reply);
        
        // Updates the PacmanAgent state
        ((PacmanAgent) myAgent).setMoving(false);
    }

    private boolean isValidDestination(Cell cell)
    {
        return CellType.DOOR != cell.getType()              // Cannot run to a door
               && CellType.GHOST != cell.getType()          // Neither to a ghost // TODO: Remove this and treat it
               && CellType.GHOST_HOUSE != cell.getType()    // Neither to a ghost house
               && CellType.WALL != cell.getType();          // Neither to a wall
    }

    private Coord2D getNewPosition(Coord2D currentPosition, Direction destination)
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
    
    private Direction getCurrentDirection()
    {
        return ((PacmanAgent) myAgent).getCurrentDirection();
    }

    private void setCurrentDirection(Direction direction)
    {
        ((PacmanAgent) myAgent).setCurrentDirection(direction);
    }

    private Direction getLastDirection()
    {
        return ((PacmanAgent) myAgent).getLastDirection();
    }

    private void setLastDirection(Direction direction)
    {
        ((PacmanAgent) myAgent).setLastDirection(direction);
    }

}
