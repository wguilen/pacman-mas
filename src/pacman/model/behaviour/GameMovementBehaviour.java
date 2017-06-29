package pacman.model.behaviour;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
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
        
        List<GhostAgent> ghosts = ((GameAgent) myAgent).getBoard().getGhosts()
                                    .stream()
                                    .filter(ghost -> ghost.isHouseLeft())
                                    .collect(Collectors.toList());

        if (!ghosts.isEmpty())
        {
            ghosts.forEach(ghost -> 
            {
                message.addReceiver(ghost.getAID());
                ((GameAgent) myAgent).addAgentToMove(ghost.getAID());
            });

            myAgent.send(message);
        }
        else
        {
            ((GameAgent) myAgent).setTurnComplete(true);
        }
    }

}
