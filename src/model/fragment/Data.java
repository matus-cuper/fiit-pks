package model.fragment;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Matus Cuper on 24.10.2016.
 *
 * Data class simplify ClientServer class and detach data stuff from connection stuff
 */
public class Data {

    private static final int MAX_CHUNK_SIZE = 65498;
    private static final int MAX_CHUNKS_COUNT = 65535;

    private byte[] data;
    private int dataType;
    private int dataChunkSize;
    private int dataLastChunkSize;
    private int dataChunksCount;
    private List<byte[]> dataChunks;

    public Data(byte[] data, int dataChunkSize, int dataType) {
        this.data = data;
        this.dataChunkSize = dataChunkSize;
        this.dataType = dataType;
        computeMetadata();
        if (isValid())
            computeChunks();
    }

    private void computeMetadata() {
        dataChunksCount = (getDataLength() / dataChunkSize) + 1;
        dataLastChunkSize = (getDataLength() % dataChunkSize);
    }

    private void computeChunks() {
        int index = 0;
        dataChunks = new ArrayList<>();
        while (index < getDataLength()) {
            dataChunks.add(Arrays.copyOfRange(data, index, Math.min(index + dataChunkSize, getDataLength())));
            index += dataChunkSize;
        }
    }

    public boolean isValid() {
        return (dataChunkSize <= MAX_CHUNK_SIZE && dataChunksCount <= MAX_CHUNKS_COUNT);
    }

    public byte[] getChunk(int index) {
        return dataChunks.get(index);
    }
    public byte[] getBytes() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getDataChunkSize() {
        return dataChunkSize;
    }

    public void setDataChunkSize(int dataChunkSize) {
        this.dataChunkSize = dataChunkSize;
    }

    public int getDataLastChunkSize() {
        return dataLastChunkSize;
    }

    public void setDataLastChunkSize(int dataLastChunkSize) {
        this.dataLastChunkSize = dataLastChunkSize;
    }

    public int getDataChunksCount() {
        return dataChunksCount;
    }

    public void setDataChunksCount(int dataChunksCount) {
        this.dataChunksCount = dataChunksCount;
    }

    public List<byte[]> getDataChunks() {
        return dataChunks;
    }

    public void setDataChunks(List<byte[]> dataChunks) {
        this.dataChunks = dataChunks;
    }

    @Contract(pure = true)
    public int getDataLength() {
        return (data == null) ? 0 : data.length;
    }
}
