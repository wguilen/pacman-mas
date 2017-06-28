package pacman.model.behaviour;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;
import pacman.model.agent.GameAgent;
import pacman.model.core.GameVocabulary;

public class GameTogglePauseBehaviour extends OneShotBehaviour
{
    
    
    @Override
    public void action()
    {
        boolean pause = ((GameAgent) myAgent).isGameRunning();
        
        // Tells the agents the game has to pause/continue
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setOntology(GameVocabulary.ONTOLOGY);
        message.setContent(pause ? GameVocabulary.PAUSE : GameVocabulary.CONTINUE);

        // Informs the ghosts...
        ((GameAgent) myAgent).getGhosts().forEach((ghost) ->
        {
            try
            {
                message.addReceiver(new AID(ghost.getName(), AID.ISGUID));
            } 
            catch (StaleProxyException ex) {}
        });
        
        // Informs Pacman...
        // TODO
        
        // Sends the message
        myAgent.send(message);
        
        // Updates the GameAgent state
        ((GameAgent) myAgent).setGameRunning(!pause);
        myAgent.removeBehaviour(this);
    }
    
}
