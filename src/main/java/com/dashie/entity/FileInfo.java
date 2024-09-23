package com.dashie.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
public class FileInfo {
    private Integer id;
    private String url;
    private long createdAt;
    private long updatedAt;

    public FileInfo(Integer id, String url, String createdAt, String updatedAt) throws ParseException {
        this.id = id;
        this.url = url;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        Date date = sdf.parse(createdAt);
        this.createdAt = date.getTime();
        date = sdf.parse(updatedAt);
        this.updatedAt = date.getTime();
    }
    public String genFileName() {
        String[] splitUrl = url.split("/");
        String fileName = splitUrl[splitUrl.length - 1];
        return id + "_" + fileName;
    }
}
