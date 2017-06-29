package pacman.model.observer;

public interface GameListener
{
    
    void onAgentInitialized();
    void onLoaded();
    void onTurnComplete();
    
}
