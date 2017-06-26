package pacman.view.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import pacman.model.board.Board;
import pacman.model.board.Coord2D;
import pacman.model.core.Constant;

public class BoardGui extends JPanel
{
    
    private final Board board;
    
    public BoardGui(Board board)
    {
        this.board = board;
        init();
    }
    
    private void init()
    {
        Dimension size = new Dimension(
            Constant.BOARD_CELL_SIZE * board.countColumns(),
            Constant.BOARD_CELL_SIZE + Constant.BOARD_CELL_SIZE * board.countRows()
        );
        
        setPreferredSize(size);
        setSize(size);
        setVisible(true);
    }
    
    @Override
    public void paint(Graphics g)
    {
        // Gets the canvas to draw in
        BufferedImage bi = new BufferedImage(
            Constant.BOARD_CELL_SIZE * board.countColumns(),
            Constant.BOARD_CELL_SIZE * board.countRows(),
            BufferedImage.TYPE_INT_RGB
        );
        
        Graphics2D canvas = bi.createGraphics();

        // Draws the game in the canvas
        for (int i = 0; i < board.countRows(); ++i)
        {
            for (int j = 0; j < board.countColumns(); ++j)
            {
                board.getCell(new Coord2D(i, j)).draw(canvas); // TODO: Change draw to Coord2D (or use the cell position? think it's better!!!)
            }
        }
        
        // Really paints the component
        ((Graphics2D) g).drawImage(bi, 0, 0, this);
    }

}