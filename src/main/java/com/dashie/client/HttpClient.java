package com.dashie.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpClient {

    public static JSONObject get(String address) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://" + address + "/posts.json");
        // 设置请求头
        setHeader(httpGet);
        // 发送并接收请求
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return JSON.parseObject(result);
        }
    }

    public static JSONObject get(String address, String limit, String tags, String page) throws Exception {
        if (limit.isEmpty() && tags.isEmpty() && page.isEmpty()) {
            return get(address);
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        URIBuilder uriBuilder = new URIBuilder("https://" + address + "/posts.json");
        // 设置请求体
        if (!limit.trim().isEmpty())    uriBuilder.setParameter("limit", limit);
        if (!tags.trim().isEmpty())     uriBuilder.setParameter("tags", tags);
        if (!page.trim().isEmpty())     uriBuilder.setParameter("page", page);
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        // 设置请求头
        setHeader(httpGet);
        // 发送并接收请求
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return JSON.parseObject(result);
        }
    }

    public static void setHeader(HttpGet httpGet) {
        httpGet.setHeader("login", "hexerade");
        httpGet.setHeader("api_key", "1nHrmzmsvJf26EhU1F7CjnjC");
    }

    public static void download(String url, String path) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            inputStream.close();
            fileOutputStream.close();
//            System.out.println(" * DOWNLOADED: " + path);
        } catch (IOException e) {
        System.out.println("ERROR: 文件下载失败\n" + e);
        }
    }
}