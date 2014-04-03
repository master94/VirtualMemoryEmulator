import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Test {
    public static void main(String[] args) {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("settings.cnf")) {
            prop.load(input);
        } catch (IOException io) {
            io.printStackTrace();
        }

        int pageSize = Integer.parseInt(prop.getProperty("page_size", "1024"));
        int physicalMemoryPages = Integer.parseInt(prop.getProperty("physical_memory_pages", "16"));
        int virtualMemoryPages = Integer.parseInt(prop.getProperty("virtual_memory_pages", "32"));
        final String logFile = prop.getProperty("logfile", "log.log");
        final String commandsFile = prop.getProperty("commands_file", "commands");

        MemoryUnit unit = new MemoryUnit(pageSize, physicalMemoryPages, logFile);
        unit.setPageFaultHandler(new NfuAgingPageFaultHandler());

        CommandProcessor processor = new CommandProcessor(unit, commandsFile);
        EmulatorView view = new EmulatorView(processor);

        view.setMemoryUnit(unit);
        unit.resizeVirtMem(virtualMemoryPages);
        view.show();
    }
}
