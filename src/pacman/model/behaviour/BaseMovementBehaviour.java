package pacman.model.behaviour;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import pacman.model.agent.GhostAgent;
import pacman.model.agent.PacmanAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.CellType;
import pacman.model.board.Coord2D;
import pacman.model.board.Direction;
import pacman.model.board.PacmanCell;
import pacman.model.core.Constant;
import pacman.model.core.GameVocabulary;
import pacman.model.core.GhostVocabulary;

public abstract class BaseMovementBehaviour extends SimpleBehaviour
{

    protected final Board board;
    protected final Cell myCell;
    protected final ACLMessage originMessage;

    // Game control properties
    protected boolean moved;      // Tracks if Pacman has done its movement

    public BaseMovementBehaviour(ACLMessage originMessage, Board board, Cell myCell)
    {
        this.board = board;
        this.myCell = myCell;
        this.originMessage = originMessage;

        // Inits the game control properties
        moved = false;
    }

    @Override
    public boolean done()
    {
        return moved;
    }

    protected boolean isValidDestination(Cell cell)
    {
        return CellType.DOOR != cell.getType()              // Cannot run to a door
               && CellType.GHOST_HOUSE != cell.getType()    // Neither to a ghost house
               && CellType.WALL != cell.getType();          // Neither to a wall
    }

    protected Coord2D getNewPosition(Coord2D currentPosition, Direction destination)
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
    
    protected void handlePacmanCollision(Cell cell)
    {
        if (!(cell instanceof PacmanCell))
        {
            return;
        }
        
        // Ghost found Pacman
        if (myAgent instanceof GhostAgent)
        {
            PacmanAgent pacman = ((PacmanCell) cell).getAgent();

            // Ghost dies
            if (pacman.isPowerfull())
            {
                // Notifies other ghosts so they can run
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.setOntology(GhostVocabulary.ONTOLOGY);
                message.setContent(GhostVocabulary.THE_MOTHERFUCKER_KILLED_ME);
                board.getGhosts()
                        .stream()
                        .filter(ghost -> !ghost.equals(((GhostAgent) myAgent)))
                        .forEach(ghost -> message.addReceiver(ghost.getAID()));
                myAgent.send(message);

                // Marks the ghost for dying
                ((GhostAgent) myAgent).setShouldDie(true);
            }
            // Pacman dies
            else
            {
                // Notifies GameAgent so the game ends
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.setOntology(GameVocabulary.ONTOLOGY);
                message.setContent(GameVocabulary.END_GHOSTS_WIN);
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
        // Pacman found ghost
        /*else if (myAgent instanceof PacmanAgent)
        {
            System.out.println("Pacman found a ghost!");
        }*/
    }

    
    // --- Getters and setters
    
    protected abstract Direction getCurrentDirection();
    protected abstract void setCurrentDirection(Direction direction);
    protected abstract Direction getLastDirection();
    protected abstract void setLastDirection(Direction direction);

}
