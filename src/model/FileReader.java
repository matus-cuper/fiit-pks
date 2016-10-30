package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Matus Cuper on 19.10.2016.
 *
 * FileReader class reads file content and returns its like byte array
 */
public class FileReader {

    private byte[] fileContent;
    private static String fileName;

    public FileReader(String pathToFile) throws IOException {
        fileName = Paths.get(pathToFile).getFileName().toString();
        File file = new File(pathToFile);
        FileInputStream fileInputStream = new FileInputStream(file);
        fileContent = new byte[(int) file.length() - 1];
        fileInputStream.read(fileContent);
        fileInputStream.close();
    }

    public byte[] getBytes() {
        return fileContent;
    }

    static byte[] getFileName() {
        return fileName.getBytes();
    }
}
