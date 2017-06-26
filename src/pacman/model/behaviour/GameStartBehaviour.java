package pacman.model.behaviour;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
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
        board.getGhosts().forEach((ghost) ->
        {
            message.addReceiver(ghost.getAID());
        });
        
        // Informs Pacman...
        // TODO: Implement
        
        myAgent.send(message);
    }

}
