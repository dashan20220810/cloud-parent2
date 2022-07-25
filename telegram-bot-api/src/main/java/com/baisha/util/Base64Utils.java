package com.baisha.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class Base64Utils {

    /**
     * 图片 URL转化为InputStream
     * @param url
     * @return InputStream
     */
    public static InputStream picUrlToStream(URL url) {
        try {
            URLConnection urlConn = url.openConnection();
            return urlConn.getInputStream();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * MP4视频 URL转化为InputStream
     * @param url
     * @return InputStream
     */
    public static InputStream videoUrlToStream(URL url) {
        try {
            URLConnection urlConn = url.openConnection();
            return urlConn.getInputStream();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Base64 转化为File
     * @param base64Str
     * @return File
     */
    public static File base64ToFile(String base64Str) {
        if (base64Str.contains("data:image")) {
            base64Str = base64Str.substring(base64Str.indexOf(",") + 1);
        }
        base64Str = base64Str.replace("\r\n", "");

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            File file = File.createTempFile("tmp", null);
            byte[] base64Data = Base64.getDecoder().decode(base64Str);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(base64Data);
            return file;
        } catch (Exception e) {
            return null;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
