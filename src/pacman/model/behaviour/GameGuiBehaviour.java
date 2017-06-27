package pacman.model.behaviour;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import pacman.model.agent.GameAgent;
import pacman.model.core.Constant;

public class GameGuiBehaviour extends TickerBehaviour
{

    public GameGuiBehaviour(Agent agent)
    {
        super(agent, Constant.MOVEMENT_DELAY);
    }

    @Override
    protected void onTick()
    {
        ((GameAgent) myAgent).getGui().repaint();
    }

}
