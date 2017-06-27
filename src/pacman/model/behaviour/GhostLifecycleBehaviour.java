package pacman.model.behaviour;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import pacman.model.agent.GhostAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.core.GameVocabulary;
import pacman.model.core.GhostVocabulary;

public class GhostLifecycleBehaviour extends CyclicBehaviour
{

    private final Board board;
    private final Cell myCell;
    
    public GhostLifecycleBehaviour(Agent agent, Board board, Cell cell)
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
                            myAgent.addBehaviour(new GhostLeaveHouseFirstBehaviour(board, myCell));
                            break;
                            
                        default:
                            block();
                    }
                    
                    break;
                    
                case GhostVocabulary.ONTOLOGY:
                    switch (message.getContent())
                    {
                        case GhostVocabulary.LEAVE_THE_HOUSE:
                            myAgent.addBehaviour(new GhostLeaveHouseNextBehaviour(board, myCell));
                            break;
                            
                        case GhostVocabulary.GET_OUT_OF_MY_WAY:
                            ((GhostAgent) myAgent).setReverseDirection(true);
                            break;
                            
                        default:
                            block();
                    }
                    
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
