package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class SoundController {

    private Music synthTrack;
    private Music jazzTrack;

    private float globalMusicVolume = 1.0f;

    private float globalSFXVolume = 1.0f;

    //private float savedJazzVolume = 0;

    //private float savedSynthVolume = 0;

    //private float savedGlobalMusicTempVolume = 0;

    private Sound tempSound;

    public Genre currentGenre;

    /**
     * Set this to true to make genre switches instantaneous / in one frame.
     * Set this to false to make genre switches gradual / over several frames.
     */
    private static final boolean USE_INSTANT_SWITCH = false;

    /** What percentage of volume should be kept when pausing.
     * A value of 0.4 means the track will be 40% as loud as it normally would be.
     */
    private static final float PAUSE_VOL = 0.4f;

    /**
     * The length of time in milliseconds that a gradual genre switch takes.
     * If instant switch is enabled, this value doesn't matter.
     */
    private static final float GRADUAL_SWITCH_DURATION = 1000f;
    private boolean currentlyUpdating;
    private int currentUpdateFrame = 0;

    private ObjectMap<String, Sound> soundNameMap;

    private ObjectMap<Sound, Long> soundIDMap;

    public SoundController() {
        currentlyUpdating = false;
        soundNameMap = new ObjectMap<String, Sound>();
        soundIDMap = new ObjectMap<Sound, Long>();
        currentGenre = Genre.SYNTH;
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
            synthTrack.setVolume(globalMusicVolume);
            jazzTrack.setVolume(0);
        }
        else {
            jazzTrack.setVolume(globalMusicVolume);
            synthTrack.setVolume(0);
        }
    }



    public void setSynthTrack(Music track) {
        synthTrack = track;}

    public void setJazzTrack(Music track) {
        jazzTrack = track;}

    public void setGlobalMusicVolume(float vol) { globalMusicVolume = vol;}

    public void setGlobalSFXVolume(float vol) { globalSFXVolume = vol;}

    public void setGlobalMusicVolumeImmediate(float vol) {
        setGlobalMusicVolumeImmediate(vol, false);

    }

    public void setGlobalSFXVolumeImmediate(float vol) {
        globalSFXVolume = vol;
    }
    public void setGlobalMusicVolumeImmediate(float vol, boolean paused) {
        globalMusicVolume = vol;
        if (currentGenre == Genre.SYNTH) {
            synthTrack.setVolume(vol * (paused ? PAUSE_VOL : 1));
            jazzTrack.setVolume(0);
        }
        else {
            jazzTrack.setVolume(vol * (paused ? PAUSE_VOL : 1));
            synthTrack.setVolume(0);
        }
    }

    public void resetMusic() {
        synthTrack.setPosition(1/44100f);
        jazzTrack.setPosition(1/44100f);
        synthTrack.setVolume(globalMusicVolume);
        jazzTrack.setVolume(0);
        currentlyUpdating = false;
        currentUpdateFrame = 0;
    }

    public void pauseMusic() {
        /*savedJazzVolume = jazzTrack.getVolume();
        savedSynthVolume = synthTrack.getVolume();
        savedGlobalMusicTempVolume = globalMusicVolume;*/
        jazzTrack.pause();
        synthTrack.pause();
    }

    public void disposeMusic(){
        synthTrack.dispose();
        jazzTrack.dispose();
    }

    public void resumeMusic() {
        jazzTrack.play();
        synthTrack.play();

        /*if (savedGlobalMusicTempVolume == 0) {
            jazzTrack.setVolume(globalMusicVolume * (currentGenre == Genre.JAZZ ? 1 : 0));
            synthTrack.setVolume(globalMusicVolume * (currentGenre == Genre.SYNTH ? 1: 0));
        }
        else {
            jazzTrack.setVolume(savedJazzVolume * globalMusicVolume / (savedGlobalMusicTempVolume == 0 ? 1 : savedGlobalMusicTempVolume));
            synthTrack.setVolume(savedSynthVolume * globalMusicVolume / (savedGlobalMusicTempVolume == 0 ? 1 : savedGlobalMusicTempVolume));
        }*/

        if (jazzTrack.getVolume() > 1) jazzTrack.setVolume(1);
        else if (jazzTrack.getVolume() < 0) jazzTrack.setVolume(0);
        if (synthTrack.getVolume() > 1) synthTrack.setVolume(1);
        else if (synthTrack.getVolume() < 0) synthTrack.setVolume(0);

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
        return replaySound( sound, soundId, 1 );
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
        return sound.play(volume * globalSFXVolume);
    }

    /** This method sets the map that maps sound names (strings) to Sound objects (Sounds).
     * It also automatically generates a second private map that maps each of these sounds to a unique ID.
     * The first sound added is assigned ID 0, the second assigned ID 1, etc.
     * @param map The String:Sound map to allow quick playback of a sound based on its name. This name is different from the assets.json entry name for the sound.
     */

    public void addSound(String name, Sound sound) {
        soundNameMap.put(name, sound);
        soundIDMap.put(sound, (long)soundIDMap.size);
    }

    public void playSFX(String soundName) {
        tempSound = soundNameMap.get(soundName);
        replaySound(tempSound, soundIDMap.get(tempSound));
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
            synthTrack.setVolume(globalMusicVolume);
        }
        // The genre just changed from jazz to synth
        else {
            synthTrack.setVolume(0);
            jazzTrack.setVolume(globalMusicVolume);
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
        synthTrack.setVolume(globalMusicVolume - synthTrack.getVolume());
        jazzTrack.setVolume(globalMusicVolume - jazzTrack.getVolume());
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
            jazzTrack.setVolume(jazzTrack.getVolume() + globalMusicVolume/frameCount);
            synthTrack.setVolume(synthTrack.getVolume() - globalMusicVolume/frameCount);
        }

        // The genre just switched from jazz to synth
        else {
            synthTrack.setVolume(synthTrack.getVolume() + globalMusicVolume/frameCount);
            jazzTrack.setVolume(jazzTrack.getVolume() - globalMusicVolume/frameCount);
        }
        if (currentUpdateFrame == frameCount) {
            currentUpdateFrame = 0;
            currentlyUpdating = false;
        }
        if (jazzTrack.getVolume() > 1) jazzTrack.setVolume(1);
        else if (jazzTrack.getVolume() < 0) jazzTrack.setVolume(0);
        if (synthTrack.getVolume() > 1) synthTrack.setVolume(1);
        else if (synthTrack.getVolume() < 0) synthTrack.setVolume(0);
    }
}
