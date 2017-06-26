package pacman.model.board;

import java.awt.Graphics2D;
import pacman.model.core.Constant;

public class Cell
{

    private CellType type;
    private Coord2D position;

    public Cell(Coord2D position, CellType type)
    {
        this.position = position;
        this.type = type;
    }

    public Coord2D getPosition()
    {
        return position;
    }

    public void setPosition(Coord2D position)
    {
        this.position = position;
    }
    
    public CellType getType()
    {
        return type;
    }

    public void setType(CellType type)
    {
        this.type = type;
    }
    
    public void draw(Graphics2D canvas)
    {
        int x = position.x,
            y = position.y;
                
        canvas.setColor(type.getColor());
        // TODO: Change some stuff to canvas.drawImage(...)
        
        switch (type)
        {
            case DOT:
                int xPos = Constant.BOARD_CELL_SIZE * x + (Constant.BOARD_CELL_SIZE / 4);
                int yPos = Constant.BOARD_CELL_SIZE * y + (Constant.BOARD_CELL_SIZE / 4);
                
                canvas.fillOval(
                    yPos,
                    xPos,
                    Constant.BOARD_CELL_SIZE / 2, 
                    Constant.BOARD_CELL_SIZE / 2   
                );
                
                break;
                
            case GHOST:
            case PACMAN:
            case POWERUP:
                canvas.fillOval(
                    Constant.BOARD_CELL_SIZE * y,
                    Constant.BOARD_CELL_SIZE * x, 
                    Constant.BOARD_CELL_SIZE, 
                    Constant.BOARD_CELL_SIZE    
                );
                
                break;
            default:
                canvas.fillRect(
                    Constant.BOARD_CELL_SIZE * y,
                    Constant.BOARD_CELL_SIZE * x, 
                    Constant.BOARD_CELL_SIZE, 
                    Constant.BOARD_CELL_SIZE
                );
        }
    }

    @Override
    public String toString()
    {
        return type.toString();
    }
    
}
