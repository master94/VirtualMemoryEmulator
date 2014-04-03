import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by user on 25.03.14.
 */

class Command {
    String command;
    long address;
}

public class CommandProcessor {
    private static final String READ_TAG = "READ";
    private static final String WRITE_TAG = "WRITE";
    private MemoryUnit memUnit;
    private final ArrayList<Command> commands = new ArrayList<>();
    private int currCommand = 0;

    public CommandProcessor(MemoryUnit memUnit, String fileName) {
        this.memUnit = memUnit;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                Command command = new Command();
                command.command = tokenizer.nextToken();
                command.address = Long.parseLong(tokenizer.nextToken());
                commands.add(command);
            }
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void updateMemory() {
        memUnit.updateMemory();
    }

    public boolean hasNextCommand() {
        return currCommand < commands.size();
    }

    public Command getNextCommand() {
        return commands.get(currCommand);
    }

    public void proceedNextCommand() {
        Command command = commands.get(currCommand++);

        switch (command.command) {
            case READ_TAG:
                memUnit.read(command.address);
                break;
            case WRITE_TAG:
                memUnit.write(command.address);
                break;
            default:
                break;
        }
    }
}
