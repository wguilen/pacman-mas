package pacman.view;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import pacman.model.agent.GameAgent;
import pacman.model.observer.GameListener;
import pacman.view.component.BoardGui;

public class GameGui extends JFrame implements GameListener
{
    
    private final GameAgent myAgent;
    private final BoardGui boardGui;
    
    public GameGui(GameAgent agent)
    {
        this.myAgent = agent;
        this.boardGui = new BoardGui(agent.getBoard());
        
        init();
        initListeners();
    }
    
    
    // --- Private auxiliary methods
    
    private void init()
    {
        setResizable(false);
        setSize(boardGui.getSize());
        setTitle("Multiagent Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Tries to set the app icon
        try
        {
            setIconImage(ImageIO.read(getClass().getResource("/pacman/resources/image/icon/app.png")));
        } 
        catch (IOException ex) {}
        
        add(boardGui);
        setVisible(true);
    }
    
    private void initListeners()
    {
        addWindowListener(new WindowAdapter()
        {
            
            @Override
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                myAgent.doDelete();
            }
            
        });
        
        addKeyListener(new KeyAdapter()
        {
            
            @Override
            public void keyReleased(KeyEvent e)
            {
                if (KeyEvent.VK_SPACE == e.getKeyCode())
                {
                    myAgent.togglePause();
                }
            }
            
        });
    }
    
    private void handleQuit(String message, String title)
    {
        int ret = JOptionPane.showConfirmDialog(
                        this, 
                        message + "\nQuit now?", 
                        title, 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.INFORMATION_MESSAGE
        );
        
        if (JOptionPane.YES_OPTION == ret)
        {
            myAgent.doDelete();
        }
    }

    
    // --- Component
    
    @Override
    public void repaint()
    {
        boardGui.repaint();
        super.repaint();
    }

    
    // --- GameListener
    
    
    @Override
    public void dispose()
    {
        System.out.println("Finishing GameGUI...");
        System.exit(0);
    }

    @Override
    public void onAgentInitialized()
    {
        repaint();
    }
    
    @Override
    public void onGameWonByPacman()
    {
        handleQuit("Pacman won the game!", "End game");
    }
    
    @Override
    public void onLoaded()
    {
        myAgent.startGame();
    }

    @Override
    public void onPacmanKilled()
    {
        handleQuit("Pacman was killed!", "End game");
    }

    @Override
    public void onTurnComplete()
    {
        repaint();
    }

}