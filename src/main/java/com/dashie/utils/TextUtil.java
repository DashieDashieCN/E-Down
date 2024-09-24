package com.dashie.utils;

import com.dashie.entity.ConfigProperties;
import com.dashie.entity.Records;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TextUtil {
    public static final String RECORDS_FILE_NAME = "records.txt";

    // jar包外
    public static String getRecordsFilePath() {
        File directory = new File("");
        String filePath = directory.getAbsolutePath();
        return filePath + "/conf/" + RECORDS_FILE_NAME;
    }

//    // jar包内
//    public static String getRecordsFilePath() {
//        File directory = new File("");
//        String filePath = directory.getAbsolutePath();
//        return filePath + "/resource/" + RECORDS_FILE_NAME;
//    }

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

//    public static Map<String, Records> readLines(String path) throws IOException {
//        Map<String, Records> map = new HashMap<>();
//        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
//        String line;
//        while ((line = br.readLine()) != null) {
//            String[] arr = line.trim().split("-", 3);
//            if (arr.length > 1) {
//                map.put(arr[0], new Records(Long.valueOf(arr[1]), Integer.valueOf(arr[2])));
//            }
//        }
//        br.close();
//        return map;
//    }

    public static Map<String, Long> readLines(String path) throws IOException {
//        TextUtil textUtil = new TextUtil();
        Map<String, Long> map = new HashMap<>();
        // jar包外读取
        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
        // jar包内读取
//        BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(textUtil.getClass().getResourceAsStream(getRecordsFilePath()))));
        String line;
        while ((line = br.readLine()) != null) {
            String[] arr = line.trim().split("-", 2);
            if (arr.length > 1) {
                map.put(arr[0], Long.valueOf(arr[1]));
            }
        }
        br.close();
        return map;
    }

    public static void writeLine(String path, String text) throws IOException {
        FileWriter fw = new FileWriter(path, true);
        fw.write(text);
        fw.flush();
        fw.close();
    }

    public static void coverWriteLine(String path, String text) throws IOException {
        FileWriter fw = new FileWriter(path, false);
        fw.write(text);
        fw.flush();
        fw.close();
    }

//    public static void coverWriteLines(String path, Map<String, Records> map) throws IOException {
//        FileWriter fw = new FileWriter(path, false);
//        Set<String> keySet = map.keySet();
//        for (String key : keySet) {
//            fw.write(key + "-" + map.get(key).toText() + "\n");
//        }
//        fw.flush();
//        fw.close();
//    }

    public static void coverWriteLines(String path, Map<String, Long> map) throws IOException {
//        FileWriter fw = new FileWriter(path, true);
        FileWriter fw = new FileWriter(path, false);
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            fw.write(key + "-" + map.get(key) + "\n");
        }
        fw.flush();
        fw.close();
    }

    public static String genKey(ConfigProperties config) {
        Map<String, String> map = new HashMap<>();
        map.put("address", config.getAddress());
        map.put("tags", config.getTags());
//        map.put("strategyOfDownloaded", String.valueOf(config.getStrategyOfDownload()));
        return String.valueOf(map.hashCode());
    }

    public static String genKey(String address, String tags) {
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("tags", tags);
        return String.valueOf(map.hashCode());
    }

    @Deprecated
    public static String genKey(String address, String tags, int strategyOfDownloaded) {
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("tags", tags);
        map.put("strategyOfDownloaded", String.valueOf(strategyOfDownloaded));
        return String.valueOf(map.hashCode());
    }
}
