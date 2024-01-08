import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;

public class PlayMusic {
    private final String backgroundPath = "audio/phon.wav";
    private final String goalPath = "audio/goal.wav";
    private final String shotPath = "audio/shot.wav";
    private final String waterPath = "audio/water.wav";
    private final String menu1Path = "audio/menu1.wav";
    private final String menu2Path = "audio/menu2.wav";
    private final Clip backgroundClip;
    private final Clip goalClip;
    private final Clip shotClip;
    private final Clip waterClip;
    private final Clip menu1Clip;
    private final Clip menu2Clip;
    private boolean isMuted = false;

    public PlayMusic() {
        backgroundClip = loadMusic(backgroundPath, -25f);
        goalClip = loadMusic(goalPath, -10f);
        shotClip = loadMusic(shotPath, -10f);
        waterClip = loadMusic(waterPath, -10f);
        menu1Clip = loadMusic(menu1Path, -10f);
        menu2Clip = loadMusic(menu2Path, -10f);
    }

    private Clip loadMusic(String path, float defaultVolume) {
        Clip musicClip = null;
        try {
            File musicFile = new File(path);
            AudioInputStream musicStream = AudioSystem.getAudioInputStream(musicFile);
            musicClip = AudioSystem.getClip();
            musicClip.open(musicStream);
            FloatControl volumeControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(defaultVolume);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        return musicClip;
    }

    public void playBackground() {
        if (backgroundClip != null && !isMuted) {
            if (!backgroundClip.isRunning()) {
                backgroundClip.setFramePosition(0);
                backgroundClip.start();
                backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public void stopBackground() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }

    public void playGoal() {
        if (goalClip.isRunning()) {
            goalClip.stop();
        }
        goalClip.setFramePosition(0);
        goalClip.start();

    }

    public void playShot() {
        if (shotClip.isRunning()) {
            shotClip.stop();
        }
        shotClip.setFramePosition(0);
        shotClip.start();
    }

    public void playWater() {
        if (waterClip != null) {
            waterClip.setFramePosition(0);
            waterClip.start();
        }
    }

    public void playButtonSound(){
        if (menu1Clip != null) {
            menu1Clip.setFramePosition(0);
            menu1Clip.start();
        }
    }
    
    public void playButtonHoverSound() {
        if (menu2Clip != null) {
            menu2Clip.setFramePosition(0);
            menu2Clip.start();
        }
    }

    public void close() {
        if (backgroundClip != null) {
            backgroundClip.close();
        }
        if (goalClip != null) {
            goalClip.close();
        }
        if (menu1Clip != null) {
            menu1Clip.close();
        }
        if (menu2Clip != null) {
            menu2Clip.close();
        }
    }

    // Set to a very low value to mute
    public void mute() {
        setVolume(-80.0f); 
    }

    // Set back to the original volume
    public void unMute() {
        setVolume(-25.0f); 
    }

    private void setVolume(float volume) {
        // Exclude background music from volume adjustments when muting/unmuting
        if (backgroundClip != null) {
            setClipVolume(backgroundClip, volume);
        }
        if (goalClip != null) {
            setClipVolume(goalClip, volume);
        }
        if (menu1Clip != null) {
            setClipVolume(menu1Clip, volume);
        }
        if (menu2Clip != null) {
            setClipVolume(menu2Clip, volume);
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(volume);
    }
}
