package com.baisha.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Base64Utils {

    /**
     * url资源转化为file流
     * @param url
     * @return
     */
    public static File urlToFile(URL url) {
        InputStream is = null;
        File file = null;
        FileOutputStream fos = null;
        try {
            file = File.createTempFile("tmp", null);
            URLConnection urlConn = null;
            urlConn = url.openConnection();
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
}
