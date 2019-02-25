package lab2.photostar.utils;

import java.io.File;

public final class FileUtils {
    public static boolean rename(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);

        return oldFile.renameTo(newFile);
    }
}
