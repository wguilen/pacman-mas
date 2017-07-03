package pacman.model.board;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import pacman.model.agent.GhostAgent;
import pacman.model.agent.PacmanAgent;
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
    
    public synchronized List<GhostAgent> getGhosts()
    {
        List<GhostAgent> ghosts = new ArrayList<>();
        
        board.forEach((row) ->
        {
            row
                .stream()
                .filter(cell -> cell instanceof GhostCell)
                .forEach(cell ->
            {
                GhostAgent ghost = ((GhostCell) cell).getAgent();
                if (!ghosts.contains(ghost))
                {
                    ghosts.add(ghost);
                }
            });
        });
        
        return ghosts;
    }
    
    public synchronized Cell getGhostDoor()
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
    
    
    
    // --- Pacman public methods
    
    public synchronized Cell getPacmanHouse()
    {
        List<Cell> pacmanHouse = new ArrayList<>();
        board.forEach(row ->
        {
            if (!pacmanHouse.isEmpty())
            {
                return;
            }
            
            Optional<Cell> opt = row
                                    .stream()
                                    .filter(cell -> CellType.PACMAN_HOUSE == cell.getType())
                                    .findAny();
            
            if (opt.isPresent())
            {
                pacmanHouse.add(opt.get());
            }
        });
        
        return pacmanHouse.get(0);
    }
    
    public synchronized PacmanAgent getPacman()
    {
        List<PacmanAgent> pacman = new ArrayList<>();
        board.forEach(row ->
        {
            if (!pacman.isEmpty())
            {
                return;
            }
            
            Optional<Cell> opt = row
                                    .stream()
                                    .filter(cell -> cell instanceof PacmanCell)
                                    .findAny();
            
            if (opt.isPresent())
            {
                pacman.add(((PacmanCell) opt.get()).getAgent());
            }
        });
        
        return pacman.get(0);
    }
    
    public synchronized void removeAgentCell(Cell agentCell)
    {
        Cell previousCell = getCell(previousBoard, agentCell.getPosition());
        setCell(previousCell);
    }
    
    
    // --- Board general public methods
    
    public synchronized Cell getCell(Coord2D position)
    {
        return getCell(board, position);
    }
    
    public synchronized void setCell(Cell cell)
    {
        setCell(board, cell);
    }
    
    public synchronized void moveCell(Cell cell, Coord2D destination)
    {
        moveCell(cell, destination, false);
    }
    
    public synchronized void moveCell(Cell cell, Coord2D destination, boolean modifyBoard)
    {
        // Gets the cell that was on the current position
        Cell previousCell = getCell(previousBoard, cell.getPosition());
        setCell(previousCell);
        
        // If board should be modified...
        if (modifyBoard)
        {
            // ... and the previous was...
            switch (previousCell.getType())
            {
                // ... a simple or a powerup collectible, removes it
                case DOT:
                case POWERUP:
                    Cell emptyCell = new Cell(previousCell.getPosition(), CellType.EMPTY);
                    setCell(emptyCell);
                    setCell(previousBoard, emptyCell);
                    break;
            }
        }
        
        // Updates the new cell with its destination
        cell.setPosition(destination);
        setCell(cell);
    }
    
    public synchronized int countRows()
    {
        return board.size();
    }
    
    public synchronized int countColumns()
    {
        return board.get(0).size();
    }
    
    public boolean hasRemainingCollectibles()
    {
        List<Cell> collectibles = new ArrayList<>();
        
        board.forEach(row ->
        {
            if (!collectibles.isEmpty())
            {
                return;
            }
            
            Optional<Cell> optCell = row
                                        .stream()
                                        .filter(cell -> (cell.getType().equals(CellType.DOT) || cell.getType().equals(CellType.POWERUP)))
                                        .findAny();
            
            if (optCell.isPresent())
            {
                collectibles.add(optCell.get());
            }
        });
        
        return !collectibles.isEmpty();
    }
    
    public synchronized void print()
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
    
    private synchronized Cell getCell(List<List<Cell>> board, Coord2D position)
    {
        return board.get(position.x).get(position.y);
    }
    
    private synchronized void setCell(List<List<Cell>> board, Cell cell)
    {
        board.get(cell.getPosition().x).set(cell.getPosition().y, cell);
    }

}