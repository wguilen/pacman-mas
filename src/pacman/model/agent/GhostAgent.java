package pacman.model.agent;

import jade.core.Agent;
import pacman.model.behaviour.GhostLeaveHouseBehaviour;
import pacman.model.behaviour.GhostMovementBehaviour;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.GhostCell;

public class GhostAgent extends Agent
{

    private Board board;
    private Cell myCell;
    
    // Game control properties
    private boolean houseLeft;
    
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
        
        // Adds its behaviour
        addBehaviour(new GhostLeaveHouseBehaviour(this, board, myCell));
        addBehaviour(new GhostMovementBehaviour(this, board, myCell));
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
    
    
    // --- Overriden public methods
    
    @Override
    public String toString()
    {
        return getLocalName();
    }

}