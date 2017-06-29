package pacman.model.behaviour;

import jade.core.Agent;
import pacman.model.agent.GhostAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.CellType;
import pacman.model.board.Coord2D;
import pacman.model.board.Direction;
import pacman.model.core.Constant;

public class GhostLeaveHouseNextBehaviour extends GhostLeaveHouseBehaviour
{
    
    // Game control properties
    private boolean startMoving;    // TRUE if the initial house delay time has finished - FALSE otherwise

    public GhostLeaveHouseNextBehaviour(Agent agent, Board board, Cell cell)
    {
        super(agent, board, cell);
    }

    @Override
    public void onStart()
    {
        startMoving = false;
        
        // Delays a time before starts moving
        try { Thread.sleep(Constant.GHOST_LEAVE_HOUSE_DELAY); } catch (InterruptedException ex) {}
        
        startMoving = true;
    }
    
    
    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void onTick()
    {
        if (!(((GhostAgent) myAgent).isGameRunning()) && startMoving 
                && !((GhostAgent) myAgent).isHouseLeft())
        {
            return;
        }
                    
        Coord2D myPosition = myCell.getPosition();
        Coord2D doorPosition = board.getGhostDoor().getPosition();
        
        for (Direction direction : Direction.values())
        {
            Coord2D myNewPosition = new Coord2D(myPosition.x + direction.xInc, myPosition.y + direction.yInc);

            // Found the door
            if (doorPosition.equals(myNewPosition))
            {
                // Leaves the house
                System.out.println(myAgent.getLocalName() + " leaving house by " + direction.toString().toLowerCase());
                board.moveCell(myCell, myNewPosition);
                ((GhostAgent) myAgent).setHouseLeft(true);
                board.print();

                // Advices the next ghost (if any) to leave his house
                adviceNextGhost();
                myAgent.removeBehaviour(this);
                
                // Delays a time before the next ghost leaves the door
                try { Thread.sleep(Constant.MOVEMENT_DELAY * 2); } catch (InterruptedException ex) {}

                return;
            }
        }
        
        // Else, keeps looking for it
        Direction currentDirection = ((GhostAgent) myAgent).getCurrentDirection();
        for (Direction direction : Direction.values())
        {
            Coord2D myNewPosition = new Coord2D(myPosition.x + direction.xInc, myPosition.y + direction.yInc);
            
            Cell nearCell = board.getCell(myNewPosition);
            if (CellType.GHOST_HOUSE == nearCell.getType())
            {
                if (null == currentDirection)
                {
                    currentDirection = direction;
                    ((GhostAgent) myAgent).setCurrentDirection(direction);
                }

                if (currentDirection == direction)
                {
                    System.out.println(myAgent.getLocalName() + " is moving to " + direction);   
                    board.moveCell(myCell, myNewPosition);
                    board.print();

                    break;
                }
            }
        }
    }
    
}
