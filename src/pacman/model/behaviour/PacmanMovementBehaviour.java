package pacman.model.behaviour;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import pacman.model.agent.PacmanAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.CellType;
import pacman.model.board.Coord2D;
import pacman.model.board.Direction;
import pacman.model.core.Constant;
import pacman.model.core.GameVocabulary;

public class PacmanMovementBehaviour extends BaseMovementBehaviour
{

    public PacmanMovementBehaviour(ACLMessage originMessage, Board board, Cell myCell)
    {
        super(originMessage, board, myCell);
    }

    @Override
    public void action()
    {
        // If the game is not running or Pacman is marked
        //      for death, doesn't move
        if (!(                                              // If not
                ((PacmanAgent) myAgent).isGameRunning()     // ... game is running
                && !((PacmanAgent) myAgent).isShouldDie()   // ... Pacman is not marked for death
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
            move();                 // Pacman makes a movement
            checkGhostInSamePath(); // Pacman checks if there is a ghost in the same path as it
        }
    }

    private void move()
    {
        boolean cellSelected = false;

        Coord2D myPosition = myCell.getPosition();
        Coord2D myNewPosition = null;
        Cell nearCell = null;

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
                // If Pacman is not following any ghost and bifurcation was found, 
                //      decides to follow it or not
                if (!((PacmanAgent) myAgent).isFollowingDirection())
                {
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
                            if (changeDirection <= Constant.PACMAN_TURN_ON_BIFURCATION_CHANCE)
                            {
                                cellSelected = true;
                                setCurrentDirection(direction);
                                setLastDirection(direction.getReverse());
                                break;
                            }
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
        
        // Handles possible collision with a ghost
        handlePacmanCollision(nearCell);
        
        // Handles powerups
        handlePowerup(nearCell);
        
        // Handles game ends (in the case Pacman won it)
        handleGameEnd();
        
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

    private void checkGhostInSamePath()
    {
        Coord2D myPosition = myCell.getPosition();
        boolean followingDirection = false;
        boolean turnBack = false;
        boolean isPowerful = ((PacmanAgent) myAgent).isPowerfull();
        
        // Fetches ghosts current position
        List<Coord2D> ghostsPositions = new ArrayList<>();
        board.getGhosts().forEach(ghost -> ghostsPositions.add(ghost.getBoardCell().getPosition()));
        
        switch (getCurrentDirection())
        {
            case UP:
                for (Coord2D ghostPosition : ghostsPositions) 
                {
                    // Ghost is in the same column as me
                    if (ghostPosition.y == myPosition.y)
                    {
                        followingDirection = true;
                        
                        // Ghost is bellow me
                        if (myPosition.x < ghostPosition.x)
                        {
                            turnBack = isPowerful;
                            
                            if (turnBack)
                            {
                                for (int i = myPosition.x + 1; i < ghostPosition.x; ++i)
                                {
                                    Cell cell = board.getCell(new Coord2D(i, myPosition.y));
                                    if (!isValidDestination(cell))
                                    {
                                        followingDirection = false;
                                        break;
                                    }
                                }
                            }
                        }
                        // Ghost is uppon me
                        else
                        {
                            turnBack = !isPowerful;
                            
                            if (!turnBack)
                            {
                                for (int i = myPosition.x - 1; i > ghostPosition.x; --i)
                                {
                                    Cell cell = board.getCell(new Coord2D(i, myPosition.y));
                                    if (!isValidDestination(cell))
                                    {
                                        followingDirection = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                
                break;
                
            case RIGHT:
                for (Coord2D ghostPosition : ghostsPositions) 
                {
                    // Ghost is in the same row as me
                    if (ghostPosition.x == myPosition.x)
                    {
                        followingDirection = true;
                        
                        // I'm in front of the ghost
                        if (myPosition.y > ghostPosition.y)
                        {
                            turnBack = isPowerful;
                            
                            if (turnBack)
                            {
                                for (int i = myPosition.y - 1; i > ghostPosition.y; --i)
                                {
                                    Cell cell = board.getCell(new Coord2D(myPosition.x, i));
                                    if (!isValidDestination(cell))
                                    {
                                        followingDirection = false;
                                        break;
                                    }
                                }
                            }
                        }
                        // Ghost is at my front
                        else
                        {
                            turnBack = !isPowerful;
                            
                            if (!turnBack)
                            {
                                for (int i = myPosition.y + 1; i < ghostPosition.y; ++i)
                                {
                                    Cell cell = board.getCell(new Coord2D(myPosition.x, i));
                                    if (!isValidDestination(cell))
                                    {
                                        followingDirection = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                
                break;
                
            case DOWN:
                for (Coord2D ghostPosition : ghostsPositions) 
                {
                    // Ghost is in the same column as me
                    if (ghostPosition.y == myPosition.y)
                    {
                        followingDirection = true;
                        
                        // Ghost is uppon me
                        if (myPosition.x > ghostPosition.x)
                        {
                            turnBack = isPowerful;
                            
                            if (turnBack)
                            {
                                for (int i = myPosition.x - 1; i > ghostPosition.x; --i)
                                {
                                    Cell cell = board.getCell(new Coord2D(i, myPosition.y));
                                    if (!isValidDestination(cell))
                                    {
                                        followingDirection = false;
                                        break;
                                    }
                                }
                            }
                        }
                        // Ghost is bellow me
                        else
                        {
                            turnBack = !isPowerful;
                            
                            if (!turnBack)
                            {
                                for (int i = myPosition.x + 1; i < ghostPosition.x; ++i)
                                {
                                    Cell cell = board.getCell(new Coord2D(i, myPosition.y));
                                    if (!isValidDestination(cell))
                                    {
                                        followingDirection = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                
                break;
                
            case LEFT:
                for (Coord2D ghostPosition : ghostsPositions) 
                {
                    // Ghost is in the same row as me
                    if (ghostPosition.x == myPosition.x)
                    {
                        followingDirection = true;
                        
                        // Ghost is at my front
                        if (myPosition.y < ghostPosition.y)
                        {
                            turnBack = isPowerful;
                            
                            if (turnBack)
                            {
                                for (int i = myPosition.y + 1; i < ghostPosition.y; ++i)
                                {
                                    Cell cell = board.getCell(new Coord2D(myPosition.x, i));
                                    if (!isValidDestination(cell))
                                    {
                                        followingDirection = false;
                                        break;
                                    }
                                }
                            }
                        }
                        // I'm in front of the ghost
                        else
                        {
                            turnBack = !isPowerful;
                            
                            if (!turnBack)
                            {
                                for (int i = myPosition.y - 1; i > ghostPosition.y; --i)
                                {
                                    Cell cell = board.getCell(new Coord2D(myPosition.x, i));
                                    if (!isValidDestination(cell))
                                    {
                                        followingDirection = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                
                break;
        }
        
        if (followingDirection && turnBack)
        {
            setLastDirection(getCurrentDirection());
            setCurrentDirection(getCurrentDirection().getReverse());
        }
        
        ((PacmanAgent) myAgent).setFollowingDirection(followingDirection);
    }
    
    private void handleGameEnd()
    {
        // If the game still has remaining ghosts and collectibles,
        //  Pacman still didn't won the game
        int remainingGhosts = (int) board.getGhosts()
                                        .stream()
                                        .filter(ghost -> !ghost.isShouldDie())
                                        .count();
        
        if (0 < remainingGhosts && board.hasRemainingCollectibles())
        {
            return;
        }

        // Else, informs Game agent the game has been won (and should end)
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setOntology(GameVocabulary.ONTOLOGY);
        message.setContent(GameVocabulary.END_PACMAN_WINS);
        message.addReceiver(new AID(Constant.GAME_AGENT_NAME, AID.ISLOCALNAME));
        myAgent.send(message);
    }
    
    
    // --- Private auxiliary methods
    
    private void handlePowerup(Cell cell)
    {
        // First, decreases a turn from Pacman's powerup
        ((PacmanAgent) myAgent).decreasePowerupRemainingTurns();
        
        System.out.println("Powerup state is " + ((PacmanAgent) myAgent).getPowerupRemainingTurns());
                
        // Then, checks if he got another powerup
        if (null == cell || 
                !cell.getType().equals(CellType.POWERUP))
        {
            return;
        }
        
        int currentPowerupTurns = ((PacmanAgent) myAgent).getPowerupRemainingTurns();
        ((PacmanAgent) myAgent).setPowerupRemainingTurns(currentPowerupTurns + Constant.PACMAN_POWERUP_TURNS);
    }

    
    // --- Getters and setters
    
    @Override
    protected Direction getCurrentDirection()
    {
        return ((PacmanAgent) myAgent).getCurrentDirection();
    }

    @Override
    protected void setCurrentDirection(Direction direction)
    {
        ((PacmanAgent) myAgent).setCurrentDirection(direction);
    }

    @Override
    protected Direction getLastDirection()
    {
        return ((PacmanAgent) myAgent).getLastDirection();
    }

    @Override
    protected void setLastDirection(Direction direction)
    {
        ((PacmanAgent) myAgent).setLastDirection(direction);
    }

}
