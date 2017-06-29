package pacman.model.board;

import pacman.model.agent.PacmanAgent;

public class PacmanCell extends Cell
{

    private final PacmanAgent agent;

    // --- Ctors
    
    private PacmanCell(PacmanAgent agent, Coord2D position, CellType type)
    {
        super(position, type);
        this.agent = agent;
    }
    
    public PacmanCell(PacmanAgent agent, Cell house)
    {
        this(agent, house.getPosition(), CellType.PACMAN);
    }

    
    // --- Getters and setters
    
    public PacmanAgent getAgent()
    {
        return agent;
    }
    
}
