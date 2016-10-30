package model.fragment;

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
    private boolean isDataCorrupted;
    private int dataChunkSize;
    private int dataLastChunkSize;
    private int dataChunksCount;
    private List<byte[]> dataChunks;

    public Data(byte[] data, int dataChunkSize, int dataType, boolean isDataCorrupted) {
        this.data = data;
        this.dataChunkSize = Math.min(dataChunkSize, getDataLength());
        this.dataType = dataType;
        this.isDataCorrupted = isDataCorrupted;
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

    public byte[] getBytes() {
        return data;
    }

    public int getDataLength() {
        return (data == null) ? 0 : data.length;
    }

    public int getDataType() {
        return dataType;
    }

    public boolean isDataCorrupted() {
        return isDataCorrupted;
    }

    public void setCorruptedData(boolean corruptedData) {
        this.isDataCorrupted = corruptedData;
    }

    public int getDataChunkSize() {
        return dataChunkSize;
    }

    public int getDataLastChunkSize() {
        return dataLastChunkSize;
    }

    public int getDataChunksCount() {
        return dataChunksCount;
    }

    public byte[] getChunk(int index) {
        return dataChunks.get(index);
    }
}
