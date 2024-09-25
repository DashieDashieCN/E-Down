package com.dashie;

import com.alibaba.fastjson2.JSONObject;
import com.dashie.client.HttpClient;
import com.dashie.entity.ConfigProperties;
import com.dashie.entity.FileInfo;
import com.dashie.utils.DirectoryUtil;
import com.dashie.utils.JsonUtil;
import com.dashie.utils.TextUtil;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.dashie.common.StrategyName.STRATEGY_DOWNLOAD;
import static com.dashie.entity.ConfigProperties.*;
import static com.dashie.utils.ScreenPrintUtil.*;

public class Main {
    public static final String VERSION = "V0.3 beta";

    public static void main(String[] args) throws Exception {
        printTitle();
        int downloadedCount = 0;
        ConfigProperties config = new ConfigProperties();
        try {
            // 获取配置文件并检查完整性
            config.readFromProperties();
            Thread.sleep(1000);
            // 创建输出路径
            if (config.getOutputAutoCreate() == OUTPUT_AUTO_CREATE_YES) {
                DirectoryUtil.checkDirectory(config.getOutputPath());
            }
            // 创建子目录
            String newPath = DirectoryUtil.generateDirectory(config.getOutputPath(), config.getTags(), config.getOutputAutoGenerate());
            config.setOutputPath(newPath);
            // 调取API并下载图片
            if (config.getStrategyOfApi() == GET_ONCE) {
                // 单词调用
                downloadedCount = handleDownload(config.getAddress(), config.getLimit(), config.getTags(), config.getPage(), config.getOutputPath());
            } else if (config.getStrategyOfApi() == GET_MULTI) {
                // 多次调用
                if (config.getStrategyOfDownload() == DOWNLOAD_ALL) {
                    // 全量下载
                    downloadedCount = handleDownload(config.getAddress(), config.getLimit(), config.getTags(), config.getOutputPath());
                } else if (config.getStrategyOfDownload() == DOWNLOAD_NEW) {
                    // 增量下载
                    TextUtil.checkFile(TextUtil.getRecordsFilePath());  // 不存在记录文件时主动创建
                    Map<String, Long> records = TextUtil.readLines(TextUtil.getRecordsFilePath());   // 获取记录
                    downloadedCount = handleDownload(config.getAddress(), config.getLimit(), config.getTags(), config.getOutputPath(), records);
                }
            }
            // 自动打开输出目录
            if (config.getOutputAutoOpen() == OUTPUT_AUTO_OPEN_YES) {
                DirectoryUtil.open(config.getOutputPath());
            }
        } catch (Exception e) {
            System.out.println("ERROR: \n");
            e.printStackTrace(System.err);
        }
        System.out.println(" * DOWNLOADED: " + downloadedCount);
        // 自动退出
        int exitTime = config.getStrategyOfExit();
        if (exitTime > EXIT_MANUAL) {
            while (exitTime > 0) {
                printWithBackgroundColor("程序已结束，将在" + exitTime + "秒后自动退出", COLOR_WHITE,STYLE_UNDERLINE);
//                System.out.println("程序已结束，将在" + config.getStrategyOfExit() + "秒后自动退出");
                Thread.sleep(1000);
                exitTime--;
            }
            return;
        }
        // 程序结束
        System.out.println("程序已结束，按Enter键退出");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s = scanner.nextLine();
            if (s != null) {
                break;
            }
        }
    }

    /**
     * 输出标题
     */
    public static void printTitle() {
        System.out.println("\n" +
                "  _____            _     _      _____            _     _      \n" +
                " |  __ \\          | |   (_)    |  __ \\          | |   (_)     \n" +
                " | |  | | __ _ ___| |__  _  ___| |  | | __ _ ___| |__  _  ___ \n" +
                " | |  | |/ _` / __| '_ \\| |/ _ \\ |  | |/ _` / __| '_ \\| |/ _ \\\n" +
                " | |__| | (_| \\__ \\ | | | |  __/ |__| | (_| \\__ \\ | | | |  __/\n" +
                " |_____/ \\__,_|___/_| |_|_|\\___|_____/ \\__,_|___/_| |_|_|\\___|\n" +
                "                                                              ");
        System.out.println("\n" +
                "    ███████╗    ██████╗  ██████╗ ██╗    ██╗███╗   ██╗\n" +
                "    ██╔════╝    ██╔══██╗██╔═══██╗██║    ██║████╗  ██║\n" +
                "    █████╗█████╗██║  ██║██║   ██║██║ █╗ ██║██╔██╗ ██║\n" +
                "    ██╔══╝╚════╝██║  ██║██║   ██║██║███╗██║██║╚██╗██║\n" +
                "    ███████╗    ██████╔╝╚██████╔╝╚███╔███╔╝██║ ╚████║\n" +
                "    ╚══════╝    ╚═════╝  ╚═════╝  ╚══╝╚══╝ ╚═╝  ╚═══╝\n" +
                "                                                   " + VERSION);
    }

    /**
     * 单次调取API并下载文件
     * @param address API地址
     * @param limit API查询参数-页码大小
     * @param tags API查询参数-标签
     * @param page API查询参数-页码
     * @param outputPath 文件输出路径
     * @return 成功下载的文件数
     * @throws Exception 异常
     */
    public static int handleDownload(String address, String limit, String tags, String page, String outputPath) throws Exception {
        int totalCount = 0;
        // 调取API并下载文件
        JSONObject response = HttpClient.get(address, limit, tags, page);
        List<FileInfo> fileList = JsonUtil.jsonAnalyse(response);
        if (fileList == null || fileList.isEmpty()) return totalCount;
        for (int i = 0; i < fileList.size(); i++) {
            cmdCls();
            printTitle();
            System.out.println("【模式】 单次调用API");
            FileInfo info = fileList.get(i);
            printDownloadingInfo(fileList, i, outputPath);
            HttpClient.download(info.getUrl(), outputPath + "/" + info.genFileName());
            ++totalCount;
        }
        System.out.println(" * APPLICATION END");
        return totalCount;
    }

    /**
     * 连续全量调取API并下载文件
     * @param address API地址
     * @param limit API查询参数-页码大小
     * @param tags API查询参数-标签
     * @param outputPath 文件输出路径
     * @return 成功下载的文件数
     * @throws IOException 异常
     * @throws InterruptedException 异常
     */
    public static int handleDownload(String address, String limit, String tags, String outputPath) throws IOException, InterruptedException {
        int totalCount = 0;
        // 调取API并下载图片
        int page = 1;
        Long sleepMillis = null;
        while (true) {
            Date start = new Date();
            List<FileInfo> fileList;
            try {
                JSONObject response = HttpClient.get(sleepMillis, address, limit, tags, String.valueOf(page));
                fileList = JsonUtil.jsonAnalyse(response);
                if (fileList == null || fileList.isEmpty()) break;
            } catch (Exception e) {
                break;
            }
            for (int i = 0; i < fileList.size(); i++) {
                handleDownloadMulti(page, fileList, i, outputPath, DOWNLOAD_ALL);
                ++totalCount;
            }
            page++;
            Date end = new Date();
            // 至少等待1秒后再调取API
            long diff = end.getTime() - start.getTime();
            sleepMillis = (diff < 1000) ? (1000 - diff) : null;
        }
        System.out.println(" * APPLICATION END");
        return totalCount;
    }

    /**
     * 连续增量调取API并下载文件
     * @param address API地址
     * @param limit API查询参数-页码大小
     * @param tags API查询参数-标签
     * @param outputPath 文件输出路径
     * @param records 增量下载记录
     * @return 成功下载的文件数
     * @throws IOException 异常
     * @throws InterruptedException 异常
     */
    public static int handleDownload(String address, String limit, String tags, String outputPath, Map<String, Long> records) throws IOException, InterruptedException {
        int totalCount = 0;
        String key = TextUtil.genKey(address, tags);
        // 获取对应时间戳
        Long record = records.get(key);
        boolean noRecord = record == null;
        int page = 1;
        Long sleepMillis = null;
        // 增量下载
        while (true) {
            Date start = new Date();
            List<FileInfo> fileList;
            try {
                JSONObject response = HttpClient.get(sleepMillis, address, limit, tags, String.valueOf(page));
                fileList = noRecord ? JsonUtil.jsonAnalyse(response) : JsonUtil.jsonAnalyse(response, record);
                if (fileList == null || fileList.isEmpty()) break;
                // 预更新最新日期
                if (page == 1) {
                    records.put(key, JsonUtil.getLatestCreatedTime(response));
                }
            } catch (Exception e) {
                break;
            }
            for (int i = 0; i < fileList.size(); i++) {
                handleDownloadMulti(page, fileList, i, outputPath, DOWNLOAD_NEW);
                ++totalCount;
            }
            page++;
            Date end = new Date();
            // 至少等待1秒后再调取API
            long diff = end.getTime() - start.getTime();
            sleepMillis = (diff < 1000) ? (1000 - diff) : null;
        }
        // 记录新的时间
        if (noRecord) {
            // 原先无相关记录时，追加更新
            TextUtil.writeLine(TextUtil.getRecordsFilePath(), key + TextUtil.SPLITTER + records.get(key) + "\n");
        } else {
            // 原先有相关记录时，覆写
            TextUtil.coverWriteLines(TextUtil.getRecordsFilePath(), records);
        }
        System.out.println(" * APPLICATION END");
        return totalCount;
    }

    /**
     * 连续下载文件
     * @param page 页码
     * @param fileList 要下载的文件列表
     * @param index 文件列表索引
     * @param outputPath 输出路径
     * @param strategy 下载策略
     * @throws IOException 异常
     * @throws InterruptedException 异常
     */
    public static void handleDownloadMulti(int page, List<FileInfo> fileList, int index, String outputPath, int strategy) throws IOException, InterruptedException {
        cmdCls();
        printTitle();
        System.out.println("【模式】 连续调用API / " + STRATEGY_DOWNLOAD[strategy]);
        System.out.println(" * START DOWNLOADING PAGE #" + page + " ...");
        FileInfo info = fileList.get(index);
        printDownloadingInfo(fileList, index, outputPath);
        // 下载
        HttpClient.download(info.getUrl(), outputPath + "/" + info.genFileName());
    }

    /**
     * 输出下载信息
     * @param list 要下载的文件列表
     * @param index 文件列表索引
     * @param outputPath 输出路径
     */
    public static void printDownloadingInfo(List<FileInfo> list, int index, String outputPath) {
        FileInfo info = list.get(index);
        System.out.println(" * NOW DOWNLOADING [" + info.getId() + "] " + info.getUrl());
        System.out.println(" *     TO " + outputPath + "/" + info.genFileName());
        int len = list.size();
        int maxLen = String.valueOf(len).length();
        System.out.println("   " + getProgressBar((double) (index + 1) / len) + "  " + getFixedLengthString(String.valueOf(index + 1), maxLen, ALIGN_RIGHT) + " / " + len);
    }
}
