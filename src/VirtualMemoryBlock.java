import java.util.Random;

/**
 * Created by user on 24.03.14.
 */

public class VirtualMemoryBlock {
    private int physMemoryBlock = -1;
    private int readCounter = 0;
    private int writeCounter = 0;
    private long lastAccessTime = -1;
    private long loadTime = 0;

    public VirtualMemoryBlock() {
    }

    public void randomize() {
        Random rand = new Random();
        readCounter = rand.nextInt(100);
        writeCounter = rand.nextInt(100);
        lastAccessTime = rand.nextInt(100000000);
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

    public void read() {
        lastAccessTime = System.currentTimeMillis() / 1000;
        ++readCounter;
    }

    public void write() {
        lastAccessTime = System.currentTimeMillis() / 1000;
        ++writeCounter;
    }

    public boolean isMapped() {
        return physMemoryBlock != -1;
    }

    public void unmap() {
        physMemoryBlock = -1;
    }

    public void map(int physMemoryBlock) {
        this.physMemoryBlock = physMemoryBlock;
        this.loadTime = System.currentTimeMillis() / 1000;
    }

    public int getPhysMemoryBlock() {
        return physMemoryBlock;
    }
}
