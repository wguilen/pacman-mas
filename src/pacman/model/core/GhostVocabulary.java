package pacman.model.core;

public class GhostVocabulary
{
    
    public static final String ONTOLOGY = "GHOST_ONTOLOGY";
    
    
    // --- Vocabulary
    
    public static final String GET_OUT_OF_MY_WAY            = "GET_OUT_OF_MY_WAY";          // Fired when a ghost is in front of another and maybe will collide
    public static final String LEAVE_THE_HOUSE              = "LEAVE_THE_HOUSE";            
    public static final String THE_MOTHERFUCKER_IS_DEAD     = "THE_MOTHERFUCKER_IS_DEAD";   // Fired when a ghost kills Pacman
    public static final String THE_MOTHERFUCKER_KILLED_ME   = "THE_MOTHERFUCKER_KILLED_ME"; // Fired when Pacman kills a ghost
            
}
