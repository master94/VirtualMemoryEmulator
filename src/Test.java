import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Test {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("USAGE program <config-file-name>");
            return;
        }

        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(args[0])) {
            prop.load(input);
        } catch (IOException io) {
            io.printStackTrace();
        }

        final int commandsPerTick = Integer.parseInt(prop.getProperty("commands_per_tick", "3"));
        final int pageSize = Integer.parseInt(prop.getProperty("page_size", "1024"));
        final int physicalMemoryPages = Integer.parseInt(prop.getProperty("physical_memory_pages", "16"));
        final int virtualMemoryPages = Integer.parseInt(prop.getProperty("virtual_memory_pages", "32"));
        final String logFile = prop.getProperty("logfile", "log.log");
        final String commandsFile = prop.getProperty("commands_file", "commands");

        MemoryUnit unit = new MemoryUnit(pageSize, physicalMemoryPages, logFile);
        unit.setPageFaultHandler(new LruAgingPageFaultHandler());

        CommandProcessor processor = new CommandProcessor(unit, commandsFile, commandsPerTick);

        EmulatorView view = new EmulatorView(processor);
        view.setMemoryUnit(unit);

        unit.resizeVirtMem(virtualMemoryPages);
        view.show();
    }
}
