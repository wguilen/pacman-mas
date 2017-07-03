package pacman.model.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import pacman.model.behaviour.GhostLifecycleBehaviour;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.Direction;
import pacman.model.board.GhostCell;
import pacman.model.core.Constant;
import pacman.model.core.GameVocabulary;
import pacman.model.core.GhostVocabulary;

public class GhostAgent extends Agent
{

    private Board board;
    private Cell myCell;
    
    // Game control properties
    private boolean houseLeft;              // TRUE when the ghost has left his house - FALSE otherwise
    private boolean gameRunning;            // TRUE when the game is running - FALSE otherwise
    private Direction currentDirection;     // Tracks the current direction being followed by the ghost
    private Direction lastDirection;        // Tracks the last direction followed by the ghost (actually, it's currentDirection.getReverse())
    private boolean reverseDirection;       // TRUE if ghost receives "GET_OUT_OF_MY_WAY" from another ghost - FALSE otherwise
    private boolean moving;                 // TRUE if the ghost is moving now - FALSE otherwise
    private boolean shouldDie;              // TRUE if the ghost was caught by Pacman - FALSE otherwise
    
    
    // --- Public overriden methods
    
    @Override
    protected void setup()
    {
        // Gets the board model
        board = (Board) getArguments()[0];
        
        // Positions the agent on the board
        Cell house = (Cell) getArguments()[1];
        myCell = new GhostCell(this, house);
        board.setCell(myCell);
        
        // Inits the control properties
        houseLeft = false;
        gameRunning = false;
        reverseDirection = false;
        moving = false;
        shouldDie = false;
        currentDirection = lastDirection = null;
        
        // Adds its behaviour
        addBehaviour(new GhostLifecycleBehaviour(this, board, myCell)); // CyclicBehaviour
        
        // Notifies game agent I'm loaded
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setOntology(GameVocabulary.ONTOLOGY);
        message.setContent(GameVocabulary.AGENT_INITIALIZED);
        message.addReceiver(new AID(Constant.GAME_AGENT_NAME, AID.ISLOCALNAME));
        send(message);
    }

    @Override
    protected void takeDown()
    {
        board.removeAgentCell(myCell);
        System.out.println(getLocalName() + ": Goodbye, cruel world...");
        
        super.takeDown();
    }
    
    
    // --- Public auxiliary methods
    
    public void die()
    {
        // Marks the ghost for dying
        setShouldDie(true);
        
        // Notifies other ghosts so they can run
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setOntology(GhostVocabulary.ONTOLOGY);
        message.setContent(GhostVocabulary.THE_MOTHERFUCKER_KILLED_ME);
        board.getGhosts()
                .stream()
                .filter(ghost -> !ghost.equals(this))
                .forEach(ghost -> message.addReceiver(ghost.getAID()));
        send(message);
        
        // Notifies GameAgent I'm dead
        message.clearAllReceiver();
        message.setOntology(GameVocabulary.ONTOLOGY);
        message.setContent(GameVocabulary.GHOST_KILLED);
        message.addReceiver(new AID(Constant.GAME_AGENT_NAME, AID.ISLOCALNAME));
        send(message);
        
        // Effectively dies
        doDelete();
    }
    
    
    // --- Getters and setters
    
    public Cell getBoardCell()
    {
        return myCell;
    }

    public boolean isHouseLeft()
    {
        return houseLeft;
    }

    public void setHouseLeft(boolean houseLeft)
    {
        this.houseLeft = houseLeft;
    }

    public boolean isGameRunning()
    {
        return gameRunning;
    }

    public void setGameRunning(boolean gameRunning)
    {
        this.gameRunning = gameRunning;
    }

    public Direction getCurrentDirection()
    {
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection)
    {
        this.currentDirection = currentDirection;
    }

    public Direction getLastDirection()
    {
        return lastDirection;
    }

    public void setLastDirection(Direction lastDirection)
    {
        this.lastDirection = lastDirection;
    }

    public boolean isReverseDirection()
    {
        return reverseDirection;
    }

    public void setReverseDirection(boolean reverseDirection)
    {
        this.reverseDirection = reverseDirection;
    }

    public boolean isMoving()
    {
        return moving;
    }

    public void setMoving(boolean moving)
    {
        this.moving = moving;
    }

    public boolean isShouldDie()
    {
        return shouldDie;
    }

    public void setShouldDie(boolean shouldDie)
    {
        this.shouldDie = shouldDie;
    }
    
    
    // --- Overriden public methods
    
    @Override
    public String toString()
    {
        return getLocalName();
    }

}
