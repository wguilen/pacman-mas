package pacman.model.behaviour;

import jade.core.AID;
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
import pacman.model.board.PacmanCell;
import pacman.model.core.Constant;
import pacman.model.core.GameVocabulary;
import pacman.model.core.GhostVocabulary;

public class GhostMovementBehaviour extends BaseMovementBehaviour
{

    // Control properties
    private boolean reverse;    // Tracks if the agent should reverse his direction if another ghost is going in the same direction and is near

    public GhostMovementBehaviour(ACLMessage originMessage, Board board, Cell myCell)
    {
        super(originMessage, board, myCell);

        // Inits the game control properties
        reverse = false;
    }

    @Override
    public void action()
    {
        // If the game is not running or the ghost still didn't 
        //      left his house, doesn't move
        if (!(                                              // If not
                ((GhostAgent) myAgent).isGameRunning()      // ... game is running
                && ((GhostAgent) myAgent).isHouseLeft()     // ... the ghost left its house
           ))
        {
            // Replies to the Game Agent denying this behaviour
            /*ACLMessage reply = originMessage.createReply();
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent(GameVocabulary.MOVED_MY_BODY);
            myAgent.send(reply);*/
        
            // Removes this behaviour from the ghost
            myAgent.removeBehaviour(this);
            
            return;
        }
        
        synchronized (this)
        {
            // If the ghost received "GET_OUT_MY_WAY", maybe it reverses the
            //      direction being followed
            if (((GhostAgent) myAgent).isReverseDirection())
            {
                maybeReverseDirection();
            }

            move();                 // Ghost makes a movement
            checkGhostOnSamePath(); // Ghost checks if are there another ghosts on the same path
        }
    }

    private void move()
    {
        boolean cellSelected = false;

        Coord2D myPosition = myCell.getPosition();
        Coord2D myNewPosition = null;
        Cell nearCell = null;

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

        // Checks if the ghost is about the kill Pacman
        checkPacmanHomicide(nearCell);
            
        // Effectively makes the movement
        board.moveCell(myCell, myNewPosition);
        moved = true;
        
        // Notifies GameAgent I've made my movement
        ACLMessage reply = originMessage.createReply();
        reply.setPerformative(ACLMessage.CONFIRM);
        reply.setContent(GameVocabulary.MOVED_MY_BODY);
        myAgent.send(reply);
        
        // Updates the GhostAgent state
        ((GhostAgent) myAgent).setMoving(false);
    }
    
    private void checkPacmanHomicide(Cell cell)
    {
        if (cell instanceof PacmanCell)
        {
            // Notifies GameAgent so the game ends
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.setOntology(GameVocabulary.ONTOLOGY);
            message.setContent(GameVocabulary.END);
            message.addReceiver(new AID(Constant.GAME_AGENT_NAME, AID.ISLOCALNAME));
            myAgent.send(message);
            
            // Notifies other ghosts so they can celebrate
            message.clearAllReceiver();
            message.setOntology(GhostVocabulary.ONTOLOGY);
            message.setContent(GhostVocabulary.THE_MOTHERFUCKER_IS_DEAD);
            board.getGhosts()
                    .stream()
                    .filter(ghost -> !ghost.equals(((GhostAgent) myAgent)))
                    .forEach(ghost -> message.addReceiver(ghost.getAID()));
            myAgent.send(message);
            
            // Notifies Pacman
            message.clearAllReceiver();
            message.setContent(GhostVocabulary.I_KILLED_YOU);
            message.addReceiver(board.getPacman().getAID());
            myAgent.send(message);
        }
    }

    private void checkGhostOnSamePath()
    {        
        List<GhostAgent> ghosts = new ArrayList<>();
        
        // Checks if there's another ghost in the same column as me
        // (from my position to the beggining of the board)
        for (int i = getNewPosition(myCell.getPosition(), Direction.UP).x; i > 0; --i)
        {
            Cell cell = board.getCell(new Coord2D(i, myCell.getPosition().y));
            
            addGhostOnOppositeDirection(ghosts, cell);
            checkGhostOnSameDirection(cell);
            
            if (!isValidDestination(cell)) 
            {
                break;
            }
        }
        
        // Checks if there's another ghost in the same column as me
        // (from my position to the end of the board)
        for (int i = getNewPosition(myCell.getPosition(), Direction.DOWN).x; i < board.countRows(); ++i)
        {
            Cell cell = board.getCell(new Coord2D(i, myCell.getPosition().y));
            
            addGhostOnOppositeDirection(ghosts, cell);
            checkGhostOnSameDirection(cell);
            
            if (!isValidDestination(cell)) 
            {
                break;
            }
        }
        
        // Checks if there's another ghost in the same row as me
        // (from my position to the beggining of the board)
        for (int i = getNewPosition(myCell.getPosition(), Direction.LEFT).y; i > 0; --i)
        {
            Cell cell = board.getCell(new Coord2D(myCell.getPosition().x, i));
            
            addGhostOnOppositeDirection(ghosts, cell);
            checkGhostOnSameDirection(cell);
            
            if (!isValidDestination(cell)) 
            {
                break;
            }
        }
        
        // Checks if there's another ghost in the same row as me
        // (from my position to the end of the board)
        for (int i = getNewPosition(myCell.getPosition(), Direction.RIGHT).y; i < board.countColumns(); ++i)
        {
            Cell cell = board.getCell(new Coord2D(myCell.getPosition().x, i));
            
            addGhostOnOppositeDirection(ghosts, cell);
            checkGhostOnSameDirection(cell);
            
            if (!isValidDestination(cell)) 
            {
                break;
            }
        }
        
        // I shall reverse my direction if another ghost is going on the same direction
        //      as me and is quite near
        if (reverse)
        {
            reverse = false;
            setLastDirection(getCurrentDirection());
            setCurrentDirection(getLastDirection().getReverse());
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

    private void addGhostOnOppositeDirection(List<GhostAgent> ghosts, Cell cell)
    {
        if (!cell.getType().equals(CellType.GHOST))
        {
            return;
        }

        GhostAgent other = ((GhostCell) cell).getAgent();

        // If the other ghost direction is the opposite of mine,
        //      I ask him for leaving my path
        if (null != getCurrentDirection()
                && null != other.getCurrentDirection()
                && other.getCurrentDirection().equals(getCurrentDirection().getReverse()))
        {
            ghosts.add(other);
        }
    }

    private void checkGhostOnSameDirection(Cell cell)
    {
        if (!cell.getType().equals(CellType.GHOST) || reverse)
        {
            return;
        }

        GhostAgent other = ((GhostCell) cell).getAgent();

        // If the other ghost is in the same direction as me and
        //      is near, I ask him for leaving my path
        if (null != getCurrentDirection()
                && null != other.getCurrentDirection()
                && other.getCurrentDirection().equals(getCurrentDirection()))
        {
            switch (other.getCurrentDirection())
            {
                case LEFT:
                case RIGHT:
                    int myY = myCell.getPosition().y;
                    int otherY = other.getBoardCell().getPosition().y;

                    if (Math.abs(myY - otherY) <= Constant.GHOST_RUN_FROM_ANOTHER_NEAR_DISTANCE)
                    {
                        reverse = true;
                    }

                    break;

                case UP:
                case DOWN:
                    int myX = myCell.getPosition().x;
                    int otherX = other.getBoardCell().getPosition().x;

                    if (Math.abs(myX - otherX) <= Constant.GHOST_RUN_FROM_ANOTHER_NEAR_DISTANCE)
                    {
                        reverse = true;
                    }

                    break;
            }
        }
    }

    @Override
    protected boolean isValidDestination(Cell cell)
    {
        return super.isValidDestination(cell)       // Cannot run to a door, ghost house or wall
               && CellType.GHOST != cell.getType(); // Neither to another ghost
    }

    private void maybeReverseDirection()
    {
        boolean shallReverse = ThreadLocalRandom.current().nextBoolean();
        if (shallReverse)
        {
            setLastDirection(getCurrentDirection());
            setCurrentDirection(getCurrentDirection().getReverse());

            //System.out.println(myAgent.getLocalName() + " reversed his direction");
        }

        ((GhostAgent) myAgent).setReverseDirection(false);
    }

    // --- Getters and setters
    @Override
    protected Direction getCurrentDirection()
    {
        return ((GhostAgent) myAgent).getCurrentDirection();
    }

    @Override
    protected void setCurrentDirection(Direction direction)
    {
        ((GhostAgent) myAgent).setCurrentDirection(direction);
    }

    @Override
    protected Direction getLastDirection()
    {
        return ((GhostAgent) myAgent).getLastDirection();
    }

    @Override
    protected void setLastDirection(Direction direction)
    {
        ((GhostAgent) myAgent).setLastDirection(direction);
    }

}
