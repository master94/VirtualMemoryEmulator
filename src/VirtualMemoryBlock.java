import java.util.Random;

/**
 * Created by user on 24.03.14.
 */

public class VirtualMemoryBlock {
    private static final int MSB = 0b10000000000000000000000000000000;
    private int physMemoryBlock = -1;
    private int readCounter = 0;
    private int writeCounter = 0;
    private long lastAccessTime = -1;
    private long loadTime = 0;
    private int usingStat = 0;
    private int readWriteDiff = 0;

    public VirtualMemoryBlock() {
    }

    public void touch() {
        usingStat >>= 1;
        if (readWriteDiff > 0)
            usingStat |= MSB;
        else
            usingStat &= ~MSB;
        readWriteDiff = 0;
    }

    public int getReadCounter() {
        return readCounter;
    }

    public int getWriteCounter() {
        return writeCounter;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public long getInMemoryTime() {
        return isMapped() ? System.currentTimeMillis() / 1000 - loadTime : -1;
    }

    public int getUsage() {
        return usingStat;
    }

    public void read() {
        lastAccessTime = System.currentTimeMillis() / 1000;
        ++readCounter;
        ++readWriteDiff;
    }

    public void write() {
        lastAccessTime = System.currentTimeMillis() / 1000;
        ++writeCounter;
        ++readWriteDiff;
    }

    public boolean isMapped() {
        return physMemoryBlock != -1;
    }

    public void unmap() {
        physMemoryBlock = -1;
        usingStat = 0;
    }

    public void map(int physMemoryBlock) {
        this.physMemoryBlock = physMemoryBlock;
        this.loadTime = System.currentTimeMillis() / 1000;
    }

    public int getPhysMemoryBlock() {
        return physMemoryBlock;
    }
}
