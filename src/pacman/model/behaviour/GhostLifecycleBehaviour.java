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
                        case GameVocabulary.CONTINUE:
                            ((GhostAgent) myAgent).setGameRunning(true);
                            break;
                            
                        case GameVocabulary.MOVE_YOUR_BODY:
                            if (!((GhostAgent) myAgent).isMoving())
                            {
                                ((GhostAgent) myAgent).setMoving(true);
                                myAgent.addBehaviour(new GhostMovementBehaviour(message, board, myCell));
                            }
                            else
                            {
                                System.out.println(myAgent.getLocalName() + " is already moving...");
                            }
                            
                            break;
                            
                        case GameVocabulary.PAUSE:
                            ((GhostAgent) myAgent).setGameRunning(false);
                            break;
                            
                        case GameVocabulary.START:
                            ((GhostAgent) myAgent).setGameRunning(true);
                            myAgent.addBehaviour(new GhostLeaveHouseFirstBehaviour(myAgent, board, myCell));
                            break;
                            
                        default:
                            block();
                    }
                    
                    break;
                    
                case GhostVocabulary.ONTOLOGY:
                    switch (message.getContent())
                    {
                        case GhostVocabulary.LEAVE_THE_HOUSE:
                            myAgent.addBehaviour(new GhostLeaveHouseNextBehaviour(myAgent, board, myCell));
                            break;
                            
                        case GhostVocabulary.GET_OUT_OF_MY_WAY:
                            ((GhostAgent) myAgent).setReverseDirection(true);
                            break;
                            
                        case GhostVocabulary.THE_MOTHERFUCKER_IS_DEAD:
                            ((GhostAgent) myAgent).setGameRunning(false);
                            System.out.println("YAAAAAY! " + myAgent.getLocalName() + " is happy because "
                                    + "Pacman is finally dead!");
                            break;
                            
                        case GhostVocabulary.THE_MOTHERFUCKER_KILLED_ME:
                            System.out.println("Oh, no... " + message.getSender().getLocalName() + " was killed by the motherfucker... :(~");
                            // TODO: Run away from Pacman (if a ghost was killed, it means Pacman is powerful)
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
