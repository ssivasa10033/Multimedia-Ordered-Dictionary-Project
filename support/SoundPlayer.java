import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Plays an audio file given its name.
 *
 * Support type, written to make this project self-contained.
 */
public class SoundPlayer {

    /**
     * Plays the given audio file.
     *
     * @throws MultimediaException if the file is missing, unreadable, or not
     *                             in a format the audio system supports
     */
    public void play(String fileName) throws MultimediaException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new MultimediaException("file " + fileName + " was not found");
        }

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            throw new MultimediaException(fileName + " is not a supported audio format");
        } catch (LineUnavailableException e) {
            throw new MultimediaException("no audio output is available");
        } catch (IOException e) {
            throw new MultimediaException("could not read " + fileName);
        }
    }
}
