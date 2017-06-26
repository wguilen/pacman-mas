/*
 * https://stackoverflow.com/questions/17646076/how-to-start-jade-gui-within-another-gui
 * https://stackoverflow.com/questions/22391640/how-to-create-containers-and-add-agents-into-it-in-jade
 */
package pacman.core;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class ContainerManager 
{
    
    private static ContainerManager instance = null;
    private ContainerController containerController;
    
    private ContainerManager()
    {
        init();
    }
    
    public static ContainerManager getInstance()
    {
        if (null == instance)
        {
            instance = new ContainerManager();
        }
        
        return instance;
    }
    
    private void init()
    {
        // Gets JADE runtime interface
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        runtime.setCloseVM(true);
        
        // Creates a profile
        Profile profile = new ProfileImpl(true);
        profile.setParameter(Profile.CONTAINER_NAME, "Pacman");
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true");
        
        // Creates a non-main container
        // In this case, JADE's main container must be running
        //containerController = runtime.createAgentContainer(profile);
        
        // Creates a main container
        containerController = runtime.createMainContainer(profile);
    }
    
    public AgentController instantiateAgent(String name, String className, Object[] args) throws StaleProxyException
    {
        AgentController agentController = containerController.createNewAgent(name, className, args);
        agentController.start();
        return agentController;
    }
    
    public AgentController instantiateAgent(String name, String className) throws StaleProxyException
    {
        return instantiateAgent(name, className, new Object[0]);
    }
    
}