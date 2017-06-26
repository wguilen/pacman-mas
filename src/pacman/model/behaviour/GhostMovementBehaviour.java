package pacman.model.behaviour;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import pacman.model.agent.GhostAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.CellType;
import pacman.model.board.Coord2D;
import pacman.model.board.Direction;
import pacman.model.core.Constant;

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
        if (!(gameRunning && ((GhostAgent) myAgent).isHouseLeft()))
        {
            //System.out.println(myAgent.getLocalName() + " still didn't left his house...");
            return;
        }

        boolean cellSelected = false;
        
        Coord2D myPosition = myCell.getPosition();
        Coord2D myNewPosition = null;
        Cell nearCell = null;

        do 
        {
            // If no direction is being followed, selects one
            if (null == currentDirection)
            {
                // Selects a direction to follow
                for (Direction direction : Direction.values())
                {
                    myNewPosition = new Coord2D(myPosition.x + direction.xInc, myPosition.y + direction.yInc);
                    nearCell = board.getCell(myNewPosition);

                    // If it's a valid cell and the direction is not a reverse one, selects it
                    if (isValidCell(nearCell) && lastDirection != direction)
                    {
                        cellSelected = true;
                        currentDirection = direction;
                        switch (currentDirection)
                        {
                            case DOWN:
                                lastDirection = Direction.UP;
                                break;
                            
                            case UP:
                                lastDirection = Direction.DOWN;
                                break;
                                
                            case LEFT:
                                lastDirection = Direction.RIGHT;
                                break;
                                
                            case RIGHT:
                                lastDirection = Direction.LEFT;
                                break;
                        }
                        
                        break;
                    }
                }
            }
            // Tries to keep following this direction
            else
            {
                myNewPosition = new Coord2D(myPosition.x + currentDirection.xInc, myPosition.y + currentDirection.yInc);
                nearCell = board.getCell(myNewPosition);
                
                if (!isValidCell(nearCell))
                {
                    currentDirection = null;
                }
                else
                {
                    cellSelected = true;
                }
            }
        } while (!cellSelected);
        
        board.moveCell(myCell, myNewPosition);
    }
    
    private boolean isValidCell(Cell cell)
    {
        return CellType.DOOR != cell.getType()             // Cannot run to a door
               && CellType.GHOST_HOUSE != cell.getType()   // Neither to a ghost house
               && CellType.GHOST != cell.getType()         // Neither to another ghost
               && CellType.WALL != cell.getType();         // Neither to a wall
    }

}
 