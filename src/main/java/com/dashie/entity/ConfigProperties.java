package com.dashie.entity;

import com.dashie.utils.PropertiesUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Data
@AllArgsConstructor
public class ConfigProperties {
    // API调用策略-单次调用
    public static final int GET_ONCE = 0;
    // API调用策略-多次调用
    public static final int GET_MULTI = 1;
    // 下载策略-全量下载
    public static final int DOWNLOAD_ALL = 0;
    // 下载策略-增量下载
    public static final int DOWNLOAD_NEW = 1;

    private String address;
    private String limit;
    private String tags;
    private String page;
    private String outputPath;
    private Integer strategyOfApi;
    private Integer strategyOfDownload;

    public ConfigProperties() throws Exception {
        System.out.println("==============================================");
        // jar包外读取
        File directory = new File("");
        String filePath = directory.getAbsolutePath() + "/conf/config.properties";
        System.out.println(filePath);
        BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)));
        // jar包内读取
//        InputStream inputStream = ClassLoader.getSystemResourceAsStream("config.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        properties.list(System.out);
        System.out.println("==============================================");
        if (!PropertiesUtil.isNull(properties)) {
            throw new Exception("config.properties 不完整或必填项为空");
        } else {
            this.address = properties.getProperty("address");
            this.limit = properties.getProperty("limit");
            this.tags = properties.getProperty("tags");
            this.page = properties.getProperty("page");
            this.outputPath = properties.getProperty("output.path");
            try {
                this.strategyOfApi = Integer.valueOf(properties.getProperty("strategy.api"));
            } catch (Exception e) {
                throw new Exception("config.properties strategy.api 参数非法", e.getCause());
            }
            try {
                this.strategyOfDownload = Integer.valueOf(properties.getProperty("strategy.download"));
            } catch (Exception e) {
                throw new Exception("config.properties strategy.download 参数非法", e.getCause());
            }
        }

    }
}
