package pacman.model.behaviour;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    
    // Control properties
    protected final boolean gameRunning; // TODO: Quando o Pacman for morto, setar o gameRunning = true (pegar o gameRunning do agente que, por sua vez, receberá do GameAgent através de mensagem)
    private Direction currentDirection;
    private Direction lastDirection;

    public GhostMovementBehaviour(Agent agent, Board board, Cell myCell)
    {
        super(agent, Constant.MOVEMENT_DELAY);
        
        this.board = board;
        this.myCell = myCell;
        
        // Inits the control properties
        currentDirection = lastDirection = null;
        gameRunning = true; // TODO: Change this
    }
    
    
    @Override
    public void onTick()
    {
        // If the game is not running or the ghost still didn't 
        //      left his house, doesn't move
        if (!(gameRunning && ((GhostAgent) myAgent).isHouseLeft()))
        {
            //System.out.println(myAgent.getLocalName() + " still didn't left his house...");
            return;
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
            if (null == currentDirection)
            {
                // Selects a direction to follow
                for (Direction direction : Direction.values())
                {
                    myNewPosition = getNewPosition(myPosition, direction);
                    nearCell = board.getCell(myNewPosition);

                    // If it's a valid cell and the direction is not a reverse one, selects it
                    if (isValidDestination(nearCell) && (lastDirection != direction || attemps >= 3))
                    {
                        cellSelected = true;
                        currentDirection = direction;
                        lastDirection = currentDirection.getReverse();
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
                    if (direction.equals(currentDirection) 
                            || direction.equals(lastDirection))
                    {
                        continue;
                    }
                    
                    myNewPosition = getNewPosition(myPosition, direction);
                    nearCell = board.getCell(myNewPosition);
                    
                    // Found a valid bifurcation
                    if (isValidDestination(nearCell))
                    {
                        boolean changeDirection = new Random().nextBoolean();
                        
                        // May I follow it?
                        if (changeDirection)
                        {
                            cellSelected = true;
                            currentDirection = direction;
                            lastDirection = currentDirection.getReverse();
                            
                            //System.out.println("Changing direction");
                            
                            break;
                        }
                    }
                }
                
                // Else, tries to keep following the previous direction
                if (!cellSelected)
                {
                    myNewPosition = getNewPosition(myPosition, currentDirection);
                    nearCell = board.getCell(myNewPosition);

                    // If the direction being followed is no longer valid, selects another
                    if (!isValidDestination(nearCell))
                    {
                        currentDirection = null;
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
        // TODO: Continue from here...
        List<GhostAgent> ghosts = new ArrayList<>();
        
        // Checks the row (from my position to the beggining of the board)
        for (int i = myCell.getPosition().x; i > 0; --i)
        {
            Cell cell = board.getCell(new Coord2D(i, myCell.getPosition().y));
            
            if (!isValidDestination(cell))
            {
                break;
            }
            
            if (cell.getType().equals(CellType.GHOST))
            {
                ghosts.add(((GhostCell) cell).getAgent());
            }
        }
        
        // Checks the row (from my position to the end of the board)
        for (int i = myCell.getPosition().x; i < board.countRows(); ++i)
        {
            Cell cell = board.getCell(new Coord2D(i, myCell.getPosition().y));
            
            if (!isValidDestination(cell))
            {
                break;
            }
            
            if (cell.getType().equals(CellType.GHOST))
            {
                ghosts.add(((GhostCell) cell).getAgent());
            }
        }
        
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

}
 