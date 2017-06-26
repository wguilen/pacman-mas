package pacman.model.behaviour;

import jade.core.Agent;
import pacman.model.agent.GhostAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;

public class GhostMovementBehaviour extends MovementBehaviour
{

    public GhostMovementBehaviour(Agent agent, Board board, Cell myCell)
    {
        super(agent, board, myCell);
    }
    
    @Override
    public void onTick()
    {
        if (!((GhostAgent) myAgent).isHouseLeft())
        {
            System.out.println(myAgent.getLocalName() + " still didn't left his house...");
            return;
        }
        
        super.onTick();
    }

}
