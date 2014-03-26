/**
 * Created by user on 24.03.14.
 */
public class Test {
    public static void main(String[] args) {
        MemoryUnit unit = new MemoryUnit(10, 100, 5, new RandomPageFaultHandler());
        CommandProcessor processor = new CommandProcessor(unit, "/Users/user/Desktop/VirtualMemoryEmulator/out/production/VirtualMemoryEmulator/commands");
        EmulatorView view = new EmulatorView(processor);
        view.setMemoryUnit(unit);
        unit.resizeVirtMem(10);
        view.show();
    }
}
