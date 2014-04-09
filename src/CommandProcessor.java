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
    private static final String RANDOM_TAG = "RANDOM";

    private MemoryUnit memUnit;
    private final ArrayList<Command> commands = new ArrayList<>();
    private int commandsPerTick;
    private int currCommand = 0;

    public CommandProcessor(MemoryUnit memUnit, String fileName, int commandsPerTick) {
        this.memUnit = memUnit;
        this.commandsPerTick = commandsPerTick;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                Command command = new Command();
                command.command = tokenizer.nextToken();

                String address = tokenizer.nextToken();
                command.address = address.compareTo(RANDOM_TAG) == 0 ? -1 : Long.parseLong(address);
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

        if (currCommand % commandsPerTick == 0)
            memUnit.updateMemory();
    }
}
