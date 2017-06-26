package pacman.model.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pacman.model.core.GhostVocabulary;

public class GhostLifecycleBehaviour extends CyclicBehaviour
{

    @Override
    public void action()
    {
        MessageTemplate mt = MessageTemplate.MatchOntology(GhostVocabulary.ONTOLOGY);
        ACLMessage message = myAgent.receive(mt);
        if (null != message)
        {
            switch (message.getContent())
            {
                case GhostVocabulary.GET_OUT_OF_MY_WAY:
                    System.out.println(message.getSender() + " asked me to get out of his way!");
                    break;
                    
                default:
                    block();
            }
        }
        else
        {
            block();
        }
    }

}
