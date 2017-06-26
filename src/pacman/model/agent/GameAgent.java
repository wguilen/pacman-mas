package pacman.model.agent;

import jade.core.Agent;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacman.model.behaviour.GameLoadBehaviour;
import pacman.model.behaviour.GameGuiBehaviour;
import pacman.model.behaviour.GameStartBehaviour;
import pacman.model.board.Board;
import pacman.view.GameGui;

public class GameAgent extends Agent
{

    private Board board;
    private GameGui myGui;
    
    // --- Ctors
    
    @Override
    protected void setup()
    {
        try
        {
            addBehaviour(new GameLoadBehaviour(this, (String) getArguments()[0]));
        } 
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(GameAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        myGui = new GameGui(this);
    }

    
    // --- Public methods
    
    public void startGame()
    {
        addBehaviour(new GameGuiBehaviour(this));
        addBehaviour(new GameStartBehaviour(this, board));
    }
    
    
    // --- Getters and setters
    
    
    public Board getBoard()
    {
        return board;
    }

    public void setBoard(Board board)
    {
        this.board = board;
    }

    public GameGui getGui()
    {
        return myGui;
    }

}
