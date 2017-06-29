package pacman.model.behaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;
import java.util.List;
import java.util.stream.Collectors;
import pacman.model.agent.GameAgent;
import pacman.model.agent.GhostAgent;
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
        
        // Add ghosts receivers...
        List<GhostAgent> ghosts = ((GameAgent) myAgent).getBoard().getGhosts()
                                    .stream()
                                    .filter(ghost -> ghost.isHouseLeft())
                                    .collect(Collectors.toList());

        ghosts.forEach(ghost -> 
        {
            message.addReceiver(ghost.getAID());
            ((GameAgent) myAgent).addAgentToMove(ghost.getAID());
        });
        
        // Add Pacman receiver...
        AID pacmanAID = null;
        
        try
        {
            pacmanAID = new AID(((GameAgent) myAgent).getPacman().getName(), AID.ISGUID);
        } 
        catch (StaleProxyException ex) {}

        // Tries to send the message
        if (null != pacmanAID)
        {
            message.addReceiver(pacmanAID);
            ((GameAgent) myAgent).addAgentToMove(pacmanAID);
            myAgent.send(message);
        }
        else if (ghosts.isEmpty())
        {
            ((GameAgent) myAgent).setTurnComplete(true);
        }
    }

}
