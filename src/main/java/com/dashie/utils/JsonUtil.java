package com.dashie.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.dashie.entity.FileInfo;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    public static List<FileInfo> jsonAnalyse(JSONObject json) {
        try {
            List<FileInfo> urlList = new ArrayList<>();
            JSONArray posts = (JSONArray) json.get("posts");
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
            System.out.println("END: 无结果\n" + e);
            return null;
        }
    }
}
