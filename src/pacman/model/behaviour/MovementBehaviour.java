package pacman.model.behaviour;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.Direction;
import pacman.model.core.Constant;

public class MovementBehaviour extends TickerBehaviour
{

    private final Board board;
    private final Cell myCell;
    private boolean gameRunning; // TODO: Quando o Pacman for morto, setar o gameRunning = true

    public MovementBehaviour(Agent agent, Board board, Cell myCell)
    {
        super(agent, Constant.MOVEMENT_DELAY);
        
        this.board = board;
        this.myCell = myCell;
        
        gameRunning = false;
    }

    @Override
    protected void onTick()
    {
        if (!gameRunning) 
        {
            return;
        }
        
        System.out.println("Move!");
        Direction lastDirection = null;
    }
    
}
