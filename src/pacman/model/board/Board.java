package pacman.model.board;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import pacman.model.agent.GhostAgent;
import pacman.model.core.Constant;

public class Board
{

    private List<List<Cell>> board;
    private List<List<Cell>> previousBoard;

    
    // --- Ctors
    
    public Board(String filename) throws FileNotFoundException
    {
        loadBoardFromFile(filename);
    }
    
    
    // --- Ghosts public methods
    
    public List<Cell> getFreeGhostsHouses()
    {
        List<Cell> ghostsHouses = new ArrayList<>();
        
        board.forEach((row) ->
        {
            row.stream().filter((cell) -> 
                (CellType.GHOST_HOUSE == cell.getType())).forEachOrdered((cell) ->
            {
                ghostsHouses.add(cell);
            });
        });
        
        return ghostsHouses;
    }
    
    public List<GhostAgent> getGhosts()
    {
        List<GhostAgent> ghosts = new ArrayList<>();
        
        board.forEach((row) ->
        {
            row.stream().filter((cell) ->
                (cell instanceof GhostCell)).forEachOrdered((cell) ->
            {
                ghosts.add(((GhostCell) cell).getAgent());
            });
        });
        
        return ghosts;
    }
    
    public Cell getGhostDoor()
    {
        Cell door = null;
        for (List<Cell> row : previousBoard)
        {
            for (Cell cell : row)
            {
                if (CellType.DOOR == cell.getType())
                {
                    door = cell;
                    break;
                }
            }
            
            if (null != door)
            {
                break;
            }
        }
        
        return door;
    }
    
    
    // --- Board general public methods
    
    public Cell getCell(Coord2D position)
    {
        return getCell(board, position);
    }
    
    public void setCell(Cell cell)
    {
        setCell(board, cell);
    }
    
    public void moveCell(Cell cell, Coord2D destination)
    {
        // Updates the current cell position to its previous state
        Cell previousCell = getCell(previousBoard, cell.getPosition());
        setCell(previousCell);
        
        // Snashops the actual board
        //setCell(previousBoard, cell); TODO: Review this because of the ghost houses on the start of the game
        
        // Updates the new cell with its destination
        cell.setPosition(destination);
        setCell(cell);
    }
    
    public int countRows()
    {
        return board.size();
    }
    
    public int countColumns()
    {
        return board.get(0).size();
    }
    
    public void print()
    {
        if (!Constant.DEBUG) return;
        
        board.forEach((row) ->
        {
            row.forEach((cell) ->
            {
                System.out.print(cell + " ");
            });
            
            System.out.println("");
        });
    }
    
    
    // --- Private methods
    
    private void loadBoardFromFile(String file) throws FileNotFoundException
    {
        try (Scanner scanner = new Scanner(new File(file)))
        {
            int rows = scanner.nextInt();
            int columns = scanner.nextInt();
            
            board = new ArrayList<>();
            previousBoard = new ArrayList<>();
            
            for (int i = 0; i < rows; ++i)
            {
                board.add(i, new ArrayList<>());
                previousBoard.add(i, new ArrayList<>());
                String buff = scanner.next();
                
                for (int j = 0; j < columns; ++j)
                {
                    board.get(i).add(j, new Cell(new Coord2D(i, j), CellType.EMPTY));
                    previousBoard.get(i).add(j, new Cell(new Coord2D(i, j), CellType.EMPTY));
                    
                    for (CellType cellType : CellType.values())
                    {
                        if (cellType.getValue() == buff.charAt(j))
                        {
                            board.get(i).get(j).setType(cellType);
                            previousBoard.get(i).get(j).setType(cellType);
                        }
                    }
                }
            }
        }
    }
    
    private Cell getCell(List<List<Cell>> board, Coord2D position)
    {
        return board.get(position.x).get(position.y);
    }
    
    private void setCell(List<List<Cell>> board, Cell cell)
    {
        board.get(cell.getPosition().x).set(cell.getPosition().y, cell);
    }

}