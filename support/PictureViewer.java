import java.awt.BorderLayout;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Displays an image file in its own window.
 *
 * Support type, written to make this project self-contained.
 */
public class PictureViewer {

    /**
     * Opens a window showing the given image file.
     *
     * @throws MultimediaException if the file is missing or is not a readable image
     */
    public void show(String fileName) throws MultimediaException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new MultimediaException("file " + fileName + " was not found");
        }

        ImageIcon picture = new ImageIcon(fileName);
        if (picture.getIconWidth() <= 0) {
            throw new MultimediaException(fileName + " is not a readable image");
        }

        JFrame frame = new JFrame(fileName);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new JLabel(picture), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
