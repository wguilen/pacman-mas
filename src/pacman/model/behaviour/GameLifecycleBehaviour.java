package pacman.model.behaviour;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pacman.model.agent.GameAgent;
import pacman.model.core.GameVocabulary;

public class GameLifecycleBehaviour extends CyclicBehaviour
{

    // Game control properties
    private String pacmanKiller;

    
    // --- Ctors
    
    public GameLifecycleBehaviour()
    {
        pacmanKiller = null;
    }
    
    
    // --- Public overriden methods
    
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
                case GameVocabulary.DEATH_CONFIRM:
                    handleDeathConfirmation(message.getSender());
                    break;
                    
                case GameVocabulary.AGENT_INITIALIZED:
                    handleAgentInitialized();
                    break;
                
                case GameVocabulary.END_GHOSTS_WIN:
                    ((GameAgent) myAgent).setGameEnded(true);
                    pacmanKiller = message.getSender().getLocalName();
                    break;
                    
                case GameVocabulary.END_PACMAN_WINS:
                    ((GameAgent) myAgent).setGameEnded(true);
                    break;
                    
                case GameVocabulary.MOVED_MY_BODY:
                    handleAgentMovement(message.getSender());
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
    
    
    // --- Private auxiliary methods
    
    // This method is called only when a ghost dies
    private void handleDeathConfirmation(AID agentAID)
    {
        // The agent won't move anymore (it's dead!)
        ((GameAgent) myAgent).removeAgentToMove(agentAID);
    }
    
    private void handleAgentInitialized()
    {
        ((GameAgent) myAgent).decrementWaitingInitialization();
        
        // After all agents are initialized...
        if (0 == ((GameAgent) myAgent).getWaitingInitialization())
        {
            System.out.println("Initing game...");
            
            // ... notifies the game loaded observers
            ((GameAgent) myAgent).getObservers().forEach(observer -> observer.onLoaded());
        }
        // Else...
        else
        {
            // ... notifies the agents initialized observers
            ((GameAgent) myAgent).getObservers().forEach(observer -> observer.onAgentInitialized());
        }
    }
    
    private void handleAgentMovement(AID agentAID)
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
            
            if (((GameAgent) myAgent).isGameEnded())
            {
                ((GameAgent) myAgent).setGameRunning(false);
                
                // Pacman won the game
                if (null == pacmanKiller)
                {
                    ((GameAgent) myAgent).getObservers()
                            .forEach(observer -> observer.onGameWonByPacman());
                }
                // Ghosts won the game
                else
                {
                    ((GameAgent) myAgent).getObservers()
                            .forEach(observer -> observer.onPacmanKilled(pacmanKiller));
                }
            }
        }
        
    }

}
