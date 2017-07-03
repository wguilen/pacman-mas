package pacman.model.behaviour;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import pacman.model.agent.PacmanAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.core.GameVocabulary;

public class PacmanLifecycleBehaviour extends CyclicBehaviour
{

    private final Board board;
    private final Cell myCell;
    
    public PacmanLifecycleBehaviour(Agent agent, Board board, Cell cell)
    {
        super(agent);
        this.board = board;
        this.myCell = cell;
    }

    @Override
    public void action()
    {
        ACLMessage message = myAgent.receive();
        if (null != message)
        {
            System.out.println(myAgent.getLocalName() + " received " + message.getContent() + " from " + message.getSender().getLocalName());
            
            switch (message.getOntology())
            {
                case GameVocabulary.ONTOLOGY:
                    switch (message.getContent())
                    {
                        case GameVocabulary.START:
                        case GameVocabulary.CONTINUE:
                            ((PacmanAgent) myAgent).setGameRunning(true);
                            break;
                            
                        case GameVocabulary.PAUSE:
                            ((PacmanAgent) myAgent).setGameRunning(false);
                            break;
                        
                        case GameVocabulary.MOVE_YOUR_BODY:
                            if (!((PacmanAgent) myAgent).isMoving())
                            {
                                ((PacmanAgent) myAgent).setMoving(true);
                                myAgent.addBehaviour(new PacmanMovementBehaviour(message, board, myCell));
                            }
                            
                            break;
                            
                        default:
                            block();
                    }
                    
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

}
