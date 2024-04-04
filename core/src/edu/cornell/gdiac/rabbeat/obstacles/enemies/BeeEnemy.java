package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.obstacles.WheelGameObject;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

public class BeeEnemy extends WheelGameObject implements ISynced, IGenreObject {

    public int beatCount = 0;

    public BeeEnemy(float x, float y, float radius) {
        super(x, y, radius);
    }

    @Override
    public float getBeat() {
        return 1;
    }

    @Override
    public void beatAction() {
        setVY(getVY() * -1);
    }

    @Override
    public void genreUpdate(Genre genre) {
        if (genre == Genre.SYNTH){
            if (getVY() < 0){
                setVY(-2);
            }
            else {
                setVY(2);
            }
        }
        else {
            if (getVY() < 0){
                setVY(-1);
            }
            else {
                setVY(1);
            }
        }
    }
}
