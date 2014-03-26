/**
 * Created by user on 24.03.14.
 */
public interface PageFaultHandler {
    public int handle(VirtualMemoryBlock[] virtualMemory, int accessedBlock);
}
