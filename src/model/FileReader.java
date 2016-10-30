package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Matus Cuper on 19.10.2016.
 *
 * FileReader class reads file content and returns its like byte array
 */
public class FileReader {

    private byte[] fileContent;

    public FileReader(String pathToFile) throws IOException {
        FileInputStream fileInputStream;
        File file = new File(pathToFile);
        fileContent = new byte[(int) file.length()];
        fileInputStream = new FileInputStream(file);
        fileInputStream.close();
    }

    public byte[] getBytes() {
        return fileContent;
    }
}
