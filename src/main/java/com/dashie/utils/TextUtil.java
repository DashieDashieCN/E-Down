package com.dashie.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.server.ExportException;

public class TextUtil {
    public static final String LATEST_DATE_FILE_NAME = "latest_date.txt";

    public static String getLatestDateFilePath() {
        File directory = new File("");
        String filePath = directory.getAbsolutePath();
        return filePath + "/conf/" + LATEST_DATE_FILE_NAME;
    }
    public static File checkFile(String path) throws Exception {
        System.out.println(" * CHECKING FILE...");
        File file = new File(path);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new Exception("文件创建失败，请检查目标路径: " + path);
            } else {
                System.out.println(" * CREATED: " + path);
            }
        } else {
            System.out.println(" * FILE EXISTS: " + path);
        }
        return file;
    }

    public static String readLine(String path) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
        String line;
        if ((line = br.readLine()) != null) {
            line = line.trim();
            br.close();
            return line;
        } else {
            br.close();
            return null;
        }
    }

    public static void coverWriteLine(String path, String text) throws IOException {
        FileWriter fw = new FileWriter(path, true);
        fw.write(text);
        fw.flush();
        fw.close();
    }
}
