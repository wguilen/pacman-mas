package pacman.view;

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
    }
    
    private void init()
    {
        setResizable(false);
        setSize(boardGui.getSize());
        setTitle("Multiagent Pacman");
        
        // Tries to set the app icon
        try
        {
            setIconImage(ImageIO.read(getClass().getResource("/pacman/resources/image/icon/app.png")));
        } 
        catch (IOException ex) {}
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                myAgent.doDelete();
            }
            
        });
        
        add(boardGui);
        setVisible(true);
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

}