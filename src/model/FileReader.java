package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by mcuper on 19.10.2016.
 */
public class FileReader {

    private byte[] fileContent;

    public FileReader(String pathToFile) {
        FileInputStream fileInputStream;
        File file = new File(pathToFile);
        this.fileContent = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            if (fileInputStream.read(this.fileContent) != file.length())
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
        return this.fileContent;
    }
}
