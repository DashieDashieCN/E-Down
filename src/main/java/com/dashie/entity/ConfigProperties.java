package com.dashie.entity;

import com.dashie.utils.PropertiesUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigProperties {
    // 自动创建输出创建路径-否
    public static final int OUTPUT_AUTO_CREATE_NO = 0;
    // 自动创建输出创建路径-是
    public static final int OUTPUT_AUTO_CREATE_YES = 1;
    // 自动生成额外输出文件夹-否
    public static final int OUTPUT_AUTO_GENERATE_NO = 0;
    // 自动生成额外输出文件夹-根据完整tags
    public static final int OUTPUT_AUTO_GENERATE_BY_TAGS = -1;
    // 下载完成后自动打开输出文件夹-否
    public static final int OUTPUT_AUTO_OPEN_NO = 0;
    // 下载完成后自动打开输出文件夹-是
    public static final int OUTPUT_AUTO_OPEN_YES = 1;
    // API调用策略-单次调用
    public static final int GET_ONCE = 0;
    // API调用策略-多次调用
    public static final int GET_MULTI = 1;
    // 下载策略-全量下载
    public static final int DOWNLOAD_ALL = 0;
    // 下载策略-增量下载
    public static final int DOWNLOAD_NEW = 1;
    // 退出策略-自动退出
    public static final int EXIT_MANUAL = 0;

    private String address;
    private String limit;
    private String tags;
    private String page;
    private String outputPath;
    private Integer outputAutoCreate;
    private Integer outputAutoGenerate;
    private Integer outputAutoOpen;
    private Integer strategyOfApi;
    private Integer strategyOfDownload;
    private Integer strategyOfExit;

    public void readFromProperties() throws Exception {
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

            this.outputAutoCreate = readPropertyAsInteger(properties, "output.auto.create");
            this.outputAutoGenerate = readPropertyAsInteger(properties, "output.auto.generate");
            this.outputAutoOpen = readPropertyAsInteger(properties, "output.auto.open");

            this.strategyOfApi = readPropertyAsInteger(properties, "strategy.api");
            this.strategyOfDownload = readPropertyAsInteger(properties, "strategy.download");
            this.strategyOfExit = readPropertyAsInteger(properties, "strategy.exit");
        }
    }

    /**
     * 以Integer格式读取配置值
     * @param properties 配置
     * @param key 键
     * @return Integer格式的配置值
     * @throws Exception 配置项参数非法
     */
    public static Integer readPropertyAsInteger(Properties properties, String key) throws Exception {
        int ret;
        try {
            ret = Integer.parseInt(properties.getProperty(key));
            return ret;
        } catch (Exception e) {
            throw new Exception("config.properties strategy.api 参数非法", e.getCause());
        }
    }
}
