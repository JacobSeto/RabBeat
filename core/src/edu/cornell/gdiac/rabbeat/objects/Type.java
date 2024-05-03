package edu.cornell.gdiac.rabbeat.objects;

public enum Type {
    /** Enum type that determines what happens when the player collides with the object*/
        /** The player*/
        Player,
        /** An obstacle that kills the player*/
        LETHAL,
        /** An obstacle with no exceptional properties */
        NONE,
}
