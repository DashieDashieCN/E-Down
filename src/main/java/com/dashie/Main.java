package com.dashie;

import com.alibaba.fastjson2.JSONObject;
import com.dashie.client.HttpClient;
import com.dashie.entity.ConfigProperties;
import com.dashie.entity.FileInfo;
import com.dashie.utils.JsonUtil;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static com.dashie.entity.ConfigProperties.GET_MULTI;
import static com.dashie.entity.ConfigProperties.GET_ONCE;
import static com.dashie.utils.ScreenPrintUtil.*;

public class Main {
    public static void main(String[] args) throws Exception {
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
                "                                                 ");
        try {
            // 获取配置文件并检查完整性
            ConfigProperties config = new ConfigProperties();
            Thread.sleep(1000);
            // 调取API并下载图片
            if (config.getStrategyOfApi() == GET_ONCE) {
                handleDownload(config.getAddress(), config.getLimit(), config.getTags(), config.getPage(), config.getOutputPath());
            } else if (config.getStrategyOfApi() == GET_MULTI) {
                handleDownload(config.getAddress(), config.getLimit(), config.getTags(), config.getOutputPath());
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
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

    public static void handleDownload(String address, String limit, String tags, String page, String outputPath) throws Exception {
        // 调取API并下载图片
        JSONObject response = HttpClient.get(address, limit, tags, page);
        List<FileInfo> fileList = JsonUtil.jsonAnalyse(response);
        if (fileList == null || fileList.isEmpty()) return;
        cmdCls();
        for (int i = 0; i < fileList.size(); i++) {
            FileInfo info = fileList.get(i);
            printDownloadingInfo(fileList, i, outputPath);
            HttpClient.download(info.getUrl(), outputPath + "/" + info.genFileName());
        }
        System.out.println(" * APPLICATION END");
    }

    public static void handleDownload(String address, String limit, String tags, String outputPath) throws IOException, InterruptedException {
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
                cmdCls();
                System.out.println(" * START DOWNLOADING PAGE #" + page + " ...");
                FileInfo info = fileList.get(i);
                printDownloadingInfo(fileList, i, outputPath);
                // 下载
                HttpClient.download(info.getUrl(), outputPath + "/" + info.genFileName());
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
