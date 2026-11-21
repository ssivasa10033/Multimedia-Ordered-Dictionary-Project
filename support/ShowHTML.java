import java.io.File;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * Displays an HTML document in its own window.
 *
 * Support type, written to make this project self-contained.
 */
public class ShowHTML {

    /**
     * Opens a window rendering the given HTML file.
     *
     * @throws MultimediaException if the file is missing or cannot be rendered
     */
    public void show(String fileName) throws MultimediaException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new MultimediaException("file " + fileName + " was not found");
        }

        try {
            JEditorPane page = new JEditorPane(file.toURI().toURL());
            page.setEditable(false);

            JFrame frame = new JFrame(fileName);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(new JScrollPane(page));
            frame.setSize(800, 600);
            frame.setVisible(true);
        } catch (IOException e) {
            throw new MultimediaException("could not display " + fileName + ": " + e.getMessage());
        }
    }
}
