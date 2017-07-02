package pacman.model.board;

import java.awt.Graphics2D;
import pacman.model.agent.PacmanAgent;
import pacman.model.core.Constant;

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

    
    // --- Public overriden methods
    
    @Override
    public void draw(Graphics2D canvas)
    {
        int x = getPosition().x,
            y = getPosition().y;
                
        canvas.setColor(!agent.isPowerfull() ? 
                getType().getColor() : CellType.POWERUP.getColor());
        
        canvas.fillOval(
            Constant.BOARD_CELL_SIZE * y,
            Constant.BOARD_CELL_SIZE * x, 
            Constant.BOARD_CELL_SIZE, 
            Constant.BOARD_CELL_SIZE    
        );
    }
    
}
