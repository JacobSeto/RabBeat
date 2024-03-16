package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundController {

    private Music synthTrack;
    private Music jazzTrack;

    private Genre currentGenre;

    /**
     * Set this to true to make genre switches instantaneous / in one frame.
     * Set this to false to make genre switches gradual / over several frames.
     */
    private static final boolean USE_INSTANT_SWITCH = true;

    /**
     * The length of time in milliseconds that a gradual genre switch takes.
     * If instant switch is enabled, this value doesn't matter.
     */
    private static final float GRADUAL_SWITCH_DURATION = 1000f;
    private boolean currentlyUpdating;
    private int currentUpdateFrame = 0;

    public SoundController() {
        currentGenre = Genre.SYNTH;
        currentlyUpdating = false;
    }

    public void playMusic() {
        synthTrack.setLooping(true);
        jazzTrack.setLooping(true);
        synthTrack.play();
        jazzTrack.play();
    }

    public void playMusic(Genre genre) {
        playMusic();
        if (genre == Genre.SYNTH) {
            synthTrack.setVolume(1);
            jazzTrack.setVolume(0);
        }
        else {
            jazzTrack.setVolume(1);
            synthTrack.setVolume(0);
        }
    }



    public void setSynthTrack(Music track) { synthTrack = track;}

    public void setJazzTrack(Music track) { jazzTrack = track;}

    public void resetMusic() {
        synthTrack.stop();
        jazzTrack.stop();
        playMusic(Genre.SYNTH);
    }

    /**
     * Method to ensure that a sound asset is only played once.
     *
     * Every time you play a sound asset, it makes a new instance of that sound.
     * If you play the sounds to close together, you will have overlapping copies.
     * To prevent that, you must stop the sound before you play it again.  That
     * is the purpose of this method.  It stops the current instance playing (if
     * any) and then returns the id of the new instance for tracking.
     *
     * @param sound		The sound asset to play
     * @param soundId	The previously playing sound instance
     *
     * @return the new sound instance for this asset.
     */
    public long replaySound(Sound sound, long soundId) {
        return replaySound( sound, soundId, 1.0f );
    }


    /**
     * Method to ensure that a sound asset is only played once.
     *
     * Every time you play a sound asset, it makes a new instance of that sound.
     * If you play the sounds to close together, you will have overlapping copies.
     * To prevent that, you must stop the sound before you play it again.  That
     * is the purpose of this method.  It stops the current instance playing (if
     * any) and then returns the id of the new instance for tracking.
     *
     * @param sound		The sound asset to play
     * @param soundId	The previously playing sound instance
     * @param volume	The sound volume
     *
     * @return the new sound instance for this asset.
     */
    public long replaySound(Sound sound, long soundId, float volume) {
        if (soundId != -1) {
            sound.stop( soundId );
        }
        return sound.play(volume);
    }


    /**
     * This method sets the genre AND sets the currentlyUpdating flag to true.
     * update() then uses that flag to gradually or instantly change the music.
     * It also resets any ongoing gradual shifts, which is why it might look backwards.
     * If instant shifts are enabled, the volumes set here will be immediately overridden.
     * @param genre
     */
    /*
    TODO: Maybe change behavior of quick genre switches when each switch is gradual.
     * Currently, if a switch happens twice in quick succession,
     * the first gradual switch will be replaced with an instantaneous switch to allow
     * the second one to happen gradually.
     */
    public void setGenre(Genre genre) {
        currentGenre = genre;
        currentlyUpdating = true;
        currentUpdateFrame = 0;

        if (USE_INSTANT_SWITCH) return;

        // Otherwise, skip to the end of the previous genre switch

        // The genre just changed from synth to jazz
        if (genre == Genre.JAZZ) {
            jazzTrack.setVolume(0);
            synthTrack.setVolume(1);
        }
        // The genre just changed from jazz to synth
        else {
            synthTrack.setVolume(0);
            jazzTrack.setVolume(1);
        }
    }

    public void update() {
        if (!currentlyUpdating) return;
        if (USE_INSTANT_SWITCH) {
            switchMusicGenreInstant();
        }
        else {

            // Convert from milliseconds to frames
            switchMusicGenreGradual(GRADUAL_SWITCH_DURATION * 60f / 1000f);
        }
    }

    /**
     * Immediately switches from synth to jazz, or vice versa.
     * The switch has no delay.
     */
    public void switchMusicGenreInstant() {
        synthTrack.setVolume(1 - synthTrack.getVolume());
        jazzTrack.setVolume(1 - jazzTrack.getVolume());
        currentlyUpdating = false;
    }

    /**
     * Switches from synth to jazz in a given number of frames.
     * Each method call makes a small adjustment to the volume of both tracks.
     * @param frameCount The number of frames between the start of the transition and the end.
     */

    public void switchMusicGenreGradual(float frameCount) {
        currentUpdateFrame++;
        // The genre just switched from synth to jazz
        if (currentGenre == Genre.JAZZ) {
            jazzTrack.setVolume(jazzTrack.getVolume() + 1/frameCount);
            synthTrack.setVolume(synthTrack.getVolume() - 1/frameCount);
        }

        // The genre just switched from jazz to synth
        else {
            synthTrack.setVolume(synthTrack.getVolume() + 1/frameCount);
            jazzTrack.setVolume(jazzTrack.getVolume() - 1/frameCount);
        }
        if (currentUpdateFrame > frameCount) {
            currentUpdateFrame = 0;
            currentlyUpdating = false;
        }
    }
}
