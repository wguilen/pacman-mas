package pacman;

import jade.wrapper.StaleProxyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacman.core.ContainerManager;
import pacman.model.agent.GameAgent;
import pacman.model.core.Constant;

public class Starter
{

    public static void main(String[] args)
    {
        try
        {
            Object[] agentArgs = { "01.board" };
            ContainerManager.getInstance().instantiateAgent(Constant.GAME_AGENT_NAME, GameAgent.class.getName(), agentArgs);
        } 
        catch (StaleProxyException ex)
        {
            Logger.getLogger(Starter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}