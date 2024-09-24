package com.dashie;

import com.alibaba.fastjson2.JSONObject;
import com.dashie.client.HttpClient;
import com.dashie.entity.ConfigProperties;
import com.dashie.entity.FileInfo;
import com.dashie.entity.Records;
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
    public static final String VERSION = "V0.2.1 alpha";

    public static void main(String[] args) throws Exception {
        printTitle();
        int downloadedCount = 0;
        try {
            // 获取配置文件并检查完整性
            ConfigProperties config = new ConfigProperties();
            Thread.sleep(1000);

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
                    Map<String, Long> records = TextUtil.readLines(TextUtil.getRecordsFilePath());   // 获取记录
                    downloadedCount = handleDownload(config.getAddress(), config.getLimit(), config.getTags(), config.getOutputPath(), records);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: \n");
            e.printStackTrace(System.err);
        }
        System.out.println(" * DOWNLOADED: " + downloadedCount);
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

    public static int handleDownload(String address, String limit, String tags, String page, String outputPath) throws Exception {
        int totalCount = 0;
        // 调取API并下载图片
        JSONObject response = HttpClient.get(address, limit, tags, page);
        List<FileInfo> fileList = JsonUtil.jsonAnalyse(response);
        if (fileList == null || fileList.isEmpty()) return totalCount;
        for (int i = 0; i < fileList.size(); i++) {
            cmdCls();
            printTitle();
            System.out.println("【模式】单次调用API");
            FileInfo info = fileList.get(i);
            printDownloadingInfo(fileList, i, outputPath);
            HttpClient.download(info.getUrl(), outputPath + "/" + info.genFileName());
            ++totalCount;
        }
        System.out.println(" * APPLICATION END");
        return totalCount;
    }

    public static int handleDownload(String address, String limit, String tags, String outputPath) throws IOException, InterruptedException {
        int totalCount = 0;
        // 调取API并下载图片
        int page = 1;
        while (true) {
            Date start = new Date();
            List<FileInfo> fileList;
            try {
                JSONObject response = HttpClient.get(address, limit, tags, String.valueOf(page));
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
            if (diff < 1000) {
                Thread.sleep(1000 - diff);
            }
        }
        System.out.println(" * APPLICATION END");
        return totalCount;
    }

    public static int handleDownload(String address, String limit, String tags, String outputPath, Map<String, Long> records) throws IOException, InterruptedException {
        int totalCount = 0;
        String key = TextUtil.genKey(address, tags);
        // 获取对应时间戳
        Long record = records.get(key);
        boolean noRecord = record == null;
        int page = 1;
        // 增量下载
        while (true) {
            Date start = new Date();
            List<FileInfo> fileList;
            try {
                JSONObject response = HttpClient.get(address, limit, tags, String.valueOf(page));
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
            if (diff < 1000) {
                Thread.sleep(1000 - diff);
            }
        }
        // 记录新的时间
        if (noRecord) {
            // 原先无相关记录时，追加更新
            TextUtil.writeLine(TextUtil.getRecordsFilePath(), String.valueOf(records.get(key)));
        } else {
            // 原先有相关记录时，覆写
            TextUtil.coverWriteLines(TextUtil.getRecordsFilePath(), records);
        }
        System.out.println(" * APPLICATION END");
        return totalCount;
    }

    public static void handleDownloadMulti(int page, List<FileInfo> fileList, int index, String outputPath, int strategy) throws IOException, InterruptedException {
        cmdCls();
        printTitle();
        System.out.println("【模式】连续调用API / " + STRATEGY_DOWNLOAD[strategy]);
        System.out.println(" * START DOWNLOADING PAGE #" + page + " ...");
        FileInfo info = fileList.get(index);
        printDownloadingInfo(fileList, index, outputPath);
        // 下载
        HttpClient.download(info.getUrl(), outputPath + "/" + info.genFileName());
    }

    public static void printDownloadingInfo(List<FileInfo> list, int index, String outputPath) {
        FileInfo info = list.get(index);
        System.out.println(" * NOW DOWNLOADING [" + info.getId() + "] " + info.getUrl());
        System.out.println(" *     TO " + outputPath + "/" + info.genFileName());
        int len = list.size();
        int maxLen = String.valueOf(len).length();
        System.out.println("   " + getProgressBar((double) (index + 1) / len) + "  " + getFixedLengthString(String.valueOf(index + 1), maxLen, ALIGN_RIGHT) + " / " + len);
    }
}
