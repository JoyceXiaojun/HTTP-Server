
/*
 * the public interfaces are read(), which read a byte per call, and write(), which write a byte per call.
 * The 32-bit memory address is split into tag(high bits), entryIndex(middle bits) and offset within the block(low bits)
 * The entryIndex determines which entry the block goes to. There are sizePerEntry slots in each entry. 
 * After each hit , the timestamp of the hit block is updated.
 * If the entry has available slots, read the according block from memory. If the entry is full, the oldest block is evicted.
 * Use write-back method, which has a dirty bit for each block in cache.
 * We cannot actually read from and write to memory address, so the readFromMem() and writeToMem() methods are just trivial.
 */

public class LRUCache {
    private int tagBitnum;
    private int tagnum;
    private int entryBitnum;
    private int entrynum;
    private int blockBitnum;
    private int blocksize;
    private int sizePerEntry;
    private cacheData[][] cache;

    private class cacheData {
        public boolean dirty;
        public long timestamp;
        public byte[] block;
        public int tag;

        public cacheData(long timestamp, byte[] block, int tag) {
            this.dirty = false;
            this.timestamp = timestamp;
            this.block = block;
            this.tag = tag;
        }

        public void writeByte(int offset, byte b) {
            block[offset] = b;
            this.dirty = true;
        }

        public byte readByte(int offset) {
            return block[offset];
        }
    }

    public LRUCache(int tagBitnum, int entryBitnum, int sizePerEntry) throws Exception {
        if (tagBitnum < 1 || entryBitnum < 1 || (entryBitnum + tagBitnum >= 32)) {
            throw new Exception("Illegal entryBitnum!");
        }
        this.setTagBitnum(tagBitnum);
        this.setTagnum(1 << tagBitnum);
        this.setEntryBitnum(entryBitnum);
        this.setEntrynum(1 << entryBitnum);
        this.setBlockBitnum(32 - tagBitnum - entryBitnum);
        this.setBlocksize(1 << blockBitnum);
        this.setSizePerEntry(sizePerEntry);
        this.cache = new cacheData[entrynum][sizePerEntry];
        for (int i = 0; i < entrynum; i++) {
            for (int j = 0; j < sizePerEntry; j++) {
                cache[i][j] = null;
            }
        }
    }

    private byte[] readFromMem(int start) {
        return new byte[blocksize];
    }

    private void writeToMem(int start, byte[] bytes) {
        return;
    }

    public void write(int address, byte b) {
        int entryIndex = (address >> blockBitnum) & ((1 << entryBitnum) - 1);
        int tag = (address >> (blockBitnum + entryBitnum)) & ((1 << tagBitnum) - 1);
        int offset = address & ((1 << blockBitnum) - 1);
        int start = address & (~((1 << blockBitnum) - 1));
        long oldest = Long.MAX_VALUE;
        int oldestIndex = 0;
        // check if a slot is available or a hit
        int i;
        for (i = 0; i < sizePerEntry; i++) {
            if (cache[entryIndex][i] == null) {// null pointer has to happen
                // after all allocated cache blocks. So encountering a null
                // pointer means a miss in
                // cache and no need to evict.
                cache[entryIndex][i] = new cacheData(System.currentTimeMillis(), readFromMem(start), tag);
                return;
            } else if (cache[entryIndex][i].tag == tag) {// hit
                cache[entryIndex][i].writeByte(offset, b);
                cache[entryIndex][i].timestamp = System.currentTimeMillis();
                return;
            } else {// find one to evict
                if (cache[entryIndex][i].timestamp < oldest) {
                    oldest = cache[entryIndex][i].timestamp;
                    oldestIndex = i;
                }
            }
        }

        // evict
        if (cache[entryIndex][oldestIndex].dirty) {
            writeToMem(start, cache[entryIndex][oldestIndex].block);
        }
        cache[entryIndex][oldestIndex] = new cacheData(System.currentTimeMillis(), readFromMem(start), tag);
        cache[entryIndex][oldestIndex].writeByte(offset, b);
    }

    public byte read(int address) {
        int entryIndex = (address >> blockBitnum) & ((1 << entryBitnum) - 1);
        int tag = (address >> (blockBitnum + entryBitnum)) & ((1 << tagBitnum) - 1);
        int offset = address & ((1 << blockBitnum) - 1);
        int start = address & (~((1 << blockBitnum) - 1));
        long oldest = Long.MAX_VALUE;
        int oldestIndex = 0;
        // check if a slot is available or a hit
        int i;
        for (i = 0; i < sizePerEntry; i++) {
            if (cache[entryIndex][i] == null) {// null pointer has to happen
                                                // after all allocated cache
                                                // blocks. So encountering a
                                                // null pointer means a miss in
                                                // cache and no need to evict.
                cache[entryIndex][i] = new cacheData(System.currentTimeMillis(), readFromMem(start), tag);
                return cache[entryIndex][i].readByte(offset);
            } else if (cache[entryIndex][i].tag == tag) {// hit
                cache[entryIndex][i].timestamp = System.currentTimeMillis();
                return cache[entryIndex][i].readByte(offset);
            } else {// find one to evict
                if (cache[entryIndex][i].timestamp < oldest) {
                    oldest = cache[entryIndex][i].timestamp;
                    oldestIndex = i;
                }
            }
        }

        // evict
        if (cache[entryIndex][oldestIndex].dirty) {
            writeToMem(start, cache[entryIndex][oldestIndex].block);
        }
        cache[entryIndex][oldestIndex] = new cacheData(System.currentTimeMillis(), readFromMem(start), tag);
        return cache[entryIndex][oldestIndex].readByte(offset);
    }

    public int getEntryBitnum() {
        return entryBitnum;
    }

    private void setEntryBitnum(int entryBitnum) {
        this.entryBitnum = entryBitnum;
    }

    public int getSizePerEntry() {
        return sizePerEntry;
    }

    private void setSizePerEntry(int sizePerEntry) {
        this.sizePerEntry = sizePerEntry;
    }

    public int getTagBitnum() {
        return tagBitnum;
    }

    private void setTagBitnum(int tagBitnum) {
        this.tagBitnum = tagBitnum;
    }

    public int getBlockBitnum() {
        return blockBitnum;
    }

    private void setBlockBitnum(int blockBitnum) {
        this.blockBitnum = blockBitnum;
    }

    public int getBlocksize() {
        return blocksize;
    }

    private void setBlocksize(int blocksize) {
        this.blocksize = blocksize;
    }

    public int getEntrynum() {
        return entrynum;
    }

    private void setEntrynum(int entrynum) {
        this.entrynum = entrynum;
    }

    public int getTagnum() {
        return tagnum;
    }

    private void setTagnum(int tagnum) {
        this.tagnum = tagnum;
    }

}