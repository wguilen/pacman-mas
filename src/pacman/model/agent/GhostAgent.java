package pacman.model.agent;

import jade.core.Agent;
import pacman.model.behaviour.GhostLifecycleBehaviour;
import pacman.model.behaviour.GhostMovementBehaviour;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.Direction;
import pacman.model.board.GhostCell;

public class GhostAgent extends Agent
{

    private Board board;
    private Cell myCell;
    
    // Game control properties
    private boolean houseLeft;
    private boolean gameRunning; // TODO: Quando o Pacman for morto, setar o gameRunning = true (pegar o gameRunning do agente que, por sua vez, receberá do GameAgent através de mensagem)
    private Direction currentDirection;
    private Direction lastDirection;
    
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
        gameRunning = true; // TODO: Change this
        currentDirection = lastDirection = null;
        
        // Adds its behaviour
        addBehaviour(new GhostLifecycleBehaviour(this, board, myCell)); // CyclicBehaviour
        addBehaviour(new GhostMovementBehaviour(this, board, myCell));  // TickerBehaviour
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
    
    
    // --- Overriden public methods
    
    @Override
    public String toString()
    {
        return getLocalName();
    }

}