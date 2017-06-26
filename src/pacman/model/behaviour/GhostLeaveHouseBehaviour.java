package pacman.model.behaviour;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;
import pacman.model.agent.GhostAgent;
import pacman.model.board.Board;
import pacman.model.board.Cell;
import pacman.model.board.CellType;
import pacman.model.board.Coord2D;
import pacman.model.board.Direction;
import pacman.model.core.GhostVocabulary;

public abstract class GhostLeaveHouseBehaviour extends OneShotBehaviour
{

    protected  final Board board;
    protected  final Cell myCell;

    public GhostLeaveHouseBehaviour(Board board, Cell cell)
    {
        //super(agent);
        this.board = board;
        this.myCell = cell;
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    protected void adviceNextGhost()
    {
        // Selects the next ghost (if any) to leave the house and sends it a message
        List<GhostAgent> nextGhosts = new ArrayList<>(); // Just for a hack
        board.getGhosts().stream().filter((ghost)
                -> (!ghost.equals(myAgent)) && !ghost.isHouseLeft() && nextGhosts.isEmpty()).forEachOrdered((ghost) ->
        {
            Coord2D position = ghost.getBoardCell().getPosition();
            for (Direction direction : Direction.values())
            {
                Cell nearCell = board.getCell(new Coord2D(position.x + direction.xInc, position.y + direction.yInc));
                if (nearCell.getType() == CellType.GHOST_HOUSE)
                {
                    // Mounts the message and sends it to the next ghost to leave the house
                    ACLMessage nextGhostMessage = new ACLMessage(ACLMessage.INFORM);
                    nextGhostMessage.setOntology(GhostVocabulary.ONTOLOGY);
                    nextGhostMessage.setContent(GhostVocabulary.LEAVE_THE_HOUSE);
                    nextGhostMessage.addReceiver(ghost.getAID());
                    myAgent.send(nextGhostMessage);

                    nextGhosts.add(ghost); // It's added an element to nextGhosts just to break the forEachOrdered()
                }
            }
        });
    }

}
