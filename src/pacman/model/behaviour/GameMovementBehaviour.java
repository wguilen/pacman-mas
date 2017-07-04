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

    private static int ResendTimeout = Constant.GAME_RESEND_MOVE_ORDER_TIMEOUT;
    
    public GameMovementBehaviour(Agent agent)
    {
        super(agent, Constant.MOVEMENT_DELAY);
    }
    
    @Override
    protected void onTick()
    {
        if (Constant.DEBUG
                && !((GameAgent) myAgent).isTurnComplete())
        {
            System.out.print("Missing agents to move:");
            ((GameAgent) myAgent).getAgentsToMove().forEach(agent ->
            {
                System.out.print("  " + agent.getLocalName());
            });
            System.out.println("");
        }
        
        recycle();
        
        if (!(((GameAgent) myAgent).isGameRunning()
                && ((GameAgent) myAgent).isTurnComplete()))
        {
            return;
        }

        if (((GameAgent) myAgent).isGameEnded())
        {
            myAgent.removeBehaviour(this);
            return;
        }        
        
        sendMoveOrder();
    }
    
    
    // --- Private auxiliary methods
    
    private void recycle()
    {
        if (!((GameAgent) myAgent).isTurnComplete())
        {
            if (Constant.DEBUG)
            {
                System.out.println("ResendTimeout is " + ResendTimeout);
            }
            
            if (0 == --ResendTimeout)
            {
                System.out.println("Resending move order...");
                
                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                message.setOntology(GameVocabulary.ONTOLOGY);
                message.setContent(GameVocabulary.MOVE_YOUR_BODY);
        
                ((GameAgent) myAgent).getAgentsToMove().forEach(agent -> message.addReceiver(agent));
                myAgent.send(message);
                
                ResendTimeout = Constant.GAME_RESEND_MOVE_ORDER_TIMEOUT;
            }
        }
    }
    
    private void sendMoveOrder()
    {
        ((GameAgent) myAgent).setTurnComplete(false);
        
        // Clears possible remaining of agents to move
        // Just a prevention: it should never happen
        ((GameAgent) myAgent).getAgentsToMove().clear();
        
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setOntology(GameVocabulary.ONTOLOGY);
        message.setContent(GameVocabulary.MOVE_YOUR_BODY);
        
        // Add ghosts receivers...
        List<GhostAgent> ghosts = ((GameAgent) myAgent).getBoard().getGhosts()
                                    .stream()
                                    .filter(ghost -> ghost.isHouseLeft() && !ghost.isShouldDie())
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
        
        ResendTimeout = Constant.GAME_RESEND_MOVE_ORDER_TIMEOUT;
    }

}
