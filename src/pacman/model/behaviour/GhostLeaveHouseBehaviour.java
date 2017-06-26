package pacman.model.behaviour;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;
import pacman.model.agent.GhostAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.CellType;
import pacman.model.board.Coord2D;
import pacman.model.board.Direction;
import pacman.model.core.Constant;
import pacman.model.core.GameVocabulary;
import pacman.model.core.GhostVocabulary;

public class GhostLeaveHouseBehaviour extends CyclicBehaviour
{

    private final Board board;
    private final Cell myCell;

    public GhostLeaveHouseBehaviour(Agent agent, Board board, Cell cell)
    {
        super(agent);
        this.board = board;
        this.myCell = cell;
    }

    @Override
    public void action()
    {
        ACLMessage message = myAgent.receive();
        if (null != message && ACLMessage.INFORM == message.getPerformative())
        {
            System.out.println(myAgent.getLocalName() + " received " + message.getContent() + " from " + message.getSender().getLocalName());
            
            switch (message.getOntology())
            {
                case GameVocabulary.ONTOLOGY:
                    switch (message.getContent())
                    {
                        case GameVocabulary.START:
                            firstGhostToLeave();
                            break;
                            
                        default:
                            block();
                    }
                    
                    break;
                    
                case GhostVocabulary.ONTOLOGY:
                    switch (message.getContent())
                    {
                        case GhostVocabulary.LEAVE_THE_HOUSE:
                            nextGhostToLeave();
                            break;
                            
                        default:
                            block();
                    }
                    
                default:
                    block();
            }
        } 
        else
        {
            block();
        }
    }
    
    private void firstGhostToLeave()
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
        }
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    private void nextGhostToLeave()
    {
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
                    
                    break;
                }

                // Else, keeps looking for it
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
        } while (!((GhostAgent) myAgent).isHouseLeft());
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    private void adviceNextGhost()
    {
        // Selects the next ghost (if any) to leave the house and sends it a message
        List<GhostAgent> nextGhosts = new ArrayList<>(); // Just for a hack
        board.getGhosts().stream().filter((ghost)
                -> (!ghost.equals(myAgent)) && !ghost.isHouseLeft() && nextGhosts.isEmpty()).forEachOrdered((ghost) ->
        {
            Coord2D position = ghost.getBoardCell().getPosition();
            for (Direction direction : Direction.values())
            {
                Cell nearCell = board.getCell(new Coord2D(position.x + direction.xInc, position.y + direction.yInc));
                if (nearCell.getType() == CellType.GHOST_HOUSE)
                {
                    // Delays a time and advices the next ghost
                    try { Thread.sleep(Constant.GHOST_LEAVE_HOUSE_DELAY); } catch (InterruptedException ex) {}
                    
                    // Mounts the message and sends it to the next ghost to leave the house
                    ACLMessage nextGhostMessage = new ACLMessage(ACLMessage.INFORM);
                    nextGhostMessage.setOntology(GhostVocabulary.ONTOLOGY);
                    nextGhostMessage.setContent(GhostVocabulary.LEAVE_THE_HOUSE);
                    nextGhostMessage.addReceiver(ghost.getAID());
                    myAgent.send(nextGhostMessage);

                    nextGhosts.add(ghost); // It's added an element to nextGhosts just to break the forEachOrdered()
                }
            }
        });
    }

}
