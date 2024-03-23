package edu.cornell.gdiac.rabbeat.obstacles;

import edu.cornell.gdiac.rabbeat.Genre;

public interface IGenreObject {
    /**
     * Called whenever the genre switches in {@link edu.cornell.gdiac.rabbeat.GameController} in
     * {@code genreUpdate()}
     */
    public abstract void genreUpdate(Genre genre);

}
