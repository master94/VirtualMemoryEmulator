import com.sun.deploy.util.ArrayUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by user on 28.03.14.
 */
public class NfuAgingPageFaultHandler implements PageFaultHandler {
    @Override
    public int handle(VirtualMemoryBlock[] virtualMemory, int accessedBlock) {
        int index = -1;

        for (int i = 0; i < virtualMemory.length; ++i) {
            if (virtualMemory[i].isMapped()) {
                if (index == -1) {
                    index = i;
                    continue;
                }

                final long ethalon = virtualMemory[index].getUsage() & 0xFFFFFFFFl;
                final long newValue = virtualMemory[i].getUsage() & 0xFFFFFFFFl;

                if (newValue < ethalon) {
                    index = i;
                }
            }
        }

        return index;
    }
}
