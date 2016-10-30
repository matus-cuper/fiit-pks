package model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * Created by Matus Cuper on 30.10.2016.
 *
 * FileWriter class writes byte array as file to disk
 */
class FileWriter {

    private String fileName;

    public FileWriter(String fileName) {
        if (!fileName.isEmpty())
            this.fileName = fileName;
        else
            this.fileName = Instant.now().toString();
    }

    void createFile(byte[] fileContent) throws IOException {
        File file = new File(Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(fileContent);
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
