package pacman.model.behaviour;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pacman.model.agent.GameAgent;
import pacman.model.core.GameVocabulary;

public class GameLifecycleBehaviour extends CyclicBehaviour
{

    private boolean pacmanWins;

    public GameLifecycleBehaviour()
    {
        pacmanWins = false;
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
                case GameVocabulary.AGENT_INITIALIZED:
                    handleAgentInitialized();
                    break;
                
                case GameVocabulary.END_PACMAN_WINS:
                    pacmanWins = true;
                    ((GameAgent) myAgent).setGameEnded(true);
                    break;
                    
                case GameVocabulary.GHOST_KILLED:
                    handleAgentKilled(message.getSender());
                    break;
                    
                case GameVocabulary.MOVED_MY_BODY:
                    handleAgentMovement(message.getSender());
                    break;
                    
                case GameVocabulary.PACMAN_KILLED:
                    pacmanWins = false;
                    ((GameAgent) myAgent).setGameEnded(true);
                    handleAgentKilled(message.getSender());
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
    
    private void handleAgentKilled(AID killedAgentAID)
    {
        handleAgentMovement(killedAgentAID);
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
                if (pacmanWins)
                {
                    ((GameAgent) myAgent).getObservers()
                            .forEach(observer -> observer.onGameWonByPacman());
                }
                // Ghosts won the game
                else
                {
                    ((GameAgent) myAgent).getObservers()
                            .forEach(observer -> observer.onPacmanKilled());
                }
            }
        }
        
    }

}
