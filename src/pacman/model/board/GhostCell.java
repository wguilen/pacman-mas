package pacman.model.board;

import pacman.model.agent.GhostAgent;

public class GhostCell extends Cell
{

    private final GhostAgent agent;

    // --- Ctors
    
    private GhostCell(GhostAgent agent, Coord2D position, CellType type)
    {
        super(position, type);
        this.agent = agent;
    }
    
    public GhostCell(GhostAgent agent, Cell house)
    {
        this(agent, house.getPosition(), CellType.GHOST);
    }

    
    // --- Getters and setters
    
    public GhostAgent getAgent()
    {
        return agent;
    }
    
}
