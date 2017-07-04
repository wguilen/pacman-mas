package pacman.model.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacman.model.behaviour.GameGuiBehaviour;
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
    
    // Observers
    private final List<GameListener> observers;
    
    // Game control properties
    private int waitingInitialization;      // Tracks the number of agents that hasn't setup yet
    private boolean gameRunning;            // TRUE if the game has already started and is running - FALSE otherwise
    private final List<AID> agentsToMove;   // Tracks the agents the has to do their movement on the board before the turn ends
    private boolean turnComplete;           // Tracks if a complete turn of the game was made
    private boolean gameEnded;              // TRUE if the game has already ended - FALSE otherwise
    
    // --- Ctors

    public GameAgent()
    {
        // Game agents controllers
        ghostsControllers = new ArrayList<>();
        observers = new ArrayList<>();
        
        // Inits game control properties
        waitingInitialization = 0;
        gameRunning = false;
        gameEnded = false;
        turnComplete = true;
        agentsToMove = new ArrayList<>();
    }
    
    
    // --- Protected overriden methods
    
    @Override
    protected void setup()
    {
        addBehaviour(new GameLifecycleBehaviour());
        
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

    @Override
    protected void takeDown()
    {
        // Kills remaining ghosts
        ghostsControllers.forEach(ghostController ->
        {
            try
            {
                ghostController.kill();
            } 
            catch (StaleProxyException ex) {}
        });
        
        // Kills Pacman
        try
        {
            pacmanController.kill();
        } 
        catch (StaleProxyException ex) {}
        
        // Ends the game
        System.out.println("Game agent is dead...");        
        ((GameListener) myGui).dispose();
    }
    
    
    // --- Public methods
    
    public void startGame()
    {
        addBehaviour(new GameGuiBehaviour(this));
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

    public int getWaitingInitialization()
    {
        return waitingInitialization;
    }

    public void incrementWaitingInitialization()
    {
        ++waitingInitialization;
    }
    
    public void decrementWaitingInitialization()
    {
        --waitingInitialization;
    }
    
    public void setGameRunning(boolean gameRunning)
    {
        this.gameRunning = gameRunning;
    }

    public boolean isGameRunning()
    {
        return gameRunning;
    }

    public boolean isGameEnded()
    {
        return gameEnded;
    }

    public void setGameEnded(boolean gameEnded)
    {
        this.gameEnded = gameEnded;
    }
    
    public boolean isAllAgentsMoved()
    {
        return agentsToMove.isEmpty();
    }

    public void addAgentToMove(AID agentAID)
    {
        if (!agentsToMove.contains(agentAID))
        {
            agentsToMove.add(agentAID);
        }
    }
    
    public void removeAgentToMove(AID agentAID)
    {
        if (agentsToMove.contains(agentAID))
        {
            agentsToMove.remove(agentAID);
        }
        /*else
        {
            System.out.println("NOT REMOVED " + agentAID);
        }*/
    }

    public List<AID> getAgentsToMove()
    {
        return agentsToMove;
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
