import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Text-based application giving access to a multimedia ordered dictionary.
 * Usage: java Query inputFile
 *
 * @author Seon Sivasathan
 */
public class Query {

    private static final String INVALID_COMMAND = "Invalid command";

    // The dictionary is static because processCommand is a static method
    private static BSTOrderedDictionary dictionary;

    /**
     * Reads the input file and stores its information in the dictionary.
     * The file consists of pairs of lines: a key, then the content.
     */
    public Query(String inputFile) {
        dictionary = new BSTOrderedDictionary();

        try {
            BufferedReader input = new BufferedReader(new FileReader(inputFile));
            String key = input.readLine();
            while (key != null) {
                String content = input.readLine();
                if (content == null) {
                    break; // key without content: ignore it
                }
                content = content.trim();
                dictionary.put(dictionary.getRoot(), key.trim().toLowerCase(),
                               content, determineType(content));
                key = input.readLine();
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Error reading input file " + inputFile + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Determines the type of a content line: audio (2), image (3), html (4)
     * if it is a single word of the form x.y with a known extension,
     * otherwise text (1).
     */
    private static int determineType(String content) {
        int dot = content.lastIndexOf('.');
        boolean singleWord = content.indexOf(' ') < 0 && content.indexOf('\t') < 0;

        if (singleWord && dot > 0 && dot < content.length() - 1) {
            String extension = content.substring(dot + 1).toLowerCase();
            if (extension.equals("wav") || extension.equals("mid")) {
                return 2;
            } else if (extension.equals("jpg") || extension.equals("gif")) {
                return 3;
            } else if (extension.equals("html")) {
                return 4;
            }
        }
        return 1;
    }

    public static void main(String[] args) {
        String result, nextCommand;
        StringReader keyboard = new StringReader();

        if (args.length != 1) {
            System.out.println("Usage: java Query fileName");
            System.exit(0);
        }
        Query myProgram = new Query(args[0]);

        nextCommand = keyboard.read("Enter next command: ");
        while (!nextCommand.trim().equals("end")) {
            result = processCommand(nextCommand);
            if (!result.isEmpty()) {
                System.out.println(result);
            }
            nextCommand = keyboard.read("Enter next command: ");
        }
    }

    /**
     * Processes a user command and returns the text it must output.
     */
    public static String processCommand(String command) {
        StringTokenizer tokens = new StringTokenizer(command);
        if (!tokens.hasMoreTokens()) {
            return INVALID_COMMAND;
        }

        String name = tokens.nextToken().toLowerCase();
        int argCount = tokens.countTokens();

        try {
            if (name.equals("get") && argCount == 1) {
                return processGet(tokens.nextToken().toLowerCase());
            } else if (name.equals("remove") && argCount == 1) {
                String key = tokens.nextToken().toLowerCase();
                try {
                    dictionary.remove(dictionary.getRoot(), key);
                } catch (DictionaryException e) {
                    return noRecordMessage(key);
                }
                return "";
            } else if (name.equals("delete") && argCount == 2) {
                String key = tokens.nextToken().toLowerCase();
                int type = Integer.parseInt(tokens.nextToken());
                try {
                    dictionary.remove(dictionary.getRoot(), key, type);
                } catch (DictionaryException e) {
                    return noRecordMessage(key);
                }
                return "";
            } else if (name.equals("add") && argCount == 3) {
                String key = tokens.nextToken().toLowerCase();
                String content = tokens.nextToken();
                int type = Integer.parseInt(tokens.nextToken());
                dictionary.put(dictionary.getRoot(), key, content, type);
                return "";
            } else if (name.equals("next") && argCount == 2) {
                String key = tokens.nextToken().toLowerCase();
                return listNeighbours(key, Integer.parseInt(tokens.nextToken()), true);
            } else if (name.equals("prev") && argCount == 2) {
                String key = tokens.nextToken().toLowerCase();
                return listNeighbours(key, Integer.parseInt(tokens.nextToken()), false);
            } else if (name.equals("first") && argCount == 0) {
                return keyOf(dictionary.smallest(dictionary.getRoot()));
            } else if (name.equals("last") && argCount == 0) {
                return keyOf(dictionary.largest(dictionary.getRoot()));
            } else if (name.equals("size") && argCount == 0) {
                return "There are " + dictionary.getNumInternalNodes()
                       + " keys in the ordered dictionary";
            }
        } catch (NumberFormatException e) {
            return INVALID_COMMAND;
        }
        return INVALID_COMMAND;
    }

    /**
     * Implements "get k": outputs/plays every item stored under the key, or
     * reports the key missing along with its neighbouring keys.
     */
    private static String processGet(String key) {
        BSTNode root = dictionary.getRoot();
        java.util.ArrayList<MultimediaItem> items = dictionary.get(root, key);

        if (items == null) {
            Data before = dictionary.predecessor(root, key);
            Data after = dictionary.successor(root, key);
            return "The word " + key + " is not in the ordered dictionary.\n"
                   + "Preceding word:  " + (before == null ? "" : before.getName()) + "\n"
                   + "Following word:  " + (after == null ? "" : after.getName());
        }

        StringBuilder text = new StringBuilder();
        SoundPlayer player = new SoundPlayer();
        PictureViewer viewer = new PictureViewer();
        ShowHTML browser = new ShowHTML();

        for (MultimediaItem item : items) {
            String content = item.getContent();
            try {
                switch (item.getType()) {
                    case 1:
                        text.append(content).append("\n");
                        break;
                    case 2:
                        player.play(content);
                        break;
                    case 3:
                        viewer.show(content);
                        break;
                    case 4:
                        browser.show(content);
                        break;
                    default:
                        text.append("Unknown item type for ").append(content).append("\n");
                }
            } catch (MultimediaException e) {
                text.append("Unable to process file ").append(content)
                    .append(": ").append(e.getMessage()).append("\n");
            }
        }
        return text.toString().trim();
    }

    /**
     * Implements "next" (forward = true) and "prev" (forward = false): lists
     * the key (if stored) followed by up to count successive neighbours.
     */
    private static String listNeighbours(String key, int count, boolean forward) {
        BSTNode root = dictionary.getRoot();
        StringBuilder keys = new StringBuilder();

        if (dictionary.get(root, key) != null) {
            keys.append(key);
        }

        String current = key;
        for (int i = 0; i < count; i++) {
            Data neighbour = forward ? dictionary.successor(root, current)
                                     : dictionary.predecessor(root, current);
            if (neighbour == null) {
                break;
            }
            current = neighbour.getName();
            if (keys.length() > 0) {
                keys.append(" ");
            }
            keys.append(current);
        }

        if (keys.length() == 0) {
            return forward ? "There are no keys larger than or equal to " + key
                           : "There are no keys smaller than or equal to " + key;
        }
        return keys.toString();
    }

    /**
     * Message printed when a command refers to a key that is not stored.
     */
    private static String noRecordMessage(String key) {
        return "No record in the ordered dictionary has key " + key + ".";
    }

    /**
     * Returns the key of the given Data, or the empty-dictionary message.
     */
    private static String keyOf(Data data) {
        if (data == null) {
            return "The ordered dictionary is empty.";
        }
        return data.getName();
    }
}
