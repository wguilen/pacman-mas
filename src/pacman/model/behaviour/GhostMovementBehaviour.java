package pacman.model.behaviour;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import pacman.model.agent.GhostAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.CellType;
import pacman.model.board.Coord2D;
import pacman.model.board.Direction;
import pacman.model.board.GhostCell;
import pacman.model.core.Constant;
import pacman.model.core.GhostVocabulary;

public class GhostMovementBehaviour extends TickerBehaviour
{

    protected final Board board;
    protected final Cell myCell;

    public GhostMovementBehaviour(Agent agent, Board board, Cell myCell)
    {
        super(agent, Constant.MOVEMENT_DELAY);
        
        this.board = board;
        this.myCell = myCell;
    }
    
    @Override
    public void onTick()
    {
        // If the game is not running or the ghost still didn't 
        //      left his house, doesn't move
        if (!(((GhostAgent)myAgent).isGameRunning() 
                && ((GhostAgent) myAgent).isHouseLeft()))
        {
            //System.out.println(myAgent.getLocalName() + " still didn't left his house...");
            return;
        }
        
        // If the ghost received "GET_OUT_MY_WAY", maybe it reverses the
        //      direction being followed
        if (((GhostAgent) myAgent).isReverseDirection())
        {
            maybeReverseDirection();
        }
        
        move();                 // Ghost makes a movement
        checkGhostOnSamePath(); // Ghost checks if are there another ghosts on the same path
    }
    
    private void move()
    {
        boolean cellSelected = false;
        
        Coord2D myPosition = myCell.getPosition();
        Coord2D myNewPosition = null;
        Cell nearCell;
        
        // Control variable used for letting the ghost reverse his way, 
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
                            || direction.equals(getLastDirection()))
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
        board.moveCell(myCell, myNewPosition);
    }
    
    private void checkGhostOnSamePath()
    {
        List<GhostAgent> ghosts = new ArrayList<>();
        
        // Checks if there's another ghost in the same column as me
        // (from my position to the beggining of the board)
        for (int i = getNewPosition(myCell.getPosition(), Direction.UP).x; i > 0; --i)
        {
            Cell cell = board.getCell(new Coord2D(i, myCell.getPosition().y));
            if (!addGhostOnSamePath(ghosts, cell))
            {
                break;
            }
        }
        
        // Checks if there's another ghost in the same column as me
        // (from my position to the end of the board)
        for (int i = getNewPosition(myCell.getPosition(), Direction.DOWN).x; i < board.countRows(); ++i)
        {
            Cell cell = board.getCell(new Coord2D(i, myCell.getPosition().y));
            if (!addGhostOnSamePath(ghosts, cell))
            {
                break;
            }
        }
        
        // Checks if there's another ghost in the same row as me
        // (from my position to the beggining of the board)
        for (int i = getNewPosition(myCell.getPosition(), Direction.LEFT).y; i > 0; --i)
        {
            Cell cell = board.getCell(new Coord2D(myCell.getPosition().x, i));
            if (!addGhostOnSamePath(ghosts, cell))
            {
                break;
            }
        }
        
        // Checks if there's another ghost in the same row as me
        // (from my position to the end of the board)
        for (int i = getNewPosition(myCell.getPosition(), Direction.RIGHT).y; i < board.countColumns(); ++i)
        {
            Cell cell = board.getCell(new Coord2D(myCell.getPosition().x, i));
            if (!addGhostOnSamePath(ghosts, cell))
            {
                break;
            }
        }
        
        // If there was any ghost(s), notifies it(them)
        if (!ghosts.isEmpty())
        {
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.setOntology(GhostVocabulary.ONTOLOGY);
            message.setContent(GhostVocabulary.GET_OUT_OF_MY_WAY);
            
            ghosts.forEach((ghost) ->
            {
                message.addReceiver(ghost.getAID());
            });
            
            myAgent.send(message);
        }
    }
    
    private boolean addGhostOnSamePath(List<GhostAgent> ghosts, Cell cell)
    {
        if (cell.getType().equals(CellType.GHOST))
        {
            GhostAgent other = ((GhostCell) cell).getAgent();
            
            // If the other ghost direction is the opposite of mine,
            //    I ask him for leaving my path
            if (
                    null != getCurrentDirection()
                    && null != other.getCurrentDirection() 
                    && other.getCurrentDirection().equals(getCurrentDirection().getReverse())
               )
            {
                ghosts.add(other);
            }
            
            // This is needed so the loop in which this method is called
            //   keeps running
            return true; 
        }
        
        return isValidDestination(cell);
    }
    
    private boolean isValidDestination(Cell cell)
    {
        return CellType.DOOR != cell.getType()             // Cannot run to a door
               && CellType.GHOST_HOUSE != cell.getType()   // Neither to a ghost house
               && CellType.GHOST != cell.getType()         // Neither to another ghost
               && CellType.WALL != cell.getType();         // Neither to a wall
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

    private void maybeReverseDirection()
    {
        boolean reverse = ThreadLocalRandom.current().nextBoolean();
        if (reverse)
        {
            setLastDirection(getCurrentDirection());
            setCurrentDirection(getCurrentDirection().getReverse());
            
            //System.out.println(myAgent.getLocalName() + " reversed his direction");
        }
        
        ((GhostAgent) myAgent).setReverseDirection(false);
    }
    
    // --- Getters and setters
    
    private Direction getCurrentDirection()
    {
        return ((GhostAgent) myAgent).getCurrentDirection();
    }
    
    private void setCurrentDirection(Direction direction)
    {
        ((GhostAgent) myAgent).setCurrentDirection(direction);
    }
    
    private Direction getLastDirection()
    {
        return ((GhostAgent) myAgent).getLastDirection();
    }
    
    private void setLastDirection(Direction direction)
    {
        ((GhostAgent) myAgent).setLastDirection(direction);
    }
}
 