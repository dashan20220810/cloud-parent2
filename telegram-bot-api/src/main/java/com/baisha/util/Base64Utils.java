package com.baisha.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class Base64Utils {

    /**
     * 图片url 转化为File
     * @param url
     * @return File
     */
    public static File urlToFile(URL url) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            File file = File.createTempFile("tmp", null);
            URLConnection urlConn = url.openConnection();
            is = urlConn.getInputStream();
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            return file;
        } catch (IOException e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * MP4视频url 转化为File
     * @param url
     * @return File
     */
    public static File videoToFile(URL url, String suffix) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            File file = File.createTempFile("tmp", suffix);
            URLConnection urlConn = url.openConnection();
            is = urlConn.getInputStream();
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            return file;
        } catch (IOException e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
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
