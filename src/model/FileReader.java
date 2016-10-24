package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Matus Cuper on 19.10.2016.
 *
 * FileReader class reads file content and returns its like byte array
 */
public class FileReader {

    private byte[] fileContent;

    public FileReader(String pathToFile) {
        FileInputStream fileInputStream;
        File file = new File(pathToFile);
        fileContent = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            if (fileInputStream.read(fileContent) != file.length())
                System.out.println("Error occurred while reading file");
            // TODO add some kind of error handling and test
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            // TODO add logging
            e.printStackTrace();
        } catch (IOException e) {
            // TODO add logging
            e.printStackTrace();
        }
    }

    public byte[] getBytes() {
        return fileContent;
    }
}
