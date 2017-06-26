package pacman.view;

import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import pacman.model.agent.GameAgent;
import pacman.view.component.BoardGui;

public class GameGui extends JFrame
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
        
        myAgent.startGame();
    }

    @Override
    public void repaint()
    {
        boardGui.repaint();
        super.repaint();
    }

}
