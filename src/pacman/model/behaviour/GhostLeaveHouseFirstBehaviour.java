package pacman.model.behaviour;

import pacman.model.agent.GhostAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.Coord2D;
import pacman.model.board.Direction;
import pacman.model.core.Constant;

public class GhostLeaveHouseFirstBehaviour extends GhostLeaveHouseBehaviour
{
    
    public GhostLeaveHouseFirstBehaviour(Board board, Cell myCell)
    {
        super(board, myCell);
    }

    @Override
    public void action()
    {
        // Delays a time before the first ghost leaves his house
        try { Thread.sleep(Constant.MOVEMENT_DELAY); } catch (InterruptedException ex) {}
                        
        // Tries to leave the house
        Coord2D doorPosition = board.getGhostDoor().getPosition();
        Coord2D myNewPosition = null;
        int x = myCell.getPosition().x,
            y = myCell.getPosition().y;

        for (Direction direction : Direction.values())
        {
            if (doorPosition.equals(myNewPosition = new Coord2D(x + direction.xInc, y + direction.yInc)))
            {
                System.out.println(myAgent.getLocalName() + " leaving house by " + direction.toString().toLowerCase());
                break;
            }

            myNewPosition = null; // Resets the position to null so the others ghosts don't try to use it
        }

        if (null != myNewPosition)
        {
            // Leaves the house
            ((GhostAgent) myAgent).setHouseLeft(true);
            board.moveCell(myCell, myNewPosition);
            board.print();
            
            // Advices the next ghost (if any) to leave his house
            adviceNextGhost();
            
            // Delays a time before the first ghost leaves the door
            try { Thread.sleep(Constant.MOVEMENT_DELAY); } catch (InterruptedException ex) {}
        }
    }
    
}
