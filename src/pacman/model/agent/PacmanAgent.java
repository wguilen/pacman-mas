package pacman.model.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import pacman.model.behaviour.PacmanLifecycleBehaviour;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.Direction;
import pacman.model.board.PacmanCell;
import pacman.model.core.Constant;
import pacman.model.core.GameVocabulary;

public class PacmanAgent extends Agent
{
    
    private Board board;
    private Cell myCell;
    
    // Game control properties
    private boolean gameRunning;            // TRUE when the game is running - FALSE otherwise
    private Direction currentDirection;     // Tracks the current direction being followed by Pacman
    private Direction lastDirection;        // Tracks the last direction followed by Pacman (actually, it's currentDirection.getReverse())
    private boolean moving;                 // TRUE if Pacman is moving now - FALSE otherwise
    private int powerupRemainingTurns;      // Tracks the quantity of turns Pacman still is with a powerup
    
    @Override
    protected void setup()
    {
        // Gets the board model
        board = (Board) getArguments()[0];
        
        // Positions the agent on the board
        Cell house = (Cell) getArguments()[1];
        myCell = new PacmanCell(this, house);
        board.setCell(myCell);
        
        // Inits the control properties
        gameRunning = false;
        moving = false;
        currentDirection = lastDirection = null;
        powerupRemainingTurns = 10000;
        
        // Adds its behaviour
        addBehaviour(new PacmanLifecycleBehaviour(this, board, myCell)); // CyclicBehaviour
        
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
    
    
    // --- Getters and setters
    
    public Cell getBoardCell()
    {
        return myCell;
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

    public boolean isMoving()
    {
        return moving;
    }

    public void setMoving(boolean moving)
    {
        this.moving = moving;
    }

    public int getPowerupRemainingTurns()
    {
        return powerupRemainingTurns;
    }

    public void setPowerupRemainingTurns(int powerupRemainingTurns)
    {
        this.powerupRemainingTurns = powerupRemainingTurns;
    }
    
    public void decreasePowerupRemainingTurns()
    {
        powerupRemainingTurns -= (powerupRemainingTurns > 0 ? 1 : 0);
    }
    
    
    // --- Public auxiliary methods
    
    public boolean isPowerfull()
    {
        return powerupRemainingTurns > 0;
    }
    
        
    // --- Overriden public methods
    
    @Override
    public String toString()
    {
        return getLocalName();
    }

}
