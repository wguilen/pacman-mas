package pacman.model.behaviour;

import jade.core.AID;
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
            move(); // Pacman makes a movement
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
                        if (changeDirection <= Constant.PACMAN_TURN_ON_BIFURCATION_CHANCE)
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
    
    @Override
    protected boolean isValidDestination(Cell cell)
    {
        return super.isValidDestination(cell)       // Cannot run to a door, ghost house or wall
               && CellType.GHOST != cell.getType(); // Neither to another ghost // TODO: Remove this and treat
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
