import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Reads a line of input from the keyboard after printing a prompt.
 * On end of input it returns "end" so the command loop terminates cleanly.
 *
 * Support type, written to make this project self-contained.
 */
public class StringReader {

    private BufferedReader input;

    public StringReader() {
        input = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Prints prompt and returns the line the user typed.
     */
    public String read(String prompt) {
        System.out.print(prompt);
        try {
            String line = input.readLine();
            if (line == null) {
                return "end";
            }
            return line;
        } catch (IOException e) {
            return "end";
        }
    }
}
