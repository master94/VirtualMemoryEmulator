/**
 * Created by user on 23.03.14.
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Observable;
import java.util.Random;

public class MemoryUnit extends Observable {

    private enum OperationType {
        Read,
        Write
    }

    private VirtualMemoryBlock[] virtualMemory;
    private int memBlockSize;
    private int physicalMemoryBlocks;
    private PageFaultHandler pageFaultHandler;
    private OutputStream logger;


    void performOperation(OperationType type, long address) {
        int blockIndex = getBlockIndex(address);

        try {
            logger.write(String.format("%s %d (BLOCK %d):", type.name(), address, blockIndex).getBytes());

            if (!virtualMemory[blockIndex].isMapped()) {
                handleFault(blockIndex);
                logger.write("PAGE FAULT\n".getBytes());
            }
            else {
                logger.write("SUCCESS\n".getBytes());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        VirtualMemoryBlock block = virtualMemory[blockIndex];

        switch (type) {
            case Read:
                block.read();
                break;
            case Write:
                block.write();
                break;
            default:
                throw new IllegalArgumentException("No such operation type.");
        }

        virtualMemory[blockIndex].read();
    }

    private void notifyView() {
        setChanged();
        notifyObservers(virtualMemory);
    }

    private int getBlockIndex(long address) {
        return (int)(address / memBlockSize);
    }

    private void handleFault(int blockIndex) {
        int chosenBlock = pageFaultHandler.handle(virtualMemory, blockIndex);
        virtualMemory[blockIndex].map(virtualMemory[chosenBlock].getPhysMemoryBlock());
        virtualMemory[chosenBlock].unmap();
    }

    private void initVirtualMemory() {
        for (int i = 0; i < virtualMemory.length; ++i) {
            virtualMemory[i] = new VirtualMemoryBlock();
        }

        VirtualMemoryBlock[] tmpArray = virtualMemory.clone();
        Collections.shuffle(Arrays.asList(tmpArray));

        for (int i = 0; i < physicalMemoryBlocks; ++i) {
            tmpArray[i].map(i);
        }
    }

    public MemoryUnit(int memBlockSize, int physicalMemoryBlocks, String logFile) {
        this.memBlockSize = memBlockSize;
        this.physicalMemoryBlocks = physicalMemoryBlocks;

        try {
            this.logger = new FileOutputStream(logFile);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void updateMemory() {
        for (VirtualMemoryBlock block : virtualMemory) {
            block.touch();
        }
    }

    public void setPageFaultHandler(PageFaultHandler pageFaultHandler) {
        this.pageFaultHandler = pageFaultHandler;
    }

    public void resizeVirtMem(int size) {
        virtualMemory = new VirtualMemoryBlock[size];
        initVirtualMemory();
        notifyView();
    }

    public void write(long address) {
        performOperation(OperationType.Write, address);
        notifyView();
    }

    public void read(long address) {
        performOperation(OperationType.Read, address);
        notifyView();
    }
}
