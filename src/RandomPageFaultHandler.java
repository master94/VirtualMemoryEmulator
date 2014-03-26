/**
 * Created by user on 26.03.14.
 */
public class RandomPageFaultHandler implements PageFaultHandler {
    @Override
    public int handle(VirtualMemoryBlock[] virtualMemory, int accessedBlock) {
        for (int i = 0; i < virtualMemory.length; ++i) {
            if (virtualMemory[i].isMapped() && i != accessedBlock)
                return i;
        }

        return 0;
    }
}
