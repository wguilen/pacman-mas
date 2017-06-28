package pacman.model.agent;

import jade.core.Agent;
import jade.wrapper.AgentController;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacman.model.behaviour.GameLoadBehaviour;
import pacman.model.behaviour.GameGuiBehaviour;
import pacman.model.behaviour.GameStartBehaviour;
import pacman.model.board.Board;
import pacman.model.observer.GameListener;
import pacman.view.GameGui;

public class GameAgent extends Agent
{

    private Board board;
    private GameGui myGui;
    
    // Game agents
    private final List<AgentController> ghosts;
    private AgentController pacman;

    // Observers
    private final List<GameListener> observers;
    
    
    // --- Ctors

    public GameAgent()
    {
        ghosts = new ArrayList<>();
        observers = new ArrayList<>();
    }
    
    
    // --- Protected overriden methods
    
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
        addObserver(myGui);
    }

    
    // --- Public methods
    
    public void startGame()
    {
        addBehaviour(new GameGuiBehaviour(this));
        addBehaviour(new GameStartBehaviour(this, board));
    }
    
    
    // --- Getters and setters
    
    
    public void addGhost(AgentController ghost)
    {
        ghosts.add(ghost);
    }

    public List<AgentController> getGhosts()
    {
        return ghosts;
    }

    public AgentController getPacman()
    {
        return pacman;
    }

    public void setPacman(AgentController pacman)
    {
        this.pacman = pacman;
    }
    
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

    public void addObserver(GameListener observer)
    {
        if (!observers.contains(observer))
        {
            observers.add(observer);
        }
    }
    
    public List<GameListener> getObservers()
    {
        return observers;
    }

}
