/**
 * Created by user on 23.03.14.
 */

import java.util.Observable;
import java.util.Random;

public class MemoryUnit extends Observable {
    private VirtualMemoryBlock[] virtualMemory;
    private int memBlockSize;
    private int physicalMemoryBlocks;
    private PageFaultHandler pageFaultHandler;

    private void notifyView() {
        setChanged();
        notifyObservers(virtualMemory);
    }

    private int getBlockIndex(long address) {
        return (int)(address / memBlockSize);
    }

    private void checkAndHandleFault(int blockIndex) {
        VirtualMemoryBlock memBlock = virtualMemory[blockIndex];
        if (!memBlock.isMapped()) {
            int chosenBlock = pageFaultHandler.handle(virtualMemory, blockIndex);
            virtualMemory[blockIndex].map(virtualMemory[chosenBlock].getPhysMemoryBlock());
            virtualMemory[chosenBlock].unmap();
        }
    }

    private void initVirtualMemory() {
        Random rand = new Random();
        for (int i = 0; i < virtualMemory.length; ++i) {
            virtualMemory[i] = new VirtualMemoryBlock();
            virtualMemory[i].map(rand.nextBoolean() ? i : -1);
        }
    }

    public MemoryUnit(int virtualMemoryBlocks, int memBlockSize, int physicalMemoryBlocks,
                      PageFaultHandler pageFaultHandler) {
        this.memBlockSize = memBlockSize;
        this.physicalMemoryBlocks = physicalMemoryBlocks;
        this.pageFaultHandler = pageFaultHandler;

        virtualMemory = new VirtualMemoryBlock[virtualMemoryBlocks];
        initVirtualMemory();
    }

    public void resizeVirtMem(int size) {
        virtualMemory = new VirtualMemoryBlock[size];
        initVirtualMemory();
        notifyView();
    }

    public void write(long address) {
        int blockIndex = getBlockIndex(address);
        checkAndHandleFault(blockIndex);
        virtualMemory[blockIndex].write();
        notifyView();
    }

    public void read(long address) {
        int blockIndex = getBlockIndex(address);
        checkAndHandleFault(blockIndex);
        virtualMemory[blockIndex].read();
        notifyView();
    }
}
