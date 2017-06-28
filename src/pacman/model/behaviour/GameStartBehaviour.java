package pacman.model.behaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;
import pacman.model.agent.GameAgent;
import pacman.model.board.Board;
import pacman.model.core.GameVocabulary;

public class GameStartBehaviour extends OneShotBehaviour
{

    private final Board board;

    public GameStartBehaviour(Agent agent, Board board)
    {
        super(agent);
        this.board = board;
    }
    
    @Override
    public void action()
    {
        System.out.println("On GameStartBehaviour action()...");
        
        // Tells the agents the game is about to start
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setOntology(GameVocabulary.ONTOLOGY);
        message.setContent(GameVocabulary.START);

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
        // TODO: Implement
        
        myAgent.send(message);
    }

}
