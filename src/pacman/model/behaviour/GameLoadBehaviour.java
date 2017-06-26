package pacman.model.behaviour;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.StaleProxyException;
import java.io.FileNotFoundException;
import pacman.core.ContainerManager;
import pacman.model.agent.GameAgent;
import pacman.model.agent.GhostAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.core.Constant;

public class GameLoadBehaviour extends OneShotBehaviour
{

    private Board board;
    private final ContainerManager containerManager;
    
    
    // --- Ctors
    
    public GameLoadBehaviour(Agent agent, String filename) throws FileNotFoundException
    {
        super(agent);
        
        // Loads the board
        ((GameAgent) agent).setBoard(loadBoard(filename));
        
        if (Constant.DEBUG)
        {
            System.out.println("Board model initialized...\nInitial board:");
            board.print();
        }
        
        // Fetches the container manager instance
        containerManager = ContainerManager.getInstance();
    }
    
    
    // --- Private methods
    
    private Board loadBoard(String filename) throws FileNotFoundException
    {
        board = new Board(getClass().getResource("/pacman/resources/board/" + filename).getPath());  
        return board;
    }

    private void loadGhosts() throws StaleProxyException
    {
        int ghost = 1;
        for (Cell freeGhostsHouse : board.getFreeGhostsHouses())
        {
            Object[] args = { board, freeGhostsHouse };
            containerManager.instantiateAgent("Ghost-" + ghost++, GhostAgent.class.getName(), args);
        }
    }   
    
    private void loadPacman()
    {
        
    }
    
    
    // --- Overriden methods
    
    @Override
    public void action()
    {
        System.out.println("On GameLoadBehaviour action()...");
        
        try
        {
            loadGhosts();
            System.out.println("Ghosts agents initialized...");
            
            loadPacman();
            System.out.println("Pacman agent initialized...");
            
            if (Constant.DEBUG)
            {
                System.out.println("Fully loaded initial board:");
                board.print();
            }
        } 
        catch (StaleProxyException ex)
        {
            System.out.println("Couldn't initialize agents...");
        }
    }

}
