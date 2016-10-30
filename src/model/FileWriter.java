package model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Matus Cuper on 30.10.2016.
 *
 * FileWriter class writes byte array as file to disk
 */
class FileWriter {

    private static String  fileName;

    FileWriter(byte[] fileContent) throws IOException {
        File file = new File(Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + "crc_test.c");
        System.out.println(Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(fileContent);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    static void setFileName(String newFileName) {
        fileName = newFileName;
    }

    static String getFileName() {
        return fileName;
    }
}
