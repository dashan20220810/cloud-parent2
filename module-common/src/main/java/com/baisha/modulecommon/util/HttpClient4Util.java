package com.baisha.modulecommon.util;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baisha.modulecommon.vo.HttpResultDTO;
import com.beust.jcommander.internal.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class HttpClient4Util {
    public static String get(String url) throws Exception {
        log.info("get请求参数{}",url);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(35000).setConnectionRequestTimeout(60000)
                .setSocketTimeout(60000).build();
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();//得到请求回来的数据
        String s = EntityUtils.toString(entity, "UTF-8");
        log.info("get请求返回参数{}",s);
        return s;
    }


    public static String specialGet(String urlStr) throws Exception {
        log.info("specialGet请求参数{}", urlStr);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            httpClient = HttpClients.createDefault();
            URL url = new URL(urlStr);
            URI uri = new URI("https", url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), null);
            HttpGet httpGet = new HttpGet(uri);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(35000).setConnectionRequestTimeout(35000)
                    .setSocketTimeout(60000).build();
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();//得到请求回来的数据
            result = EntityUtils.toString(entity, "UTF-8");
            log.info("specialGet请求返回参数{}", result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String doGet(String url) {
        log.info("doGet请求参数{}",url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头信息，鉴权
//            httpGet.setHeader("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            httpGet.setHeader("Accept-Encoding", "gzip,deflate");
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(60000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity);
            log.info("doGet请求返回参数{}",result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            log.error("http远程请求异常,msg={}",e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    
    public static String doPost(String url, String json) {
        log.info("doPost请求路径,url:{},{}",url, json);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(60000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/json");

        // 为httpPost设置封装好的请求参数
        try {
            HttpEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
            httpPost.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
            log.info("doPost请求返回参数{}",result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            log.error("http远程请求异常,msg={}",e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String doPost(String url, Map<String, Object> paramMap) {
        log.info("doPost请求路径,url:{},{}",url, JSONObject.toJSONString(paramMap));
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(60000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        // 封装post请求参数
        if (null != paramMap && paramMap.size() > 0) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            // 通过map集成entrySet方法获取entity
            Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
            // 循环遍历，获取迭代器
            Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> mapEntry = iterator.next();
                nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue()==null?null:mapEntry.getValue().toString()));
            }

            // 为httpPost设置封装好的请求参数
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
            log.info("doPost请求返回参数{}",result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            log.error("http远程请求异常,msg={}",e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String postMultipartFile(
            String url, MultipartFile multipartFile) throws IOException {
        log.info("doPost请求路径,url:{}",url);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);// 创建httpPost
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        String fileName;
        fileName = multipartFile.getOriginalFilename();
        builder.addBinaryBody("file", multipartFile.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
//        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
//        for (Map.Entry<String, Object> entry : params.entrySet()) {
//            if (entry.getValue() == null)
//                continue;
//            // 类似浏览器表单提交，对应input的name和value
//            builder.addTextBody(entry.getKey(), entry.getValue().toString(), contentType);
//        }
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {

            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            } else {
                return null;
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
