package pacman.view;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
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

    
    // --- Component
    
    @Override
    public void repaint()
    {
        boardGui.repaint();
        super.repaint();
    }

    
    // --- GameListener
    
    @Override
    public void onLoaded()
    {
        myAgent.startGame();
    }

    @Override
    public void onTurnComplete()
    {
        repaint();
    }

    @Override
    public void onAgentInitialized()
    {
        repaint();
    }

}