package com.dashie.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.dashie.entity.ConfigProperties.*;

public class DirectoryUtil {
    public static final String SPLITTER = ".";

    public static void checkDirectory(String path) throws Exception {
        System.out.println(" * CHECKING PATH...");
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new Exception("文件夹创建失败，请检查目标路径: " + path);
            } else {
                System.out.println(" * CREATED: " + path);
            }
        } else {
            System.out.println(" * PATH EXISTS: " + path);
        }
    }

    public static String generateDirectory(String path, String tags, Integer strategy) throws Exception {
        if (tags == null || tags.trim().isEmpty()) {
            return path;
        }
        if (strategy > OUTPUT_AUTO_GENERATE_NO) {
            String[] tagArray = tags.split(" ");
            if (tagArray.length > strategy) {
                tagArray = Arrays.copyOfRange(tagArray, 0, strategy);
            }
            String dirName = path + "/" + String.join(SPLITTER, tagArray);
            checkDirectory(dirName);
            return dirName;
        } else if (strategy == OUTPUT_AUTO_GENERATE_BY_TAGS) {
            String dirName = path + "/" + tags.replace(" ", SPLITTER);
            checkDirectory(dirName);
            return dirName;
        } else if (strategy == OUTPUT_AUTO_GENERATE_BY_TIME) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String dirName = path + "/" + format.format(date);
            checkDirectory(dirName);
            return dirName;
        }
        return path;
    }

    public static void open(String path) throws IOException {
        Desktop.getDesktop().open(new File(path));
    }

}
