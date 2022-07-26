package com.baisha.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

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
}
