package pacman.model.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.stream.Collectors;
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
                    handleAgentsMovement();
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
    
    private void handleAgentsMovement()
    {
        // Increments the counter of agents that has made their movement
        ((GameAgent) myAgent).incrementMovedCounter();

        // Counts the number of agents that has to move
        int ghosts = ((GameAgent) myAgent).getBoard().getGhosts()
                        .stream()
                        .filter(ghost -> ghost.isHouseLeft())
                        .collect(Collectors.toList()).size();
        
        // If the counter is equal to the quantity of agents,
        //      notifies the observers and resets the counter
        // IE, the movement is done on the board
        if (((GameAgent) myAgent).getMovedCounter() == (ghosts))    // TODO: Change to Ghosts + 1 (Pacman)
        {
            ((GameAgent) myAgent).getObservers().forEach(observer ->
            {
                observer.onTurn();
            });
            
            System.out.println("Turn is complete...");
            
            ((GameAgent) myAgent).resetMovedCounter();
            ((GameAgent) myAgent).setTurnComplete(true);
        }
    }

}
