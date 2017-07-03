package pacman.model.observer;

public interface GameListener
{
    
    void dispose();
    void onAgentInitialized();
    void onGameWonByPacman();
    void onLoaded();
    void onPacmanKilled();
    void onTurnComplete();
    
}
