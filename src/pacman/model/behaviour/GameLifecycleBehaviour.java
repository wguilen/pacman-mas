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
        // The agent has done its movement
        ((GameAgent) myAgent).removeAgentToMove(agentAID);

        // If all agents has done their movements
        if (((GameAgent) myAgent).isAllAgentsMoved())
        {
            ((GameAgent) myAgent).getObservers().forEach(observer ->
            {
                observer.onTurnComplete();
            });
            
            System.out.println("Turn is complete...");
            ((GameAgent) myAgent).setTurnComplete(true);
        }
    }

}
