package pacman.model.agent;

import jade.core.Agent;
import jade.wrapper.AgentController;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacman.model.behaviour.GameLifecycleBehaviour;
import pacman.model.behaviour.GameLoadBehaviour;
import pacman.model.behaviour.GameMovementBehaviour;
import pacman.model.behaviour.GameStartBehaviour;
import pacman.model.behaviour.GameTogglePauseBehaviour;
import pacman.model.board.Board;
import pacman.model.observer.GameListener;
import pacman.view.GameGui;

public class GameAgent extends Agent
{

    private Board board;
    private GameGui myGui;
    
    // Game agents controllers
    private final List<AgentController> ghostsControllers;
    private AgentController pacmanController;
    
    // Game agents
    //private final List<GhostAgent> ghosts;
    
    // Observers
    private final List<GameListener> observers;
    
    // Game control properties
    private boolean gameRunning;    // TRUE if the game has already started and is running - FALSE otherwise
    private int movedCounter;       // Tracks the quantity of agents the has done their movement on the board
    private boolean turnComplete;   // Tracks if a complete turn of the game was made
    
    // --- Ctors

    public GameAgent()
    {
        // Game agents controllers
        ghostsControllers = new ArrayList<>();
        observers = new ArrayList<>();
        
        // Game agents
        //ghosts = new ArrayList<>();
        
        // Inits game control properties
        gameRunning = false;
        movedCounter = 0;
        turnComplete = true;
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
        //addBehaviour(new GameGuiBehaviour(this));
        addBehaviour(new GameLifecycleBehaviour());
        addBehaviour(new GameMovementBehaviour(this));
        addBehaviour(new GameStartBehaviour(this, board));
    }
    
    public void togglePause()
    {
        addBehaviour(new GameTogglePauseBehaviour());
    }
    
    
    // --- Getters and setters
    
    
    public void addGhost(AgentController ghost)
    {
        ghostsControllers.add(ghost);
    }

    public List<AgentController> getGhosts()
    {
        return ghostsControllers;
    }

    public AgentController getPacman()
    {
        return pacmanController;
    }

    public void setPacman(AgentController pacman)
    {
        this.pacmanController = pacman;
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

    public void setGameRunning(boolean gameRunning)
    {
        this.gameRunning = gameRunning;
    }

    public boolean isGameRunning()
    {
        return gameRunning;
    }

    public int getMovedCounter()
    {
        return movedCounter;
    }

    public void resetMovedCounter()
    {
        movedCounter = 0;
    }
    
    public void incrementMovedCounter()
    {
        ++movedCounter;
    }

    public boolean isTurnComplete()
    {
        return turnComplete;
    }

    public void setTurnComplete(boolean turnComplete)
    {
        this.turnComplete = turnComplete;
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
