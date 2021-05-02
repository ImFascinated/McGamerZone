package zone.themcgamer.common;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Braydon
 */
public class ZipUtils {
    /**
     * Zip the given directory
     *
     * @param sourceDirectoryPath the path of the directory to zip
     * @param zipDirectoryPath the path of the output file
     */
    public static void zip(String sourceDirectoryPath, String zipDirectoryPath) {
        try {
            Path zipPath = Files.createFile(Paths.get(zipDirectoryPath));
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                Path sourcePath = Paths.get(sourceDirectoryPath);
                for (Path path : Files.walk(sourcePath).filter(path -> !Files.isDirectory(path)).collect(Collectors.toList())) {
                    ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(path).toString());
                    zipOutputStream.putNextEntry(zipEntry);
                    Files.copy(path, zipOutputStream);
                    zipOutputStream.closeEntry();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void unzip(File source, File output) throws IOException {
        long started = System.currentTimeMillis();

        FileInputStream fileInputStream = new FileInputStream(source);
        ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
        ZipEntry entry = zipInputStream.getNextEntry();
        while (entry != null) {
            File file = new File(output, entry.getName());
            if (entry.isDirectory())
                file.mkdirs();
            else {
                File parent = file.getParentFile();
                if (!parent.exists())
                    parent.mkdirs();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                byte[] buffer = new byte[1024];
                int location;
                while ((location = zipInputStream.read(buffer)) != -1)
                    bufferedOutputStream.write(buffer, 0, location);
                bufferedOutputStream.close();
                fileOutputStream.close();
            }
            entry = zipInputStream.getNextEntry();
        }
        fileInputStream.close();

        zipInputStream.closeEntry();
        zipInputStream.close();

        System.out.println("Finished unzip process for \"" + source.getPath() + "\" in " + (System.currentTimeMillis() - started) + "ms");
    }
}