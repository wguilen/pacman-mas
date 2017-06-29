package pacman.model.behaviour;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pacman.model.agent.GameAgent;
import pacman.model.core.GameVocabulary;

public class GameLifecycleBehaviour extends CyclicBehaviour
{

    @Override
    public void action()
    {
        MessageTemplate mt = MessageTemplate.MatchOntology(GameVocabulary.ONTOLOGY);
        ACLMessage message = myAgent.receive(mt);
        if (null != message)
        {
            System.out.println(myAgent.getLocalName() + " received " + message.getContent() + " from " + message.getSender().getLocalName());
            
            switch (message.getContent())
            {
                case GameVocabulary.MOVED_MY_BODY:
                    handleAgentsMovement(message.getSender());
                    break;
                    
                default:
                    block();
            }
        }
        else
        {
            block();
        }
    }
    
    private void handleAgentsMovement(AID agentAID)
    {
        // Increments the counter of agents that has made their movement
        ((GameAgent) myAgent).addMovedAgent(agentAID);

        // Counts the number of agents that has to move
        int ghosts = (int) ((GameAgent) myAgent).getBoard().getGhosts()
                                .stream()
                                .filter(ghost -> ghost.isHouseLeft())
                                .count();
        
        // If the counter is equal to the quantity of agents,
        //      notifies the observers and resets the counter
        // IE, the movement is done on the board
        if (((GameAgent) myAgent).getMovedCounter() == (ghosts))    // TODO: Change to Ghosts + 1 (Pacman)
        {
            ((GameAgent) myAgent).getObservers().forEach(observer ->
            {
                observer.onTurnComplete();
            });
            
            System.out.println("Turn is complete...");
            
            ((GameAgent) myAgent).resetMoved();
            ((GameAgent) myAgent).setTurnComplete(true);
        }
    }

}
