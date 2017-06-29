package pacman.model.behaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;
import pacman.model.agent.GameAgent;
import pacman.model.core.Constant;
import pacman.model.core.GameVocabulary;

public class GameMovementBehaviour extends TickerBehaviour
{

    public GameMovementBehaviour(Agent agent)
    {
        super(agent, Constant.MOVEMENT_DELAY);
    }
    
    @Override
    protected void onTick()
    {
        if (!(((GameAgent) myAgent).isGameRunning()
                && ((GameAgent) myAgent).isTurnComplete()))
        {
            return;
        }
        
        ((GameAgent) myAgent).setTurnComplete(false);
        
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setOntology(GameVocabulary.ONTOLOGY);
        message.setContent(GameVocabulary.MOVE_YOUR_BODY);
        
        ((GameAgent) myAgent).getGhosts().forEach((ghost) ->
        {
            try
            {
                message.addReceiver(new AID(ghost.getName(), AID.ISGUID));
            } 
            catch (StaleProxyException ex) {}
        });
        
        myAgent.send(message);
    }

}
