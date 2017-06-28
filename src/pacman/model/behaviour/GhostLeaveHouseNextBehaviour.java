package pacman.model.behaviour;

import pacman.model.agent.GhostAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.CellType;
import pacman.model.board.Coord2D;
import pacman.model.board.Direction;
import pacman.model.core.Constant;

public class GhostLeaveHouseNextBehaviour extends GhostLeaveHouseBehaviour
{
    
    public GhostLeaveHouseNextBehaviour(Board board, Cell myCell)
    {
        super(board, myCell);
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void action()
    {
        // Delays a time and starts to move
        try { Thread.sleep(Constant.GHOST_LEAVE_HOUSE_DELAY); } catch (InterruptedException ex) {}
                    
        Coord2D doorPosition = board.getGhostDoor().getPosition();
        Direction lastDirection = null;
        
        do 
        {
            for (Direction direction : Direction.values())
            {
                Coord2D myPosition = myCell.getPosition();
                Coord2D myNewPosition = new Coord2D(myPosition.x + direction.xInc, myPosition.y + direction.yInc);
                        
                // Found the door
                if (doorPosition.equals(myNewPosition))
                {
                    // Leaves the house
                    System.out.println(myAgent.getLocalName() + " leaving house by " + direction.toString().toLowerCase());
                    ((GhostAgent) myAgent).setHouseLeft(true);
                    board.moveCell(myCell, myNewPosition);
                    board.print();
            
                    // Advices the next ghost (if any) to leave his house
                    adviceNextGhost();
                    
                    // Delays a time before the next ghost leaves the door
                    try { Thread.sleep(Constant.MOVEMENT_DELAY); } catch (InterruptedException ex) {}
            
                    break;
                }
                // Else, keeps looking for it
                else
                {
                    Cell nearCell = board.getCell(myNewPosition);
                    if (CellType.GHOST_HOUSE == nearCell.getType())
                    {
                        if (null == lastDirection)
                        {
                            lastDirection = direction;
                        }

                        if (lastDirection == direction)
                        {
                            System.out.println(myAgent.getLocalName() + " is moving to " + direction);   
                            board.moveCell(myCell, myNewPosition);
                            board.print();    

                            // Delays a time after the ghost makes its movement
                            try { Thread.sleep(Constant.MOVEMENT_DELAY); } catch (InterruptedException ex) {}
                        }
                    }
                }
            }
        } while (!((GhostAgent) myAgent).isHouseLeft());
        
        myAgent.removeBehaviour(this);
    }
    
}
