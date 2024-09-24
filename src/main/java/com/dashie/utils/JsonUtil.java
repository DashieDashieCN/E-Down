package com.dashie.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.dashie.entity.FileInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonUtil {
    public static List<FileInfo> jsonAnalyse(JSONObject json) {
        try {
            List<FileInfo> urlList = new ArrayList<>();
            JSONArray posts = (JSONArray) json.get("posts");
            if (posts.isEmpty()) {
                System.out.println(" * HIT BOTTOM");
                return urlList;
            }
            System.out.println(" * SIZE: " + posts.size());
            System.out.println(" * Getting URL...");
            for (Object object : posts) {
                JSONObject jsonObject = (JSONObject) object;
                Integer id = jsonObject.getInteger("id");
                String createdAt = jsonObject.getString("created_at");
                String updatedAt = jsonObject.getString("updated_at");
                JSONObject file = jsonObject.getJSONObject("file");
                String url = file.getString("url");
                System.out.println(" * [" + id + "] " + url);
                urlList.add(new FileInfo(id, url, createdAt, updatedAt));
            }
            return urlList;
        } catch (Exception e) {
            System.out.println("END: 无结果或出错\n");
            e.printStackTrace(System.err);
            return null;
        }
    }

    public static List<FileInfo> jsonAnalyse(JSONObject json, Long timestamp) {
        try {
            List<FileInfo> urlList = new ArrayList<>();
            JSONArray posts = (JSONArray) json.get("posts");
            System.out.println(" * SIZE: " + posts.size());
            if (posts.isEmpty()) {
                System.out.println(" * HIT BOTTOM");
            }
            System.out.println(" * Getting URL...");
            for (Object object : posts) {
                JSONObject jsonObject = (JSONObject) object;
                Integer id = jsonObject.getInteger("id");
                String createdAt = jsonObject.getString("created_at");
                String updatedAt = jsonObject.getString("updated_at");
                JSONObject file = jsonObject.getJSONObject("file");
                String url = file.getString("url");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                Date date = sdf.parse(createdAt);
                // 加入比记录日期更新的对象
                if (date.getTime() > timestamp) {
                    System.out.println(" * [" + id + "] " + url);
                    urlList.add(new FileInfo(id, url, createdAt, updatedAt));
                }
            }
            return urlList;
        } catch (Exception e) {
            System.out.println("END: 无结果或出错\n");
            e.printStackTrace(System.err);
            return null;
        }
    }

    public static Long getLatestCreatedTime(JSONObject json) throws Exception {
        JSONArray posts = (JSONArray) json.get("posts");
        try {
            String createdAt = ((JSONObject) posts.get(0)).getString("created_at");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Date date = sdf.parse(createdAt);
            return date.getTime();
        } catch (Exception e) {
            throw new Exception("获取最新日期时出错", e.getCause());
        }
    }
}
